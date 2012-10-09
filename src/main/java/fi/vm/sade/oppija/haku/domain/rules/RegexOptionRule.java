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

package fi.vm.sade.oppija.haku.domain.rules;

import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.oppija.haku.domain.questions.Option;

/**
 * @author jukka
 * @version 10/4/123:25 PM}
 * @since 1.1
 */
public class RegexOptionRule {
    private final String regex;
    private final Option option;

    public RegexOptionRule(@JsonProperty(value = "regex") String regex, @JsonProperty(value = "option") Option option) {
        this.regex = regex;
        this.option = option;
    }

    public String getRegex() {
        return regex;
    }

    public Option getOption() {
        return option;
    }
}
