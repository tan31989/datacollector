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
import com.streamsets.pipeline.stage.origin.http.HttpStatusOriginResponseActionConfigBean;
import com.streamsets.pipeline.stage.origin.http.ResponseAction;
import com.streamsets.pipeline.stage.origin.http.ResponseActionChooserValues;

public class HttpStatusProcessorResponseActionConfigBean extends HttpStatusOriginResponseActionConfigBean {

    public HttpStatusProcessorResponseActionConfigBean(int statusCode, int maxNumRetries, long backoffInterval, ResponseAction action, boolean passRecord) {
        this.statusCode = statusCode;
        this.maxNumRetries = maxNumRetries;
        this.backoffInterval = backoffInterval;
        this.action = action;
        this.passRecord = passRecord;
    }

    @SuppressWarnings("unused")
    public HttpStatusProcessorResponseActionConfigBean() {
        // needed for UI
    }

    @ConfigDef(required = false,
        type = ConfigDef.Type.BOOLEAN,
        label = "Pass Record",
        description = "Pass record along the pipeline unchanged when all retries fail.",
        defaultValue = "false",
        group = "#0",
        displayPosition = 200,
        dependsOn = "action",
        triggeredByValue = { "ERROR_RECORD", "RETRY_LINEAR_BACKOFF", "RETRY_EXPONENTIAL_BACKOFF", "RETRY_IMMEDIATELY" })
    public boolean passRecord = false;

    @Override
    public boolean isPassRecord() {
        return passRecord;
    }
}
