/*
 * Copyright 2020 StreamSets Inc.
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

package com.streamsets.pipeline.lib;

import com.google.common.collect.ImmutableList;
import com.streamsets.datacollector.client.model.MetricRegistryJson;
import com.streamsets.pipeline.api.StageException;
import com.streamsets.pipeline.lib.startJob.StartJobErrors;
import com.streamsets.pipeline.lib.util.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ControlHubApiUtil {

  private static final Logger LOG = LoggerFactory.getLogger(ControlHubApiUtil.class);

  public static void resetOffset(ClientBuilder clientBuilder, String baseUrl, String jobId) {
    String resetOffsetUrl = baseUrl + "jobrunner/rest/v1/jobs/resetOffset";
    try (Response response = clientBuilder.build()
        .target(resetOffsetUrl)
        .request()
        .post(Entity.json(ImmutableList.of(jobId)))) {
      if (response.getStatus() != Response.Status.OK.getStatusCode()) {
        throw new StageException(
            StartJobErrors.START_JOB_02,
            jobId,
            response.getStatus(),
            response.readEntity(String.class)
        );
      }
    }
  }

  public static Map<String, Object> getJobStatus(
      ClientBuilder clientBuilder,
      String baseUrl,
      String jobId
  ) {
    String jobStatusUrl = baseUrl + "jobrunner/rest/v1/job/" + jobId + "/currentStatus";
    try (Response response = clientBuilder.build()
        .target(jobStatusUrl)
        .request()
        .get()) {
      return (Map<String, Object>)response.readEntity(Map.class);
    }
  }

  public static Map<String, Map<String, Object>> getMultipleJobStatus(
      ClientBuilder clientBuilder,
      String baseUrl,
      List<String> jobInstancesIdList
  ) {
    String jobStatusUrl = baseUrl + "jobrunner/rest/v1/jobs/status";
    try (Response response = clientBuilder.build()
        .target(jobStatusUrl)
        .request()
        .post(Entity.json(jobInstancesIdList))) {
      return (Map<String, Map<String, Object>>)response.readEntity(Map.class);
    }
  }

  /*
  Doing a self-referential call to enter in a wait loop can potentially create a
  StackOverfkowException, and could provoke an unestable JVM due to OutOfMemmoryException.
 */
  public static List<Map<String, Object>> waitForJobCompletion(
      ClientBuilder clientBuilder,
      String baseUrl,
      List<String> jobIdList,
      long waitTime
  ) {
    while (true) {
      ThreadUtil.sleep(waitTime);

      Map<String, Map<String, Object>> jobStatusMap = ControlHubApiUtil.getMultipleJobStatus(
          clientBuilder,
          baseUrl,
          jobIdList
      );
      List<Map<String, Object>> jobStatusList = jobStatusMap
          .keySet()
          .stream()
          .map(jobStatusMap::get)
          .collect(Collectors.toList());

      boolean allDone = true;
      for(Map<String, Object> jobStatus: jobStatusList) {
        String status = jobStatus.containsKey("status") ? (String) jobStatus.get("status") : null;
        allDone &= (Constants.JOB_SUCCESS_STATES.contains(status) || Constants.JOB_ERROR_STATES.contains(status));
      }

      if (allDone) {
        return jobStatusList;
      }
    }
  }

  public static MetricRegistryJson getJobMetrics(
      ClientBuilder clientBuilder,
      String baseUrl,
      String jobId
  ) {
    String jobStartUrl = baseUrl + "jobrunner/rest/v1/metrics/job/" + jobId;
    try (Response response = clientBuilder.build()
        .target(jobStartUrl)
        .request()
        .get()) {
      if (response.getStatus() != Response.Status.OK.getStatusCode()) {
        return null;
      }
      return response.readEntity(MetricRegistryJson.class);
    } catch (Exception ex) {
      LOG.warn("Failed to fetch job metrics: {}", ex.toString(), ex);
      return null;
    }
  }

  public static boolean determineJobSuccess(String status, String statusColor) {
    return (Constants.JOB_SUCCESS_STATES.contains(status) &&
        !Constants.JOB_STATUS_COLOR_RED.equals(statusColor));
  }
}
