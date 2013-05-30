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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import fi.vm.sade.oppija.lomake.domain.I18nText;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Question;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Theme extends Titled {

    private static final long serialVersionUID = -1394712276903310469L;
    final Map<String, List<Question>> additionalQuestions;
    final boolean previewable;

    public Theme(@JsonProperty(value = "id") final String id,
                 @JsonProperty(value = "i18nText") final I18nText i18nText,
                 @JsonProperty(value = "additionalQuestions") final Map<String, List<Question>> additionalQuestions,
                 @JsonProperty(value = "previewable") final boolean previewable
    ) {
        super(id, i18nText);
        if (additionalQuestions == null) {
            this.additionalQuestions = ImmutableMap.copyOf(new HashMap<String, List<Question>>(0));
        } else {
            this.additionalQuestions = ImmutableMap.copyOf(additionalQuestions);
        }
        this.previewable = previewable;
    }

    public List<Question> getAdditionalQuestions(final String aoId) {
        List<Question> questions = additionalQuestions.get(aoId);
        if (questions != null) {
            return questions;
        } else {
            return ImmutableList.of();
        }

    }

    public boolean isPreviewable() {
        return previewable;
    }
}
