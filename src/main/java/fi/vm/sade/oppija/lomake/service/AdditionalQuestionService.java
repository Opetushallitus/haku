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

package fi.vm.sade.oppija.lomake.service;

import fi.vm.sade.oppija.lomake.domain.FormId;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Question;

import java.util.List;
import java.util.Set;

/**
 * Resolves education specific additional questions related to different themes.
 *
 * @author Hannu Lyytikainen
 */
public interface AdditionalQuestionService {

    /**
     * Lists questions in a given teema based on current answers.
     *
     * @param teemaId      teema id
     * @param formId hakemus id
     * @return list of questions
     */
    Set<Question> findAdditionalQuestions(String teemaId, FormId formId, String vaiheId);

    /**
     * Lists additional questions in a theme based on a list of education targets.
     *
     * @param teemaId      teema id
     * @param hakukohdeIds education targets
     * @param formId hakemus id
     * @return list of questions
     */
    Set<Question> findAdditionalQuestions(String teemaId, List<String> hakukohdeIds, FormId formId, String vaiheId);
}
