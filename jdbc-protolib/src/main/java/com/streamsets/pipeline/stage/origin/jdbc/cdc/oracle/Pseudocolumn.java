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
package com.streamsets.pipeline.stage.origin.jdbc.cdc.oracle;

public enum Pseudocolumn {

  COLUMN_VALUE("column_value", PseudocolumnType.STANDARD),
  OBJECT_ID("object_id", PseudocolumnType.STANDARD),
  OBJECT_VALUE("object_value", PseudocolumnType.STANDARD),
  ORA_ROWSCN("ora_rowscn", PseudocolumnType.STANDARD),
  ROWID("rowid", PseudocolumnType.STANDARD),
  ROWNUM("rownum", PseudocolumnType.STANDARD),
  XMLDATA("xmldata", PseudocolumnType.STANDARD),
  CONNECT_BY_ISCYCLE("connect_by_iscycle", PseudocolumnType.HIERARCHICAL),
  CONNECT_BY_ISLEAF("connect_by_isleaf", PseudocolumnType.HIERARCHICAL ),
  LEVEL("level", PseudocolumnType.HIERARCHICAL),
  CURRVAL("currval", PseudocolumnType.SEQUENCE),
  NEXTVAL("nextval", PseudocolumnType.SEQUENCE),
  VERSIONS_STARTSCN("versions_startscn", PseudocolumnType.VERSION),
  VERSIONS_STARTTIME("versions_starttime", PseudocolumnType.VERSION),
  VERSIONS_ENDSCN("versions_endscn", PseudocolumnType.VERSION),
  VERSIONS_ENDTIME("versions_endtime", PseudocolumnType.VERSION),
  VERSIONS_XID("versions_xid", PseudocolumnType.VERSION),
  VERSIONS_OPERATION("versions_operation", PseudocolumnType.VERSION);

  private final PseudocolumnType type;
  private final String name;

  Pseudocolumn(String name, PseudocolumnType type) {
    this.name = name;
    this.type = type;
  }

  public String getName() {
    return this.name;
  }

  public PseudocolumnType getType() {
    return this.type;
  }
}
