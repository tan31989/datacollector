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
package com.streamsets.pipeline.lib.jdbc.parser.sql;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfig;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

import java.util.BitSet;

/*
  Main ideas taken from org.antlr.v4.runtime.DiagnosticErrorListener
 */
public class SqlParserErrorListener extends BaseErrorListener {

  protected final boolean exactOnly;

  public SqlParserErrorListener() {
    this(true);
  }

  public SqlParserErrorListener(boolean exactOnly) {
    this.exactOnly = exactOnly;
  }

  @Override
  public void syntaxError(
      Recognizer<?, ?> recognizer,
      Object offendingSymbol,
      int line,
      int charPositionInLine,
      String msg,
      RecognitionException e)
  {
    String message = "line " + line + ":" + charPositionInLine + " " + msg;
    throw new SQLParseException("syntax error", message);
  }

  @Override
  public void reportAmbiguity(
      Parser recognizer,
      DFA dfa,
      int startIndex,
      int stopIndex,
      boolean exact,
      BitSet ambigAlts,
      ATNConfigSet configs)
  {
  }

  @Override
  public void reportAttemptingFullContext(
      Parser recognizer,
      DFA dfa,
      int startIndex,
      int stopIndex,
      BitSet conflictingAlts,
      ATNConfigSet configs)
  {
  }

  @Override
  public void reportContextSensitivity(
      Parser recognizer,
      DFA dfa,
      int startIndex,
      int stopIndex,
      int prediction,
      ATNConfigSet configs)
  {
  }

  protected String getDecisionDescription(Parser recognizer, DFA dfa) {
    int decision = dfa.decision;
    int ruleIndex = dfa.atnStartState.ruleIndex;

    String[] ruleNames = recognizer.getRuleNames();
    if (ruleIndex < 0 || ruleIndex >= ruleNames.length) {
      return String.valueOf(decision);
    }

    String ruleName = ruleNames[ruleIndex];
    if (ruleName == null || ruleName.isEmpty()) {
      return String.valueOf(decision);
    }
    return String.format("%d (%s)", decision, ruleName);
  }

  protected BitSet getConflictingAlts(BitSet reportedAlts, ATNConfigSet configs) {
    if (reportedAlts != null) {
      return reportedAlts;
    }

    BitSet result = new BitSet();
    for (ATNConfig config : configs) {
      result.set(config.alt);
    }
    return result;
  }
}
