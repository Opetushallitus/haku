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

package fi.vm.sade.oppija.lomake.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author jukka
 * @version 10/18/129:27 AM}
 * @since 1.1
 */
public class PreferenceHelper {
    static final String PREFERENCE_PREFIX = "preference";
    private static final String OPETUSPISTE_SUFFIX = "-Opetuspiste";
    private static final String KOULUTUS_SUFFIX = "-Koulutus";
    private static final String OPETUSPISTE_KEY_REGEX = PREFERENCE_PREFIX + "\\d*" + OPETUSPISTE_SUFFIX;
    private static final String IDSUFFIX = "-Id";
    private List<Preference> opetuspisteet;


    public PreferenceHelper(Map<String, String> values) {
        parseOpetuspisteet(values);
    }

    private void parseOpetuspisteet(Map<String, String> values) {
        Preference[] opetuspisteet = new Preference[values.size()];
        int count = 0;
        for (Map.Entry<String, String> entry : values.entrySet()) {
            String key = entry.getKey();
            if (key.matches(OPETUSPISTE_KEY_REGEX)) {
                final String integerPartBetweenPrefixAndSuffix =
                        key.substring(PREFERENCE_PREFIX.length(), (key.length() - OPETUSPISTE_SUFFIX.length()));
                Integer value = Integer.parseInt(integerPartBetweenPrefixAndSuffix);
                opetuspisteet[value - 1] =
                        new Preference(value, entry.getValue(), values.get(key + IDSUFFIX),
                                values.get(createKoulutusSuffix(value)), values.get(createKoulutusIdSuffix(value)));
                count++;
            }
        }

        final List<Preference> preferences = Arrays.asList(opetuspisteet);
        this.opetuspisteet = preferences.subList(0, count);
    }

    private String createKoulutusIdSuffix(Integer value) {
        return createKoulutusSuffix(value) + IDSUFFIX;
    }

    private String createKoulutusSuffix(Integer value) {
        return PREFERENCE_PREFIX + value + KOULUTUS_SUFFIX;
    }

    public List<Preference> getOpetuspisteet() {
        return opetuspisteet;
    }
}
