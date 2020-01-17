/*
 * Copyright 2016 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.robotframework.ide.eclipse.main.plugin.assist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.robotframework.ide.eclipse.main.plugin.assist.Commons.prefixesMatcher;

import java.util.List;

import org.junit.jupiter.api.Test;

public class LibraryAliasReservedWordProposalsTest {

    @Test
    public void noProposalsAreProvided_whenPredicateIsAlwaysFalse() {
        final AssistProposalPredicate<String> predicateWordHasToSatisfy = AssistProposalPredicates.alwaysFalse();
        final LibraryAliasReservedWordProposals proposalsProvider = new LibraryAliasReservedWordProposals(
                predicateWordHasToSatisfy);

        final List<? extends AssistProposal> proposals = proposalsProvider.getReservedWordProposals("");
        assertThat(proposals).isEmpty();
    }

    @Test
    public void allProposalsAreProvided_whenPredicateIsAlwaysTrue() {
        final AssistProposalPredicate<String> predicateWordHasToSatisfy = AssistProposalPredicates.alwaysTrue();
        final LibraryAliasReservedWordProposals proposalsProvider = new LibraryAliasReservedWordProposals(
                predicateWordHasToSatisfy);

        final List<? extends AssistProposal> proposals = proposalsProvider.getReservedWordProposals("");
        assertThat(proposals).extracting(AssistProposal::getLabel).containsExactly("WITH NAME");
    }

    @Test
    public void onlyProposalsMatchingPredicateAreProvided_whenPredicateSelectsThem() {
        final AssistProposalPredicate<String> predicateWordHasToSatisfy = word -> word.length() < 4;
        final LibraryAliasReservedWordProposals proposalsProvider = new LibraryAliasReservedWordProposals(
                predicateWordHasToSatisfy);

        final List<? extends AssistProposal> proposals = proposalsProvider.getReservedWordProposals("");
        assertThat(proposals).isEmpty();
    }

    @Test
    public void onlyProposalsMatchingPredicateAreProvided_whenPredicateCannotSelectThem() {
        final AssistProposalPredicate<String> predicateWordHasToSatisfy = word -> word.length() > 4;
        final LibraryAliasReservedWordProposals proposalsProvider = new LibraryAliasReservedWordProposals(
                predicateWordHasToSatisfy);

        final List<? extends AssistProposal> proposals = proposalsProvider.getReservedWordProposals("");
        assertThat(proposals).extracting(AssistProposal::getLabel).containsExactly("WITH NAME");
    }

    @Test
    public void onlyProposalsContainingInputAreProvided_whenDefaultMatcherIsUsed() {
        final LibraryAliasReservedWordProposals proposalsProvider = new LibraryAliasReservedWordProposals(
                AssistProposalPredicates.alwaysTrue());

        final List<? extends AssistProposal> proposals = proposalsProvider.getReservedWordProposals("am");
        assertThat(proposals).extracting(AssistProposal::getLabel).containsExactly("WITH NAME");
    }

    @Test
    public void onlyProposalsMatchingGivenMatcherAreProvided_whenMatcherIsGiven() {
        final LibraryAliasReservedWordProposals proposalsProvider = new LibraryAliasReservedWordProposals(
                prefixesMatcher(), AssistProposalPredicates.alwaysTrue());

        final List<? extends AssistProposal> proposals = proposalsProvider.getReservedWordProposals("with");
        assertThat(proposals).extracting(AssistProposal::getLabel).containsExactly("WITH NAME");
    }
}
