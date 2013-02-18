/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.oppija.common.valintaperusteet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedListMultimap;

public class AdditionalQuestions {

    private final LinkedListMultimap<String /* oid */, InputParameter> questions = LinkedListMultimap.create();
    private final Map<String /* key */, InputParameter> questionMap = new HashMap<String, InputParameter>();

    public void addParameter(final String oid, final InputParameter param) {
        questions.put(oid, param);
        questionMap.put(param.getKey(), param);
    }

    /**
     * Get questions for hakukohde.
     */
    public List<InputParameter> getQuestistionsForHakukohde(final String oid) {
        Preconditions.checkNotNull(oid, "Oid cannot be null");
        return ImmutableList.copyOf(questions.get(oid));
    }

    /**
     * Get questions for hakukohde, phase
     */
    public List<InputParameter> getQuestistionsForHakukohdePhase(final String oid, final String phase) {
        Preconditions.checkNotNull(oid, "Oid cannot be null");
        Preconditions.checkNotNull(phase, "Phase cannot be null");
        return ImmutableList.copyOf(Iterables.filter(getQuestistionsForHakukohde(oid), new Predicate<InputParameter>() {
            public boolean apply(InputParameter input) {
                return phase.equals(input.getPhase());
            }
        }));
    }

    public List<InputParameter> getAllQuestions() {
        return ImmutableList.copyOf(questionMap.values());
    }

    public Map<String, InputParameter> getQuestionMap() {
        return questionMap;
    }

    @Override
    public String toString() {
        return String.format("%s, questions (%d) %s", this.getClass().getName(), this.questions.entries().size(), questions);
    }
}
