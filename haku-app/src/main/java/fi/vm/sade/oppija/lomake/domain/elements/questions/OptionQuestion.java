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

import com.google.common.collect.ImmutableList;
import fi.vm.sade.oppija.lomake.domain.I18nText;
import fi.vm.sade.oppija.lomake.domain.elements.Element;
import fi.vm.sade.oppija.lomake.validation.Validator;
import fi.vm.sade.oppija.lomake.validation.validators.ValueSetValidator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public abstract class OptionQuestion extends Question {

    private static final long serialVersionUID = -2304711424350028559L;
    private final List<Option> options = new ArrayList<Option>();

    protected OptionQuestion(@JsonProperty(value = "id") String id,
                             @JsonProperty(value = "i18nText") I18nText i18nText) {
        super(id, i18nText);
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

    public final void addOptions(final List<Option> countries) {
        this.options.addAll(countries);
    }

    public List<Option> getOptions() {
        return ImmutableList.copyOf(options);
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

    @Override
    public List<Validator> getValidators() {
        List<Validator> listOfValidator = new ArrayList<Validator>();
        listOfValidator.addAll(super.getValidators());
        List<String> values = new ArrayList<String>();
        for (Option option : options) {
            values.add(option.getValue());
        }
        listOfValidator.add(new ValueSetValidator(this.getId(), "Virheellinen arvo", values));
        return listOfValidator;
    }
}


