package org.robotframework.ide.eclipse.main.plugin.texteditor.contentAssist;

import static com.google.common.collect.Lists.newArrayList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Image;
import org.robotframework.ide.eclipse.main.plugin.RedImages;
import org.robotframework.red.graphics.ImagesManager;

public class TextEditorContentAssist {
    
    private static final String PROPOSAL_SEPARATOR = "  ";  //placed after proposal text
    private static final String SECTION_PROPOSAL_SEPARATOR = "\n";
    private static Image variableImage = ImagesManager.getImage(RedImages.getRobotScalarVariableImage());

    private static List<String> sections = new ArrayList<>();
    static {
        sections.add("*** Variables ***");
        sections.add("*** Settings ***");
        sections.add("*** Test Cases ***");
        sections.add("*** Keywords ***");
    }
    
    private static List<String> settingsSectionWords = new ArrayList<>();
    static {
        settingsSectionWords.add("Library");
        settingsSectionWords.add("Resource");
        settingsSectionWords.add("Variables");
        settingsSectionWords.add("Documentation");
        settingsSectionWords.add("Metadata");
        settingsSectionWords.add("Suite Setup");
        settingsSectionWords.add("Suite Teardown");
        settingsSectionWords.add("Force Tags");
        settingsSectionWords.add("Default Tags");
        settingsSectionWords.add("Test Setup");
        settingsSectionWords.add("Test Teardown");
        settingsSectionWords.add("Test Template");
        settingsSectionWords.add("Test Timeout");
    }
    
    private static List<String> keywordsSectionWords = new ArrayList<>();
    static {
        keywordsSectionWords.add("[Documentation]");
        keywordsSectionWords.add("[Tags]");
        keywordsSectionWords.add("[Arguments]");
        keywordsSectionWords.add("[Return]");
        keywordsSectionWords.add("[Teardown]");
        keywordsSectionWords.add("[Timeout]");
    }
    
    private static List<String> testCasesSectionWords = new ArrayList<>();
    static {
        testCasesSectionWords.add("[Documentation]");
        testCasesSectionWords.add("[Tags]");
        testCasesSectionWords.add("[Setup]");
        testCasesSectionWords.add("[Teardown]");
        testCasesSectionWords.add("[Template]");
        testCasesSectionWords.add("[Timeout]");
    }
    
    private static Map<String, String> variables = new HashMap<String, String>();
    static {
        variables.put("${/}", "\\");
        variables.put("${:}", ";");
        variables.put("${DEBUG_FILE}", null);
        variables.put("${EMPTY}", "");
        variables.put("${EXECDIR}", null);
        variables.put("${False}", "False");
        variables.put("${LOG_FILE}", null);
        variables.put("${LOG_LEVEL}", "INFO");
        variables.put("${None}", "None");
        variables.put("${OUTPUT_DIR}", null);
        variables.put("${REPORT_FILE}", null);
        variables.put("${SPACE}", " ");
        variables.put("${TEMPDIR}", null);
        variables.put("${True}", "True");
        variables.put("${\n}", null);
        variables.put("${null}", "None");
        variables.put("@{EMPTY}", "()");
    }
    
    private TextEditorContentAssist() {
        
    }
    
    public static ICompletionProposal[] buildSectionProposals(final String replacedWord,
            final int offset) {

        final ICompletionProposal[] completionProposals = new ICompletionProposal[sections.size()];
        int index = 0;
        for (String proposal : sections) {
            completionProposals[index] = new CompletionProposal(proposal + SECTION_PROPOSAL_SEPARATOR, offset,
                    replacedWord.length(), proposal.length() + SECTION_PROPOSAL_SEPARATOR.length(), null, proposal, null,
                    null);
            index++;
        }
        return completionProposals;
    }
    
    public static ICompletionProposal[] buildSimpleProposals(final List<String> proposals, final String replacedWord,
            final int offset, final Image image) {

        if (proposals.size() == 0) {
            return new ICompletionProposal[0];
        }
        final ICompletionProposal[] completionProposals = new ICompletionProposal[proposals.size()];
        int index = 0;
        for (String proposal : proposals) {
            completionProposals[index] = new CompletionProposal(proposal + PROPOSAL_SEPARATOR, offset,
                    replacedWord.length(), proposal.length() + PROPOSAL_SEPARATOR.length(), image, proposal, null, null);
            index++;
        }
        return completionProposals;
    }
    
    public static ICompletionProposal[] buildKeywordsProposals(final Map<String, ContentAssistKeywordContext> proposals,
            final String replacedWord, final int offset) {

        if (proposals.size() == 0) {
            return new ICompletionProposal[0];
        }

        final ICompletionProposal[] completionProposals = new ICompletionProposal[proposals.size()];
        String separator = "";
        int index = 0;
        for (Iterator<String> i = proposals.keySet().iterator(); i.hasNext();) {
            final String proposal = (String) i.next();
            final ContentAssistKeywordContext keywordContext = proposals.get(proposal);
            
            if(!keywordContext.getArguments().equals("[]")) {
                separator = PROPOSAL_SEPARATOR;
            } else {
                separator = "";
            }
            completionProposals[index] = new TextEditorCompletionProposal(proposal + separator, offset,
                    replacedWord.length(), proposal.length() + separator.length(), keywordContext.getImage(),
                    proposal, new ContextInformation(proposal, keywordContext.getArguments()),
                    keywordContext.getDescription(), keywordContext.getLibName());
            index++;

        }

        return completionProposals;
    }
    
    public static ICompletionProposal[] buildVariablesProposals(final Map<String, String> proposals,
            final String replacedWord, final int offset) {

        if (proposals.size() == 0) {
            return new ICompletionProposal[0];
        }

        final ICompletionProposal[] completionProposals = new ICompletionProposal[proposals.size()];
        int index = 0;
        for (Iterator<String> i = proposals.keySet().iterator(); i.hasNext();) {
            final String proposal = (String) i.next();
            final String variableValue = proposals.get(proposal);
            String info = null;
            if(variableValue != null) {
                info = "Value: " + variableValue;
            }
            
            completionProposals[index] = new CompletionProposal(proposal, offset,
                    replacedWord.length(), proposal.length(), variableImage, proposal, null, info);
            index++;
        }

        return completionProposals;
    }
    
    public static List<String> filterProposals(final List<String> allProposals, final String filter) {
        List<String> filteredProposals = newArrayList();
        for (String word : allProposals) {
            if (word.toLowerCase().startsWith(filter.toLowerCase())) {
                filteredProposals.add(word);
            }
        }
        return filteredProposals;
    }
    
    public static Map<String, ContentAssistKeywordContext> filterKeywordsProposals(
            final Map<String, ContentAssistKeywordContext> keywordMap, final String filter) {
        Map<String, ContentAssistKeywordContext> keywordProposals = new LinkedHashMap<>();
        for (Iterator<String> i = keywordMap.keySet().iterator(); i.hasNext();) {
            String keyword = (String) i.next();
            if (keyword.toLowerCase().startsWith(filter.toLowerCase())) {
                keywordProposals.put(keyword, keywordMap.get(keyword));
            }
        }
        return keywordProposals;
    }
    
    public static Map<String, String> filterVariablesProposals(final Map<String, String> variablesMap,
            final String filter) {
        Map<String, String> variablesProposals = new LinkedHashMap<>();
        for (Iterator<String> i = variablesMap.keySet().iterator(); i.hasNext();) {
            String variable = (String) i.next();
            if (variable.toLowerCase().contains(filter.toLowerCase())) {
                variablesProposals.put(variable, variablesMap.get(variable));
            }
        }
        return variablesProposals;
    }
    
    public static String readEnteredWord(final int offset, final IDocument document) throws BadLocationException {
        String currentWord = "";
        char currentChar;
        int currentOffset = offset;
        while (currentOffset >= 0 && document.getChar(currentOffset) != '\n') {
            currentChar = document.getChar(currentOffset);
            currentWord = currentChar + currentWord;
            currentOffset--;
        }
        
        return currentWord;
    }
    
    public static String readEnteredKeyword(final int offset, final IDocument document) throws BadLocationException {
        String currentWord = "";
        char currentChar, prevChar;
        int currentOffset = offset;
        while (currentOffset > 0) {
            currentChar = document.getChar(currentOffset);
            prevChar = document.getChar(currentOffset - 1);
            if (Character.isWhitespace(currentChar)) {
                if (Character.isWhitespace(prevChar) && prevChar != '\n' || currentChar == '\t') {
                    break;
                } else if (prevChar == '\n') {
                    return null;
                }
            }
            if (prevChar == '\n') {
                return null;
            }
            currentWord = currentChar + currentWord;
            currentOffset--;
        }
        return currentWord;
    }
    
    public static String readEnteredVariable(final int offset, final IDocument document) throws BadLocationException {
        String currentWord = "";
        char currentChar, prevChar;
        int currentOffset = offset;
        while (currentOffset > 0 && document.getChar(currentOffset) != '\n') {
            currentChar = document.getChar(currentOffset);
            prevChar = document.getChar(currentOffset - 1);
            if (Character.isWhitespace(currentChar) && Character.isWhitespace(prevChar)) {
                break;
            }
            currentWord = currentChar + currentWord;
            currentOffset--;
        }
        
        return currentWord;
    }
    
    public static boolean shouldShowVariablesProposals(final String currentWord) {
        return currentWord.startsWith("$") || currentWord.startsWith("@") || currentWord.startsWith("&")
                || currentWord.startsWith("{");
    }

    public static List<String> getSettingsSectionWords() {
        return settingsSectionWords;
    }

    public static List<String> getKeywordsSectionWords() {
        return keywordsSectionWords;
    }

    public static List<String> getTestCasesSectionWords() {
        return testCasesSectionWords;
    }

    public static Map<String, String> getVariables() {
        return variables;
    }

}
