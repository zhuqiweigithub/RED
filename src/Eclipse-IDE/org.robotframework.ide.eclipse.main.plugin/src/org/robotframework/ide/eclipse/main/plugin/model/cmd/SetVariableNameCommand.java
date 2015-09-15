/*
 * Copyright 2015 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.robotframework.ide.eclipse.main.plugin.model.cmd;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import org.robotframework.ide.core.testData.model.table.variables.AVariable.VariableType;
import org.robotframework.ide.core.testData.model.table.variables.IVariableHolder;
import org.robotframework.ide.core.testData.text.read.recognizer.RobotToken;
import org.robotframework.ide.eclipse.main.plugin.model.RobotModelEvents;
import org.robotframework.ide.eclipse.main.plugin.model.RobotVariable;
import org.robotframework.ide.eclipse.main.plugin.model.RobotVariablesSection;
import org.robotframework.ide.eclipse.main.plugin.tableeditor.EditorCommand;

public class SetVariableNameCommand extends EditorCommand {

    private final RobotVariable variable;
    private final String newName;

    public SetVariableNameCommand(final RobotVariable variable, final String newName) {
        this.variable = variable;
        this.newName = newName;
    }

    @Override
    public void execute() throws CommandExecutionException {
        if (variable.getType() == VariableType.INVALID && newName.equals(variable.getName())) {
            return;
        } else if (newName.equals(variable.getPrefix() + variable.getName() + variable.getSuffix())) {
            return;
        }

        boolean typeWasChanged;
        if (newName.startsWith(VariableType.SCALAR.getIdentificator() + "{") && newName.endsWith("}")) {

            typeWasChanged = setTypeIfNeeded(VariableType.SCALAR, VariableType.SCALAR_AS_LIST);
            setName(newName);
        } else if (newName.startsWith(VariableType.DICTIONARY.getIdentificator() + "{") && newName.endsWith("}")) {

            typeWasChanged = setTypeIfNeeded(VariableType.DICTIONARY);
            setName(newName);
        } else if (newName.startsWith(VariableType.LIST.getIdentificator() + "{") && newName.endsWith("}")) {

            typeWasChanged = setTypeIfNeeded(VariableType.LIST);
            setName(newName);
        } else {

            typeWasChanged = setTypeIfNeeded(VariableType.INVALID);
            setName(newName);
        }

        final List<RobotVariable> children = variable.getSuiteFile()
                .findSection(RobotVariablesSection.class)
                .get()
                .getChildren();
        RobotVariable newVariable = null;
        for (final RobotVariable var : children) {
            if (var.getName().equals(getNewNameWithoutMarks(newName))) {
                newVariable = var;
                break;
            }
        }
        if (typeWasChanged) {
            eventBroker.send(RobotModelEvents.ROBOT_VARIABLE_TYPE_CHANGE, newVariable);
        }
        eventBroker.send(RobotModelEvents.ROBOT_VARIABLE_NAME_CHANGE, newVariable);
    }

    private void setName(final String rawName) {
        final IVariableHolder linkedVariable = variable.getLinkedElement();
        
        final RobotToken declaration = linkedVariable.getDeclaration();
        final int offset = declaration.getStartOffset();
        final int length = declaration.getText().length();

        // DocumentUpdater.replace(document, offset, length, rawName);
        // variable.getSuiteFile().reparseEverything(document.get());
    }

    private boolean setTypeIfNeeded(final VariableType... type) {
        if (!newArrayList(type).contains(variable.getType())) {
            // final IVariableHolder linkedVariable = variable.getLinkedElement();
            // final RobotToken declaration = linkedVariable.getDeclaration();
            // linkedVariable.setType(type.toVariableType());
            // variable.setType(type);

            return true;
        }
        return false;
    }

    private String getNewNameWithoutMarks(final String name) {
        return name.substring(2, name.length() - 1);
    }
}
