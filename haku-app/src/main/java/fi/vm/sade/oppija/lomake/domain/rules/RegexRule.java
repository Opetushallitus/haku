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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RegexRule {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegexRule.class);

    private RegexRule() {
    }

    public static boolean evaluate(final String value, final String expression) {
        if (value != null) {
            final Pattern compile = Pattern.compile(expression);
            Matcher matcher = compile.matcher(value);
            LOGGER.debug("Using regexp: {} for value: {}, matches: {}", new Object[]{expression, value, matcher.matches()});
            return matcher.matches();
        }
        return false;
    }
}
