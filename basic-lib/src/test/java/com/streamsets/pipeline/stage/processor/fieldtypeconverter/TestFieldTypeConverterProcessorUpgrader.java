/*
 * Copyright 2021 StreamSets Inc.
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
package com.streamsets.pipeline.stage.processor.fieldtypeconverter;

import com.streamsets.pipeline.api.Config;
import com.streamsets.pipeline.api.StageUpgrader;
import com.streamsets.pipeline.config.upgrade.UpgraderTestUtils;
import com.streamsets.pipeline.config.upgrade.UpgraderUtils;
import com.streamsets.pipeline.stage.origin.remote.RemoteDownloadSourceUpgrader;
import com.streamsets.pipeline.upgrader.SelectorStageUpgrader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TestFieldTypeConverterProcessorUpgrader {

  private StageUpgrader upgrader;
  private List<Config> configs;
  private StageUpgrader.Context context;

  @Before
  public void setUp() {
    URL yamlResource =
        ClassLoader.getSystemClassLoader().getResource("upgrader/FieldTypeConverterDProcessor.yaml");
    upgrader = new SelectorStageUpgrader("stage", new RemoteDownloadSourceUpgrader(), yamlResource);
    configs = new ArrayList<>();
    context = Mockito.mock(StageUpgrader.Context.class);
  }

  @Test
  public void testUpgradeV2ToV3() throws Exception {
    Mockito.doReturn(2).when(context).getFromVersion();
    Mockito.doReturn(3).when(context).getToVersion();

    List<Map<String, Object>> fieldTypeConverterConfigs = new ArrayList<>();
    fieldTypeConverterConfigs.add(Collections.emptyMap());
    configs.add(new Config("fieldTypeConverterConfigs", fieldTypeConverterConfigs));

    List<Map<String, Object>> wholeTypeConverterConfigs = new ArrayList<>();
    wholeTypeConverterConfigs.add(Collections.emptyMap());
    configs.add(new Config("wholeTypeConverterConfigs", wholeTypeConverterConfigs));

    configs = upgrader.upgrade(configs, context);

    UpgraderTestUtils.assertExists(configs, "fieldTypeConverterConfigs");
    Config fieldTypeConverterConfigsConfig = UpgraderUtils.getConfigWithName(configs, "fieldTypeConverterConfigs");
    Map<String, Object> fieldTypeConverterConfig = ((List<Map<String, Object>>) fieldTypeConverterConfigsConfig.getValue()).get(0);
    Assert.assertTrue(fieldTypeConverterConfig.containsKey("inputFieldEmpty"));
    Assert.assertEquals(fieldTypeConverterConfig.get("inputFieldEmpty"), "ERROR");

    UpgraderTestUtils.assertExists(configs, "wholeTypeConverterConfigs");
    Config wholeTypeConverterConfigsConfig = UpgraderUtils.getConfigWithName(configs, "wholeTypeConverterConfigs");
    Map<String, Object> wholeTypeConverterConfig = ((List<Map<String, Object>>) wholeTypeConverterConfigsConfig.getValue()).get(0);
    Assert.assertTrue(wholeTypeConverterConfig.containsKey("inputFieldEmpty"));
    Assert.assertEquals(wholeTypeConverterConfig.get("inputFieldEmpty"), "ERROR");

  }

}
