/*
 * Copyright 2018 StreamSets Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.streamsets.pipeline.stage.origin.hdfs;

import com.google.common.base.Throwables;
import com.streamsets.pipeline.api.BatchContext;
import com.streamsets.pipeline.api.BatchMaker;
import com.streamsets.pipeline.api.ErrorCode;
import com.streamsets.pipeline.api.PushSource;
import com.streamsets.pipeline.api.Record;
import com.streamsets.pipeline.api.StageException;
import com.streamsets.pipeline.api.ToErrorContext;
import com.streamsets.pipeline.lib.parser.DataParser;
import com.streamsets.pipeline.lib.parser.DataParserException;
import com.streamsets.pipeline.lib.parser.DataParserFactory;
import com.streamsets.pipeline.stage.common.DefaultErrorRecordHandler;
import com.streamsets.pipeline.stage.common.ErrorRecordHandler;
import com.streamsets.pipeline.lib.hdfs.common.Errors;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HdfsSourceRunnable implements Runnable {
  private static final Logger LOG = LoggerFactory.getLogger(HdfsSourceRunnable.class);
  public static final String PREFIX = "Hadoop FS Thread - ";
  private static final String THREAD_NAME = "Thread Name";
  private static final String STATUS = "Status";
  private static final String OFFSET = "Current Offset";
  private static final String CURRENT_FILE = "Current File";

  private final int threadNumber;
  private final int batchSize;
  private final PushSource.Context context;
  private final FileSystem fs;
  private final HdfsDirectorySpooler spooler;
  private final Map<String, String> offsetMap;
  private final HdfsSourceConfigBean hdfsSourceConfigBean;
  private final ErrorRecordHandler errorRecordHandler;
  private DataParserFactory parserFactory;
  private DataParser parser;
  private final Map<String, Object> gaugeMap;
  private boolean noMoreData = false;

  private enum Status {
    SPOOLING,
    READING,
    GENERATING_BATCH,
    BATCH_GENERATED,
    ;
  }

  public HdfsSourceRunnable(
      int threadNumber,
      int batchSize,
      PushSource.Context context,
      FileSystem fs,
      HdfsDirectorySpooler spooler,
      Map<String, String> offsetMap,
      HdfsSourceConfigBean hdfsSourceConfigBean
  ) {
    this.threadNumber = threadNumber;
    this.batchSize = batchSize;
    this.context = context;
    this.fs = fs;
    this.spooler = spooler;
    this.offsetMap = offsetMap;
    this.hdfsSourceConfigBean = hdfsSourceConfigBean;
    this.parserFactory = hdfsSourceConfigBean.dataFormatConfig.getParserFactory();
    this.errorRecordHandler = new DefaultErrorRecordHandler(context, (ToErrorContext) context);

    // Metrics
    this.gaugeMap = context.createGauge(PREFIX + threadNumber).getValue();
  }

  @Override
  public void run() {
    Thread.currentThread().setName(PREFIX + threadNumber);
    FSDataInputStream inputStream = null;

    initGaugeIfNeeded();
    Path previousFile = null;

    while (!context.isStopped()) {
      BatchContext batchContext = context.startBatch();

      try {
        updateGauge(Status.SPOOLING, null, null);
        Path currentFile = spooler.poolForFile(10, TimeUnit.SECONDS).getPath();
        if (currentFile == null) {
          continue;
        }

        inputStream = fs.open(currentFile);

        noMoreData = false;

        while (!context.isStopped() && !noMoreData) {
          updateGauge(Status.READING, currentFile.getName(), currentFile.getName());
          String offset = generateBatch(
              currentFile.getName(),
              inputStream.getWrappedStream(),
              getOffset(currentFile.getName()),
              batchContext.getBatchMaker()
          );

          updateGauge(Status.GENERATING_BATCH, currentFile.getName(), getOffset(currentFile.getName()));
          context.processBatch(batchContext, currentFile.getName(), offset);
          if (previousFile != null && previousFile != currentFile) {
            context.commitOffset(previousFile.getName(), null);
          }

          updateGauge(Status.BATCH_GENERATED, currentFile.getName(), getOffset(currentFile.getName()));
          previousFile = currentFile;
        }
      } catch (DataParserException | IOException ex) {
        handleStageError(Errors.HADOOPFS_63, ex);
      } catch (InterruptedException ex) {
        LOG.error("Thread '{}' Interrupted: {}", Thread.currentThread().getName(), ex.toString(), ex);
        Thread.currentThread().interrupt();
      } finally {
        try {
          if (inputStream != null) {
            inputStream.close();
          }
        } catch (IOException ex) {
          LOG.error("failed to close file system: {}", ex.toString(), ex);
        }
      }
    }
  }

  private String getOffset(String fileName) {
    String offset = "0";

    if (offsetMap.containsKey(fileName)) {
      offset = offsetMap.get(fileName);
    }

    return offset;
  }

  private String generateBatch(String file, InputStream in, String offset, BatchMaker batchMaker)
      throws DataParserException, IOException {
    switch (hdfsSourceConfigBean.dataFormat) {
      case AVRO:
        parser = parserFactory.getParser(file, offset);
        break;
      default:
        parser = parserFactory.getParser(file, in, offset);
    }

    for (int i = 0; i < batchSize; i++) {
      Record record;
      record = parser.parse();

      if (record == null) {
        noMoreData = true;
      } else {
        batchMaker.addRecord(record);
        offset = parser.getOffset();
      }
    }

    return offset;
  }

  /**
   * Handle Exception
   */
  private void handleStageError(ErrorCode errorCode, Exception e) {
    LOG.error(e.toString(), e);
    try {
      errorRecordHandler.onError(errorCode, e);
    } catch (StageException se) {
      LOG.error("Error when routing to stage error", se);
      //Way to throw stage exception from runnable to main source thread
      Throwables.propagate(se);
    }
  }

  /**
   * Initialize the gauge with needed information
   */
  private void initGaugeIfNeeded() {
    gaugeMap.put(THREAD_NAME, Thread.currentThread().getName());
    gaugeMap.put(STATUS, "");
    gaugeMap.put(CURRENT_FILE, "");
  }

  private void updateGauge(Status status, String currentFile, String offset) {
    gaugeMap.put(STATUS, status.name());
    gaugeMap.put(
        CURRENT_FILE,
        currentFile == null ? "" : currentFile
    );
    gaugeMap.put(
        OFFSET,
        offset == null ? "" : offset
    );
  }
}
