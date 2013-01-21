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

import fi.vm.sade.oppija.lomake.domain.elements.questions.Option;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

/**
 * Models a grade grid row that has an input for language option and
 * scope (A1, B1 etc) option. Language options are listed in options list
 * provided by OptionQuestion and scope options are listed in scope options variable.
 *
 * @author Hannu Lyytikainen
 */
public class CustomLanguageRow extends LanguageRow {

    private List<Option> scopeOptions;

    public CustomLanguageRow(@JsonProperty(value = "id") String id, @JsonProperty(value = "title") String title,
                             @JsonProperty(value = "scopeOptions") List<Option> scopeOptions) {
        super(id, title);
        this.scopeOptions = scopeOptions;
    }

    public void addScopeOption(final String id, final String value, final String title) {
        this.scopeOptions.add(new Option(this.getId() + ID_DELIMITER + id, value, title));
    }

    public List<Option> getScopeOptions() {
        return scopeOptions;
    }

}
