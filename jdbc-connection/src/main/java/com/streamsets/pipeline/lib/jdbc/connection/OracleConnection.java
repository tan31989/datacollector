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

package com.streamsets.pipeline.lib.jdbc.connection;

import com.streamsets.pipeline.api.*;
import com.streamsets.pipeline.lib.jdbc.connection.common.AbstractJdbcConnection;
import com.streamsets.pipeline.lib.jdbc.connection.common.JdbcConnectionGroups;

@InterfaceAudience.LimitedPrivate
@InterfaceStability.Unstable
@ConnectionDef(
    label = "Oracle",
    type = OracleConnection.TYPE,
    description = "Connects to Oracle",
    version = 1,
    upgraderDef = "upgrader/OracleConnectionUpgrader.yaml",
    supportedEngines = { ConnectionEngine.COLLECTOR, ConnectionEngine.TRANSFORMER }
)
@ConfigGroups(JdbcConnectionGroups.class)
public class OracleConnection extends AbstractJdbcConnection {

  public static final String TYPE = "STREAMSETS_ORACLE";

}
