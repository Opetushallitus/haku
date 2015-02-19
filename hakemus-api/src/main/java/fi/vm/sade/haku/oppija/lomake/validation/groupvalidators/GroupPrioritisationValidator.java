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

package fi.vm.sade.haku.oppija.lomake.validation.groupvalidators;

import fi.vm.sade.haku.oppija.common.koulutusinformaatio.ApplicationOption;
import fi.vm.sade.haku.oppija.common.koulutusinformaatio.ApplicationOptionGroup;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.validation.ApplicationOptionInfo;
import fi.vm.sade.haku.oppija.lomake.validation.GroupRestrictionValidator;

import java.util.*;

public class GroupPrioritisationValidator extends GroupRestrictionValidator {

    private static final String REPLACE_KORKEAMPI = "{hakukohde_korkeampi}";
    private static final String REPLACE_ALEMPI = "{hakukohde_alempi}";

    public GroupPrioritisationValidator(final String groupId, final I18nText errorMessage) {
        super(errorMessage, groupId);
    }

    @Override
    public Map<String, I18nText> validate(SortedSet<ApplicationOptionInfo> inputAosInGroup) {
        Map<String, I18nText> errors = new HashMap<>();
        SortedMap<Integer, Set<ApplicationOptionInfo>> prioritized = new TreeMap<>();
        for (ApplicationOptionInfo aoInfo: inputAosInGroup) {
            for(ApplicationOptionGroup group: aoInfo.ao.getGroups()) {
                if(groupId.equals(group.oid)) {
                    errors.putAll(checkPrioriteetti(prioritized, group, aoInfo));
                }
            }
        }
        return errors;
    }

    private Map<? extends String,? extends I18nText> checkPrioriteetti(SortedMap<Integer, Set<ApplicationOptionInfo>> prioritized, ApplicationOptionGroup group, ApplicationOptionInfo aoInfo) {
        Map<String, I18nText> errors = new HashMap<>();
        Integer prioriteetti = group.prioriteetti == null ?  Integer.MAX_VALUE : group.prioriteetti;
        Set<ApplicationOptionInfo> shouldBeAfter = getAfterValues(prioritized, prioriteetti);
        for (ApplicationOptionInfo after: shouldBeAfter) {
            addErrors(aoInfo, after, errors);
        }
        saveToPrioritized(aoInfo, prioriteetti, prioritized);
        return errors;
    }

    private void addErrors(ApplicationOptionInfo higher, ApplicationOptionInfo lower, Map<String, I18nText> errors) {
        Map<String, String> errorMessages = new HashMap<>();
        for(String lang: errorMessage.getTranslations().keySet()) {
            errorMessages.put(lang, errorMessage.getText(lang).replace(REPLACE_KORKEAMPI, higher.ao.getName()).replace(REPLACE_ALEMPI, lower.ao.getName()));
        }
        errors.put(higher.aoInputId, new I18nText(errorMessages));
        errors.put(lower.aoInputId, new I18nText(errorMessages));
    }

    private void saveToPrioritized(ApplicationOptionInfo aoInfo, Integer prioriteetti, SortedMap<Integer, Set<ApplicationOptionInfo>> prioritized) {
        Set<ApplicationOptionInfo> prioriteettiRyhma = prioritized.get(prioriteetti);
        if(prioriteettiRyhma == null) {
            prioriteettiRyhma = new HashSet<>();
            prioritized.put(prioriteetti, prioriteettiRyhma);
        }
        prioriteettiRyhma.add(aoInfo);
    }

    private Set<ApplicationOptionInfo> getAfterValues(SortedMap<Integer, Set<ApplicationOptionInfo>> prioritized, Integer prioriteetti) {
        Set<ApplicationOptionInfo> shouldBeAfter = new HashSet<>();
        for(Integer prioriteettiRyhma: prioritized.tailMap(prioriteetti).keySet()) {
            if(prioriteettiRyhma > prioriteetti) {
                shouldBeAfter.addAll(prioritized.get(prioriteettiRyhma));
            }
        }
        return shouldBeAfter;
    }
}
