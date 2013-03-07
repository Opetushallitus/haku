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

package fi.vm.sade.oppija.lomake.domain.elements.custom;

import fi.vm.sade.oppija.lomake.domain.I18nText;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Renders as a language grade row in grade grid. Title is used to hold the scope
 * of the language studies (A1, B2 etc) and options (defined in OptionQuestion) are used to hold
 * the different languages.
 *
 * @author Hannu Lyytikainen
 */
public class LanguageRow extends SubjectRow {

    public LanguageRow(@JsonProperty(value = "id") String id,
                       @JsonProperty(value = "i18nText") I18nText i18nText) {
        super(id, true, true, i18nText);
    }
}
