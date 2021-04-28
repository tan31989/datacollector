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
package com.streamsets.pipeline.stage.util.http;

import com.streamsets.pipeline.lib.http.Errors;
import com.streamsets.pipeline.stage.origin.http.ResponseAction;

public class PassthroughAttributes {
  public static final String HEADER_ERROR = "httpClientError";
  public static final String HEADER_STATUS = "httpClientStatus";
  public static final String HEADER_ACTION = "httpClientLastAction";
  public static final String HEADER_TIMEOUT_TYPE = "httpClientTimeoutType";
  public static final String HEADER_RETRIES = "httpClientRetries";

  private Errors error;
  private int status = -1;
  private ResponseAction action;
  private TimeoutType timeoutType;
  private int retries = -1;
  private boolean sendToError = false;
  private boolean sendToOutput = false;

  public Errors getError() {
    return this.error;
  }

  public void setError(Errors error) {
    this.error = error;
  }

  public ResponseAction getAction() { return this.action; }

  public TimeoutType getTimeoutType() { return this.timeoutType; }

  public void setTimeoutType(TimeoutType timeoutType) { this.timeoutType = timeoutType; }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public void setAction(ResponseAction action) {
    this.action = action;
  }

  public void setRetries(int retries) { this.retries = retries; }

  public int getRetries() {
    return this.retries;
  }

  public boolean isSendToError() {
    return this.sendToError;
  }

  public void setSendToError(boolean sendToError) {
    this.sendToError = sendToError;
  }

  public boolean isSendToOutput() {
    return this.sendToOutput;
  }

  public void setSendToOutput(boolean sendToOutput) {
    this.sendToOutput = sendToOutput;
  }
}
