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

import com.streamsets.pipeline.api.Label;

public enum TimeoutType {
  UNKNOWN(null, "Unknown timeout reason"),
  CONNECTION("connect timed out", "Connection timeout"),
  READ("Read timed out", "Read timeout"),
  REQUEST("Timeout waiting for task", "Request timeout"),
  RECORD("Timeout waiting for record response", "Record processing timeout"),
  NONE(null, null);

  private final String message;
  private final String reason;

  TimeoutType(String message, String reason) {
    this.message = message;
    this.reason = reason;
  }

  public String getMessage() {
    return this.message;
  }

  public String getReason() {
    return this.reason;
  }
}
