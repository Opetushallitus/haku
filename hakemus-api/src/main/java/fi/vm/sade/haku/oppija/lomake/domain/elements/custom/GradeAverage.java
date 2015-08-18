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

package fi.vm.sade.haku.oppija.lomake.domain.elements.custom;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Question;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GradeAverage extends Question {

    private String relatedNimikeId;
    private Map<String, Option> ammattitutkintonimikkeet;
    private String relatedMuuNimike;
    private I18nText nimikeFallback;
    private List<Option> ammattitutkintonimikkeetList;

    private String relatedOppilaitosId;
    private Map<String, Option> oppilaitokset;
    private List<Option> oppilaitoksetList;
    private String relatedMuuOppilaitos;

    public GradeAverage(final String id,
                        final String relatedNimikeId, final List<Option> ammattitutkintonimikkeetList, final String relatedMuuNimike, final I18nText nimikeFallback,
                        final String relatedOppilaitosId, final String relatedMuuOppilaitos, final List<Option> oppilaitoksetList) {
        super(id, null);

        this.relatedNimikeId = relatedNimikeId;
        this.ammattitutkintonimikkeet = new HashMap<>();
        this.ammattitutkintonimikkeet.put("nimikeFallback", new Option(nimikeFallback, ""));
        this.ammattitutkintonimikkeetList = ammattitutkintonimikkeetList;
        if (ammattitutkintonimikkeetList != null) {
            for (Option nimike : ammattitutkintonimikkeetList) {
                this.ammattitutkintonimikkeet.put(nimike.getValue(), nimike);
            }
        }
        this.relatedMuuNimike = relatedMuuNimike;
        this.nimikeFallback = nimikeFallback;

        this.relatedOppilaitosId = relatedOppilaitosId;
        this.oppilaitokset = new HashMap<>();
        if (oppilaitoksetList != null) {
            for (Option oppilaitos : oppilaitoksetList) {
                String key = oppilaitos.getValue().replaceAll("\\.", "_");
                this.oppilaitokset.put(key, oppilaitos);
            }
        }
        this.relatedMuuOppilaitos = relatedMuuOppilaitos;
    }

    public String getRelatedNimikeId() {
        return relatedNimikeId;
    }

    public Map<String, Option> getAmmattitutkintonimikkeet() {
        return ammattitutkintonimikkeet;
    }

    public String getRelatedMuuNimike() {
        return relatedMuuNimike;
    }

    public I18nText getNimikeFallback() {
        return nimikeFallback;
    }

    public String getRelatedOppilaitosId() {
        return relatedOppilaitosId;
    }

    public Map<String, Option> getOppilaitokset() {
        return oppilaitokset;
    }

    public String getRelatedMuuOppilaitos() {
        return relatedMuuOppilaitos;
    }

    public List<Option> getAmmattitutkintonimikkeetList() {
        return ammattitutkintonimikkeetList;
    }

    public List<Option> getOppilaitoksetList() {
        return oppilaitoksetList;
    }
}
