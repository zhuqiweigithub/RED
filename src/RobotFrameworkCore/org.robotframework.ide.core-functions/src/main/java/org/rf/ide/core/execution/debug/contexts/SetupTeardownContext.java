/*
 * Copyright 2017 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.rf.ide.core.execution.debug.contexts;

import java.net.URI;
import java.util.Optional;

import org.rf.ide.core.execution.debug.RobotBreakpoint;
import org.rf.ide.core.execution.debug.RobotBreakpointSupplier;
import org.rf.ide.core.execution.debug.RunningKeyword;
import org.rf.ide.core.execution.debug.StackFrameContext;
import org.rf.ide.core.testdata.model.FilePosition;
import org.rf.ide.core.testdata.model.FileRegion;
import org.rf.ide.core.testdata.model.table.keywords.names.QualifiedKeywordName;

class SetupTeardownContext extends DefaultContext {

    private final URI locationUri;

    private final String keywordName;

    private final int line;

    private final StackFrameContext previousContext;

    private final RobotBreakpointSupplier breakpointSupplier;

    private final String errorMessage;

    SetupTeardownContext(final URI locationUri, final String keywordName, final int line,
            final StackFrameContext previousContext,
            final RobotBreakpointSupplier breakpointSupplier) {
        this(locationUri, keywordName, line, null, previousContext, breakpointSupplier);
    }

    SetupTeardownContext(final String errorMessage, final StackFrameContext previousContext) {
        this(null, null, -1, errorMessage, previousContext, new RobotBreakpointSupplier());
    }

    SetupTeardownContext(final URI locationUri, final int line, final String errorMessage,
            final StackFrameContext previousContext) {
        this(locationUri, null, line, errorMessage, previousContext, new RobotBreakpointSupplier());
    }

    SetupTeardownContext(final URI locationUri, final int line, final String errorMessage,
            final StackFrameContext previousContext, final RobotBreakpointSupplier breakpointSupplier) {
        this(locationUri, null, line, errorMessage, previousContext, breakpointSupplier);
    }

    private SetupTeardownContext(final URI locationUri, final String keywordName, final int line,
            final String errorMessage, final StackFrameContext previousContext,
            final RobotBreakpointSupplier breakpointSupplier) {
        this.locationUri = locationUri;
        this.keywordName = keywordName;
        this.line = line;
        this.errorMessage = errorMessage;
        this.previousContext = previousContext;
        this.breakpointSupplier = breakpointSupplier;
    }

    @Override
    public boolean isErroneous() {
        return errorMessage != null;
    }

    @Override
    public Optional<String> getErrorMessage() {
        return Optional.ofNullable(errorMessage);
    }

    @Override
    public Optional<URI> getAssociatedPath() {
        return Optional.ofNullable(locationUri);
    }

    @Override
    public Optional<FileRegion> getFileRegion() {
        return Optional.of(new FileRegion(new FilePosition(line, -1, -1), new FilePosition(line, -1, -1)));
    }

    @Override
    public StackFrameContext moveTo(final RunningKeyword keyword, final RobotBreakpointSupplier breakpointSupplier) {
        throw new IllegalDebugContextStateException(
                "Only single keyword can be called as setup or teardown, so it impossible to move to next one if already positioned at first one");
    }

    @Override
    public StackFrameContext previousContext() {
        return previousContext;
    }

    @Override
    public Optional<RobotBreakpoint> getLineBreakpoint() {
        return breakpointSupplier.lineBreakpointFor(locationUri, line);
    }

    @Override
    public Optional<RobotBreakpoint> getKeywordFailBreakpoint(final QualifiedKeywordName currentlyFailedKeyword) {
        return ExecutableCallContext.getKeywordFailBreakpoint(breakpointSupplier, keywordName, currentlyFailedKeyword);
    }
}
