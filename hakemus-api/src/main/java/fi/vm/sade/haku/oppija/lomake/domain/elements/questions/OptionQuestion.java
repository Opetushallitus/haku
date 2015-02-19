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

package fi.vm.sade.haku.oppija.lomake.domain.elements.questions;

import com.google.common.collect.ImmutableList;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import org.springframework.data.annotation.Transient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class OptionQuestion extends Question {

    private static final long serialVersionUID = -2304711424350028559L;

    private final List<Option> options = new ArrayList<Option>();

    @Transient
    private final Map<String, Option> optionsMap = new LinkedHashMap<String, Option>();
    @Transient
    private final Map<String, List<Option>> optionsSortedByText = new HashMap<String, List<Option>>();

    protected OptionQuestion(final String id, final I18nText i18nText, final List<Option> options) {
        super(id, i18nText);
        this.options.addAll(options);
    }

    public List<Option> getOptions() {
        return ImmutableList.copyOf(options);
    }

    public Map<String, Option> getData() {
        if (optionsMap.size() < 1 && options.size() > 0)
            initOptionsMap();
        return optionsMap;
    }

    public Map<String, List<Option>> getOptionsSortedByText() {
        if (optionsSortedByText.size() < 1 && options.size() > 0)
            initSortedOptions();
        return optionsSortedByText;
    }

    private void initSortedOptions() {
        synchronized (optionsSortedByText) {
            if (optionsSortedByText.size() > 0)
                return;
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
    }

    private void initOptionsMap(){
        synchronized (optionsMap){
            if (optionsMap.size() > 0)
                return;
            for (Option option : options) {
                this.optionsMap.put(option.getValue(), option);
            }
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
    public String getExcelValue(String answer, String lang) {
        Option option = getData().get(answer);
        String value = null;
        if (option != null) {
            value = ElementUtil.getText(option, lang);
        }
        return value;
    }
}


