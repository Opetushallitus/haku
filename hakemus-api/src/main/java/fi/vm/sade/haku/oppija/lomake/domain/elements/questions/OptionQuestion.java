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
import com.google.common.collect.ImmutableMap;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import org.codehaus.jackson.annotate.JsonIgnore;
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

    private final List<Option> options;

    private final String[] keepFirst;

    private final Boolean useGivenOrder;

    @Transient
    private Map<String, Option> optionsMap;
    @Transient
    private Map<String, List<Option>> optionsSortedByText;
    @Transient @JsonIgnore
    private final Object optionsMapLock = new Object();
    @Transient @JsonIgnore
    private final Object optionsSortedByTextLock = new Object();

    protected OptionQuestion(final String id, final I18nText i18nText, final List<Option> options, final String[] keepFirst, final Boolean useGivenOrder) {
        super(id, i18nText);
        this.options = ImmutableList.copyOf(options);
        this.keepFirst = keepFirst;
        this.useGivenOrder = useGivenOrder;
    }

    public List<Option> getOptions() {
        return options;
    }


    public Map<String, Option> getData() {
        if (null == optionsMap)
            initOptionsMap();
        return optionsMap;
    }

    public Map<String, List<Option>> getOptionsSortedByText() {
        if (null == optionsSortedByText)
            initSortedOptions();
        return optionsSortedByText;
    }

    private void initSortedOptions() {
        synchronized (optionsSortedByTextLock) {
            if (null != optionsSortedByText)
                return;
            Map<String, List<Option>> tempOptionsSortedByText = new HashMap<>();
            for (Option option : options) {
                for (String lang : option.getI18nText().getAvailableLanguages()) {
                    List<Option> optionListForLang = tempOptionsSortedByText.get(lang);
                    if (optionListForLang == null) {
                        optionListForLang = new ArrayList<Option>(options.size());
                        tempOptionsSortedByText.put(lang, optionListForLang);
                    }
                    optionListForLang.add(option);
                }
            }
            if(useGivenOrder == null || useGivenOrder.equals(false)) {
                for (Map.Entry<String, List<Option>> entry : tempOptionsSortedByText.entrySet()) {
                    List<Option> optionList = entry.getValue();
                    final String lang = entry.getKey();
                    Collections.sort(optionList, new Comparator<Option>() {
                        @Override
                        public int compare(Option o1, Option o2) {
                            String o1Trans = o1.getI18nText().getText(lang);
                            String o2Trans = o2.getI18nText().getText(lang);
                            if (keepFirst != null) {
                                for (String value : keepFirst) {
                                    if (value.equals(o1.getValue())) {
                                        return o1.getValue().equals(o2.getValue()) ? 0 : -1;
                                    } else if (value.equals(o2.getValue())) {
                                        return o1.getValue().equals(o2.getValue()) ? 0 : 1;
                                    }
                                }
                            }
                            return o1Trans.compareTo(o2Trans);
                        }
                    });
                }
            }
            this.optionsSortedByText = ImmutableMap.copyOf(tempOptionsSortedByText);
        }
    }

    private void initOptionsMap(){
        synchronized (optionsMapLock){
            if (null != optionsMap)
                return;
            Map<String, Option> tempOptionsMap = new LinkedHashMap<String, Option>();
            for (Option option : options) {
                tempOptionsMap.put(option.getValue(), option);
            }
            this.optionsMap = ImmutableMap.copyOf(tempOptionsMap);
        }
    }

    @Override
    public List<Element> getChildren() {
        List<Element> childList = super.getChildren();
        List<Element> listOfElements = new ArrayList<Element>(childList.size()+ options.size());
        listOfElements.addAll(childList);
        for (Option option : options) {
            listOfElements.addAll(option.getChildren());
        }
        return Collections.unmodifiableList(listOfElements);
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


