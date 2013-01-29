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

import fi.vm.sade.oppija.lomake.domain.I18nText;
import fi.vm.sade.oppija.lomake.domain.elements.Element;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public abstract class OptionQuestion extends Question {

    private final List<Option> options = new ArrayList<Option>();

    protected OptionQuestion(@JsonProperty(value = "id") String id,
                             @JsonProperty(value = "i18nText") I18nText i18nText) {
        super(id, i18nText);
    }

    @Override
    public void init() {
        super.init();
        for (Option option : options) {
            option.init();
        }
    }

    public Option addOption(final String id, final I18nText i18nText, final String value) {
        Option option = new Option(this.getId() + ID_DELIMITER + id, i18nText, value);
        this.options.add(option);
        return option;
    }

    public Option addOption(final String id, final I18nText i18nText, final String value, final String help) {
        Option option = new Option(this.getId() + ID_DELIMITER + id, i18nText, value);
        option.setHelp(help);
        this.options.add(option);
        return option;
    }

    public List<Option> getOptions() {
        return options;
    }

    @Override
    public List<Element> getChildren() {
        List<Element> listOfElements = new ArrayList<Element>();
        listOfElements.addAll(super.getChildren());
        for (Option option : options) {
            listOfElements.addAll(option.getChildren());
        }
        return listOfElements;
    }
}


