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
import fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil;

import java.util.*;

public abstract class OptionQuestion extends Question {

    private static final long serialVersionUID = -2304711424350028559L;
    private final List<Option> options = new ArrayList<Option>();
    private Map<String, List<Option>> optionsSortedByText;

    protected OptionQuestion(final String id, final I18nText i18nText) {
        super(id, i18nText);
    }

    public Option addOption(final String id, final I18nText i18nText, final String value) {
        Option option = new Option(this.getId() + ID_DELIMITER + id, i18nText, value);
        this.options.add(option);
        return option;
    }

    public Option addOption(final String id, final I18nText i18nText, final String value, final I18nText help) {
        Option option = new Option(this.getId() + ID_DELIMITER + id, i18nText, value);
        option.setHelp(help);
        this.options.add(option);
        return option;
    }

    public final void addOptions(final List<Option> options) {
        this.options.addAll(options);
    }

    public List<Option> getOptions() {
        return ImmutableList.copyOf(options);
    }

    public Map<String, List<Option>> getOptionsSortedByText() {
        if (optionsSortedByText == null) {
            initSortedOptions();
        }
        return optionsSortedByText;
    }

    private void initSortedOptions() {
        optionsSortedByText = new HashMap<String, List<Option>>();
        for (Option option : options) {
            Set<String> langs = option.getI18nText().getTranslations().keySet();
            for (String lang : langs) {
                List<Option> optionListForLang = optionsSortedByText.get(lang);
                if (optionListForLang == null) {
                    optionListForLang = new ArrayList<Option>(options.size());
                    optionsSortedByText.put(lang, optionListForLang);
                }
                optionListForLang.add(option);
            }
        }
        for (Map.Entry<String, List<Option>> entry : optionsSortedByText.entrySet()) {
            List<Option> optionList = entry.getValue();
            final String lang = entry.getKey();
            Collections.sort(optionList, new Comparator<Option>() {
                @Override
                public int compare(Option o1, Option o2) {
                    String o1Trans = o1.getI18nText().getTranslations().get(lang);
                    String o2Trans = o2.getI18nText().getTranslations().get(lang);
                    return o1Trans.compareTo(o2Trans);
                }
            });
        }
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

        listOfValidator.add(new ValueSetValidator(this.getId(), ElementUtil.createI18NTextError("yleinen.virheellinenArvo"), values));
        return listOfValidator;
    }
}


