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
package fi.vm.sade.oppija.lomake.domain.elements.questions;

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.oppija.lomake.domain.I18nText;

import java.io.Serializable;
import java.util.Map;

/**
 * Base class of data related question.
 * Contains key value pairs of data.
 *
 * @author Mikko Majapuro
 */
public abstract class DataRelatedQuestion<E extends Serializable> extends Question {

    private static final long serialVersionUID = 1051594528316818102L;
    protected Map<String, E> data;


    protected DataRelatedQuestion(final String id, final I18nText i18nText, final Map<String, E> data) {
        super(id, i18nText);
        this.data = ImmutableMap.copyOf(data);
    }

    public Map<String, E> getData() {
        return data;
    }

    public E getData(String key) {
        return data.get(key);
    }
}
