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

import java.util.List;
import java.util.Map;

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
                return "[name='" + input + "']";
            }
        });
        return toCommaSeparatedString(selectors);
    }

    public static String toCommaSeparatedString(final Iterable<String> listOfStrings) {
        return Joiner.on(',').skipNulls().join(listOfStrings);
    }

    public static List<Element> getChildren(final Element element, final Map<String, String> data) {
        return element.getChildren(data);
    }
}
