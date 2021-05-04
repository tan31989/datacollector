/*
 * Copyright 2017 StreamSets Inc.
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
package com.streamsets.datacollector.main;


import com.streamsets.datacollector.antennadoctor.AntennaDoctor;
import com.streamsets.datacollector.aster.EntitlementSyncTask;
import com.streamsets.datacollector.blobstore.BlobStoreTask;
import com.streamsets.datacollector.bundles.BundleContext;
import com.streamsets.datacollector.bundles.SupportBundle;
import com.streamsets.datacollector.bundles.SupportBundleManager;
import com.streamsets.datacollector.credential.CredentialStoresTask;
import com.streamsets.datacollector.event.handler.EventHandlerTask;
import com.streamsets.datacollector.execution.Manager;
import com.streamsets.datacollector.http.DataCollectorWebServerTask;
import com.streamsets.datacollector.http.WebServerTask;
import com.streamsets.datacollector.lineage.LineagePublisherTask;
import com.streamsets.datacollector.stagelibrary.StageLibraryTask;
import com.streamsets.datacollector.store.PipelineStoreTask;

import com.streamsets.datacollector.usagestats.StatsCollector;
import com.streamsets.pipeline.api.lineage.LineagePublisher;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class TestPipelineTask {

  @Test
  public void testSubTasksOrderingInPipelineTask() {
    StageLibraryTask library = Mockito.mock(StageLibraryTask.class);
    PipelineStoreTask store = Mockito.mock(PipelineStoreTask.class);
    DataCollectorWebServerTask webServer = Mockito.mock(DataCollectorWebServerTask.class);
    Manager pipelineManager = Mockito.mock(Manager.class);
    EventHandlerTask eventHandlerTask = Mockito.mock(EventHandlerTask.class);
    LineagePublisherTask lineagePublisherTask = Mockito.mock(LineagePublisherTask.class);
    SupportBundleManager supportBundleTask = Mockito.mock(SupportBundleManager.class);
    BlobStoreTask blobStoreTask = Mockito.mock(BlobStoreTask.class);
    CredentialStoresTask credentialStoresTask = Mockito.mock(CredentialStoresTask.class);
    StatsCollector statsCollectorTask = Mockito.mock(StatsCollector.class);
    AntennaDoctor antennaDoctor = Mockito.mock(AntennaDoctor.class);
    EntitlementSyncTask entitlementSyncTask = Mockito.mock(EntitlementSyncTask.class);
    PipelineTask task = new PipelineTask(library, store, pipelineManager, webServer, eventHandlerTask,lineagePublisherTask,
        supportBundleTask, blobStoreTask, credentialStoresTask, statsCollectorTask, antennaDoctor, entitlementSyncTask);
    Assert.assertEquals(12, task.getSubTasks().size());
    Assert.assertTrue(task.getSubTasks().get(0) instanceof StageLibraryTask);
    Assert.assertTrue(task.getSubTasks().get(1) instanceof LineagePublisherTask);
    Assert.assertTrue(task.getSubTasks().get(2) instanceof CredentialStoresTask);
    Assert.assertTrue(task.getSubTasks().get(3) instanceof BlobStoreTask);
    Assert.assertTrue(task.getSubTasks().get(4) instanceof PipelineStoreTask);
    Assert.assertTrue(task.getSubTasks().get(5) instanceof WebServerTask);
    Assert.assertTrue(task.getSubTasks().get(6) instanceof EventHandlerTask);
    Assert.assertTrue(task.getSubTasks().get(7) instanceof Manager);
    Assert.assertTrue(task.getSubTasks().get(8) instanceof SupportBundleManager);
    Assert.assertTrue(task.getSubTasks().get(9) instanceof StatsCollector);
    Assert.assertTrue(task.getSubTasks().get(10) instanceof AntennaDoctor);
    Assert.assertTrue(task.getSubTasks().get(11) instanceof EntitlementSyncTask);
  }

}
