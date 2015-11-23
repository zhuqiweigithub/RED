/*
 * Copyright 2015 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.rf.ide.core.testdata.model.table.testCases.mapping;

import java.util.List;
import java.util.Stack;

import org.rf.ide.core.testdata.model.FilePosition;
import org.rf.ide.core.testdata.model.RobotFileOutput;
import org.rf.ide.core.testdata.model.table.TestCaseTable;
import org.rf.ide.core.testdata.model.table.mapping.IParsingMapper;
import org.rf.ide.core.testdata.model.table.mapping.ParsingStateHelper;
import org.rf.ide.core.testdata.model.table.testCases.TestCase;
import org.rf.ide.core.testdata.model.table.testCases.TestCaseTimeout;
import org.rf.ide.core.testdata.text.read.IRobotTokenType;
import org.rf.ide.core.testdata.text.read.ParsingState;
import org.rf.ide.core.testdata.text.read.RobotLine;
import org.rf.ide.core.testdata.text.read.recognizer.RobotToken;
import org.rf.ide.core.testdata.text.read.recognizer.RobotTokenType;

import com.google.common.annotations.VisibleForTesting;


public class TestCaseTimeoutValueMapper implements IParsingMapper {

    private final ParsingStateHelper utility;


    public TestCaseTimeoutValueMapper() {
        this.utility = new ParsingStateHelper();
    }


    @Override
    public RobotToken map(final RobotLine currentLine,
            final Stack<ParsingState> processingState,
            final RobotFileOutput robotFileOutput, final RobotToken rt, final FilePosition fp,
            final String text) {
        final List<IRobotTokenType> types = rt.getTypes();
        types.add(0, RobotTokenType.TEST_CASE_SETTING_TIMEOUT_VALUE);

        rt.setText(text);
        rt.setRaw(text);

        final TestCaseTable testCaseTable = robotFileOutput.getFileModel()
                .getTestCaseTable();
        final List<TestCase> testCases = testCaseTable.getTestCases();
        final TestCase testCase = testCases.get(testCases.size() - 1);
        final List<TestCaseTimeout> timeouts = testCase.getTimeouts();
        if (!timeouts.isEmpty()) {
            timeouts.get(timeouts.size() - 1).setTimeout(rt);
        } else {
            // FIXME: some internal error
        }
        processingState.push(ParsingState.TEST_CASE_SETTING_TEST_TIMEOUT_VALUE);

        return rt;
    }


    @Override
    public boolean checkIfCanBeMapped(final RobotFileOutput robotFileOutput,
            final RobotLine currentLine, final RobotToken rt, final String text,
            final Stack<ParsingState> processingState) {
        boolean result = false;
        final ParsingState state = utility.getCurrentStatus(processingState);

        if (state == ParsingState.TEST_CASE_SETTING_TEST_TIMEOUT) {
            final List<TestCase> tests = robotFileOutput.getFileModel()
                    .getTestCaseTable().getTestCases();
            final List<TestCaseTimeout> timeouts = tests.get(tests.size() - 1)
                    .getTimeouts();
            result = !checkIfHasAlreadyValue(timeouts);
        }
        return result;
    }


    @VisibleForTesting
    protected boolean checkIfHasAlreadyValue(
            final List<TestCaseTimeout> testCaseTimeouts) {
        boolean result = false;
        for (final TestCaseTimeout setting : testCaseTimeouts) {
            result = (setting.getTimeout() != null);
            result = result || !setting.getMessage().isEmpty();
            if (result) {
                break;
            }
        }

        return result;
    }
}
