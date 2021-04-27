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
package com.streamsets.pipeline.stage.processor.http;

import com.streamsets.pipeline.api.ConfigDef;
import com.streamsets.pipeline.api.ValueChooserModel;
import com.streamsets.pipeline.stage.origin.http.HttpResponseActionConfigBean;
import com.streamsets.pipeline.stage.origin.http.HttpTimeoutOriginResponseActionConfigBean;
import com.streamsets.pipeline.stage.origin.http.ResponseAction;
import com.streamsets.pipeline.stage.origin.http.ResponseActionChooserValues;

public class HttpTimeoutProcessorResponseActionConfigBean extends HttpTimeoutOriginResponseActionConfigBean {

    public HttpTimeoutProcessorResponseActionConfigBean(int maxNumRetries, long backoffInterval, ResponseAction action, boolean passRecord) {
        this.maxNumRetries = maxNumRetries;
        this.backoffInterval = backoffInterval;
        this.action = action;
        this.passRecord = passRecord;
    }

    public HttpTimeoutProcessorResponseActionConfigBean(long backoffInterval, ResponseAction action) {
        this(-1, backoffInterval, action, false);
    }

    @SuppressWarnings("unused")
    public HttpTimeoutProcessorResponseActionConfigBean() {
        // needed for UI
    }

    @ConfigDef(
        required = false,
        type = ConfigDef.Type.BOOLEAN,
        label = "Pass Record",
        description = "Pass record along the pipeline unchanged when all retries fail.",
        defaultValue = "false",
        group = "#0",
        displayPosition = 200,
        displayMode = ConfigDef.DisplayMode.ADVANCED,
        dependsOn = "action",
        triggeredByValue = { "ERROR_RECORD", "RETRY_LINEAR_BACKOFF", "RETRY_EXPONENTIAL_BACKOFF", "RETRY_IMMEDIATELY" }
    )
    public boolean passRecord = false;

    @Override
    public boolean isPassRecord() { return passRecord; }
}
