/*
 * Copyright 2016 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.robotframework.ide.eclipse.main.plugin.model.cmd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.robotframework.ide.eclipse.main.plugin.model.ModelConditions.name;
import static org.robotframework.ide.eclipse.main.plugin.model.ModelConditions.noChildren;

import java.util.stream.Stream;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.robotframework.ide.eclipse.main.plugin.mockeclipse.ContextInjector;
import org.robotframework.ide.eclipse.main.plugin.mockmodel.RobotSuiteFileCreator;
import org.robotframework.ide.eclipse.main.plugin.model.RobotCase;
import org.robotframework.ide.eclipse.main.plugin.model.RobotCasesSection;
import org.robotframework.ide.eclipse.main.plugin.model.RobotCodeHoldingElement;
import org.robotframework.ide.eclipse.main.plugin.model.RobotKeywordCall;
import org.robotframework.ide.eclipse.main.plugin.model.RobotKeywordCallConditions;
import org.robotframework.ide.eclipse.main.plugin.model.RobotKeywordDefinition;
import org.robotframework.ide.eclipse.main.plugin.model.RobotKeywordsSection;
import org.robotframework.ide.eclipse.main.plugin.model.RobotModelEvents;
import org.robotframework.ide.eclipse.main.plugin.model.RobotSuiteFile;
import org.robotframework.ide.eclipse.main.plugin.tableeditor.EditorCommand;

import com.google.common.collect.ImmutableMap;

public class CreateFreshKeywordCallCommandTest {

    private static Stream<Arguments> provideTestData() throws Exception {
        return Stream.of(Arguments.of(createTestCase()), Arguments.of(createTestCaseWithSettings()),
                Arguments.of(createKeywords()), Arguments.of(createKeywordsWithSettings()));
    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    public void whenCommandIsUsedWithoutIndex_newCallIsProperlyAddedAtTheEnd(
            final RobotCodeHoldingElement<?> codeHolder) {

        final int oldSize = codeHolder.getChildren().size();

        final IEventBroker eventBroker = mock(IEventBroker.class);
        final CreateFreshKeywordCallCommand command = ContextInjector.prepareContext()
                .inWhich(eventBroker)
                .isInjectedInto(new CreateFreshKeywordCallCommand(codeHolder));
        command.execute();

        assertThat(codeHolder.getChildren().size()).isEqualTo(oldSize + 1);

        final RobotKeywordCall addedCall = codeHolder.getChildren().get(oldSize);
        assertThat(addedCall).has(RobotKeywordCallConditions.properlySetParent()).has(name("")).has(noChildren());
        assertThat(addedCall.getArguments()).isEmpty();

        for (final EditorCommand undo : command.getUndoCommands()) {
            undo.execute();
        }

        assertThat(codeHolder.getChildren().size()).isEqualTo(oldSize);

        verify(eventBroker, times(1)).send(eq(RobotModelEvents.ROBOT_KEYWORD_CALL_ADDED),
                eq(ImmutableMap.of(IEventBroker.DATA, codeHolder, RobotModelEvents.ADDITIONAL_DATA, addedCall)));
        verify(eventBroker, times(1)).send(RobotModelEvents.ROBOT_KEYWORD_CALL_REMOVED, codeHolder);
        verifyNoMoreInteractions(eventBroker);
    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    public void whenCommandIsUsedWithIndex_newCallIsProperlyAddedAtSpecifiedPlace(
            final RobotCodeHoldingElement<?> codeHolder) {
        for (int index = 0; index <= codeHolder.getChildren().size(); index++) {

            final int oldSize = codeHolder.getChildren().size();

            final IEventBroker eventBroker = mock(IEventBroker.class);
            final CreateFreshKeywordCallCommand command = ContextInjector.prepareContext()
                    .inWhich(eventBroker)
                    .isInjectedInto(new CreateFreshKeywordCallCommand(codeHolder, index));
            command.execute();

            assertThat(codeHolder.getChildren().size()).isEqualTo(oldSize + 1);

            final RobotKeywordCall addedCall = codeHolder.getChildren().get(index);
            assertThat(addedCall).has(RobotKeywordCallConditions.properlySetParent()).has(name(""));
            assertThat(addedCall.getArguments()).isEmpty();

            for (final EditorCommand undo : command.getUndoCommands()) {
                undo.execute();
            }

            assertThat(codeHolder.getChildren().size()).isEqualTo(oldSize);

            verify(eventBroker, times(1)).send(eq(RobotModelEvents.ROBOT_KEYWORD_CALL_ADDED),
                    eq(ImmutableMap.of(IEventBroker.DATA, codeHolder, RobotModelEvents.ADDITIONAL_DATA, addedCall)));
            verify(eventBroker, times(1)).send(RobotModelEvents.ROBOT_KEYWORD_CALL_REMOVED, codeHolder);
            verifyNoMoreInteractions(eventBroker);
        }
    }

    private static RobotCase createTestCase() {
        final RobotSuiteFile model = new RobotSuiteFileCreator().appendLine("*** Test Cases ***")
                .appendLine("case 1")
                .appendLine("  Log  10")
                .appendLine("  Log  20")
                .appendLine("  Log  30")
                .build();
        final RobotCasesSection section = model.findSection(RobotCasesSection.class).get();
        return section.getChildren().get(0);
    }

    private static RobotCase createTestCaseWithSettings() {
        final RobotSuiteFile model = new RobotSuiteFileCreator().appendLine("*** Test Cases ***")
                .appendLine("case 1")
                .appendLine("  [setup]  a")
                .appendLine("  [tags]  a")
                .appendLine("  [teardown]  a")
                .appendLine("  Log  10")
                .appendLine("  Log  20")
                .appendLine("  Log  30")
                .build();
        final RobotCasesSection section = model.findSection(RobotCasesSection.class).get();
        return section.getChildren().get(0);
    }

    private static RobotKeywordDefinition createKeywords() {
        final RobotSuiteFile model = new RobotSuiteFileCreator().appendLine("*** Keywords ***")
                .appendLine("keyword")
                .appendLine("  call  10")
                .appendLine("  call  20")
                .appendLine("  call  30")
                .build();
        final RobotKeywordsSection section = model.findSection(RobotKeywordsSection.class).get();
        return section.getChildren().get(0);
    }

    private static RobotKeywordDefinition createKeywordsWithSettings() {
        final RobotSuiteFile model = new RobotSuiteFileCreator().appendLine("*** Keywords ***")
                .appendLine("keyword")
                .appendLine("  [arguments]  a")
                .appendLine("  [tags]  a")
                .appendLine("  [teardown]  a")
                .appendLine("  call  10")
                .appendLine("  call  20")
                .appendLine("  call  30")
                .build();
        final RobotKeywordsSection section = model.findSection(RobotKeywordsSection.class).get();
        return section.getChildren().get(0);
    }
}
