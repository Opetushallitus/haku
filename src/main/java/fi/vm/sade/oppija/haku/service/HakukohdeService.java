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

package fi.vm.sade.oppija.haku.service;

import fi.vm.sade.oppija.haku.domain.Hakukohde;
import fi.vm.sade.oppija.haku.domain.Organisaatio;
import fi.vm.sade.oppija.haku.domain.elements.custom.SubjectRow;
import fi.vm.sade.oppija.haku.domain.questions.Question;

import java.util.List;

/**
 * Service for searching education institutes
 * @author Mikko Majapuro
 */
public interface HakukohdeService {

    /**
     *
     * @param term search term
     * @return list of education institutes matching search term
     */
    List<Organisaatio> searchOrganisaatio(final String term);

    /**
     *
     * @param organisaatioId
     * @return
     */
    List<Hakukohde> searchHakukohde(final String organisaatioId);

    /**
     *
     * @param hakukohdeId
     * @param teemaId
     * @return
     */
    List<Question> getHakukohdeSpecificQuestions(String hakukohdeId, String teemaId);

    /**
     *
     * @param hakukohdeId
     * @param teemaId
     * @return
     */
    List<SubjectRow> getHakukohdeSpecificSubjects(String hakukohdeId, String teemaId);

}
