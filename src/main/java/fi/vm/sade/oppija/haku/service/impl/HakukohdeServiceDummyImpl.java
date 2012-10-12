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

package fi.vm.sade.oppija.haku.service.impl;

import fi.vm.sade.oppija.haku.domain.Hakukohde;
import fi.vm.sade.oppija.haku.domain.Organisaatio;
import fi.vm.sade.oppija.haku.domain.elements.custom.SubjectRow;
import fi.vm.sade.oppija.haku.domain.questions.Question;
import fi.vm.sade.oppija.haku.domain.questions.Radio;
import fi.vm.sade.oppija.haku.domain.questions.TextQuestion;
import fi.vm.sade.oppija.haku.service.HakukohdeService;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Implementation of education service.
 *
 * @author Mikko Majapuro
 */
@Service("hakukohdeServiceDummyImpl")
public class HakukohdeServiceDummyImpl implements HakukohdeService {

    public static final int AMOUNT_OF_TEST_OPETUSPISTE = 1000;
    public static final int AMOUNT_OF_TEST_HAKUKOHDE = 5;
    private List<Organisaatio> institutes = new ArrayList<Organisaatio>();
    private Map<String, List<Hakukohde>> hakukohdeMap = new HashMap<String, List<Hakukohde>>();
    private Map<String, List<Question>> lisakysymysMap = new HashMap<String, List<Question>>();
    private Map<String, List<SubjectRow>> oppiaineMap = new HashMap<String, List<SubjectRow>>();
    public static final int MAX_RESULTS = 10;

    public HakukohdeServiceDummyImpl() {
        // populate test data
        for (int i = 0; i < AMOUNT_OF_TEST_OPETUSPISTE; ++i) {
            Organisaatio op = new Organisaatio(String.valueOf(i), "Koulu" + i);
            institutes.add(op);
        }

        TextQuestion textQuestion = new TextQuestion("additional_question_1", "Lorem ipsum");
        Radio radio = new Radio("additional_question_2", "Lorem ipsum dolor sit ame");
        radio.addOption("q2_option_1", "q2_option_1", "Option one");
        radio.addOption("q2_option_2", "q2_option_2", "Option two");

        List<Question> lisakysymysList = new ArrayList<Question>();
        lisakysymysList.add(textQuestion);
        lisakysymysList.add(radio);

        List<SubjectRow> oppianieList = new ArrayList<SubjectRow>();
        oppianieList.add(new SubjectRow("geo", "Maantieto"));
        oppianieList.add(new SubjectRow("fys", "Fysiikka"));
        oppianieList.add(new SubjectRow("fil", "Filosofia"));
        oppianieList.add(new SubjectRow("kem", "Kemia"));

        for (Organisaatio institute : institutes) {
            List<Hakukohde> hakukohdeList = new ArrayList<Hakukohde>();
            for (int i = 0; i < AMOUNT_OF_TEST_HAKUKOHDE; i++) {
                String id = String.valueOf(institute.getId()) + "_" + String.valueOf(i);
                Hakukohde h;
                if (i % 2 == 0) {
                    h = new Hakukohde(id, "Hakukohde_" + id, lisakysymysList, oppianieList);
                    lisakysymysMap.put(id, lisakysymysList);
                    oppiaineMap.put(id, oppianieList);
                }
                else {
                    h = new Hakukohde(id, "Hakukohde_" + id);
                }
                hakukohdeList.add(h);
            }
            hakukohdeMap.put(institute.getId(), hakukohdeList);
        }
    }

    @Override
    public List<Organisaatio> searchOrganisaatio(String term) {
        List<Organisaatio> result = new ArrayList<Organisaatio>();
        if (term != null && !term.trim().isEmpty()) {
            term = term.trim().toLowerCase(Locale.getDefault());
            for (Organisaatio o : institutes) {
                if (o.getKey().startsWith(term)) {
                    result.add(o);
                    if (result.size() >= MAX_RESULTS) {
                        break;
                    }
                }
            }
        }
        return result;
    }

    @Override
    public List<Hakukohde> searchHakukohde(String organisaatioId) {
        return hakukohdeMap.get(organisaatioId);
    }

    @Override
    public List<Question> getHakukohdeSpecificQuestions(String hakukohdeId, String teemaId) {
        return lisakysymysMap.get(hakukohdeId);
    }

    @Override
    public List<SubjectRow> getHakukohdeSpecificSubjects(String hakukohdeId, String teemaId) {
        if (oppiaineMap.containsKey(hakukohdeId)) {
            return oppiaineMap.get(hakukohdeId);
        }
        else {
            return new ArrayList<SubjectRow>();
        }
    }
}
