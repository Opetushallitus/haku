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

package fi.vm.sade.oppija.lomake.domain.rules;


import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import fi.vm.sade.oppija.lomake.domain.elements.Element;
import fi.vm.sade.oppija.lomake.validation.validators.SocialSecurityNumberFieldValidator;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static org.apache.commons.lang.StringUtils.isEmpty;

public final class RegexRule {

    private RegexRule() {
    }

    public static boolean evaluate(final String value, final String expression) {
        return value != null && value.matches(expression);
    }

    public static String toNameSelectorString(final Iterable<String> listOfStrings) {
        Iterable<String> selectors = Iterables.transform(listOfStrings, new Function<String, String>() {
            @Override
            public String apply(final String input) {
                return "[name='" + input + "']"; //NOSONAR
            }
        });
        return toCommaSeparatedString(selectors);
    }


    public static String tochildIdList(final Element element) {
        return Joiner.on(',').skipNulls().join(
                Iterables.transform(element.getChildren(), new Function<Element, String>() {
                    @Override
                    public String apply(final Element element) {
                        return "'" + element.getId() + "'"; //NOSONAR
                    }
                }));
    }

    public static String setToList(final Set<String> names) {
        return Joiner.on(',').skipNulls().join(
                Iterables.transform(names, new Function<String, String>() {
                    @Override
                    public String apply(final String name) {
                        return "'" + name + "'";
                    }
                }));
    }

    public static String toCommaSeparatedString(final Iterable<String> listOfStrings) {
        return Joiner.on(',').skipNulls().join(listOfStrings);
    }

    public static List<Element> getChildren(final Element element, final Map<String, String> data) {
        return element.getChildren(data);
    }

    public static String getDateOfBirth(final Map<String, String> data) {

        if (data.containsKey("syntymaaika")) {
            return data.get("syntymaaika");
        } else if (data.containsKey("Henkilotunnus")) {
            return ssnToDateOfBirth(data.get("Henkilotunnus"));
        }
        return null;

    }

    private static String ssnToDateOfBirth(String ssn) {
        Pattern ssnPattern = Pattern.compile(SocialSecurityNumberFieldValidator.SOCIAL_SECURITY_NUMBER_PATTERN);
        if (isEmpty(ssn) || !ssnPattern.matcher(ssn).matches()) {
            return "";
        }
        HashMap<String, Integer> centuries = new HashMap<String, Integer>();
        centuries.put("+", 1800); // NOSONAR
        centuries.put("-", 1900); // NOSONAR
        centuries.put("a", 2000); // NOSONAR
        centuries.put("A", 2000); // NOSONAR
        DateFormat isoDate = new SimpleDateFormat("dd.MM.yyyy");
        isoDate.setLenient(false);

        String day = ssn.substring(0, 2); // NOSONAR
        String month = ssn.substring(2, 4); // NOSONAR
        String year = Integer.toString((centuries.get(ssn.substring(6, 7)) + // NOSONAR
                Integer.valueOf(ssn.substring(4, 6)))); // NOSONAR
        String dob = day + "." + month + "." + year;
        try {
            isoDate.parse(dob);
            return dob;
        } catch (ParseException pe) {
            // Definitely shouldn't happen, SSN should've been checked before getting into db.
            // Letting it slide now, but I'll fix this. Later. Promise.
            return null;
        }
    }
}
