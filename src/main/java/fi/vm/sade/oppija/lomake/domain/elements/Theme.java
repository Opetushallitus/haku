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

package fi.vm.sade.oppija.lomake.domain.elements;

import fi.vm.sade.oppija.lomake.domain.elements.questions.Question;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * @author jukka
 * @version 9/7/1210:36 AM}
 * @since 1.1
 */
public class Theme extends Titled {

    Map<String, List<Question>> additionalQuestions;

    public Theme(@JsonProperty(value = "id") final String id, @JsonProperty(value = "title") final String title,
                 @JsonProperty(value = "additionalQuestions") Map<String, List<Question>> additionalQuestions) {
        super(id, title);
        this.additionalQuestions = additionalQuestions;
    }

    public Map<String, List<Question>> getAdditionalQuestions() {
        return additionalQuestions;
    }
}
