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

package fi.vm.sade.oppija.lomake.domain.elements.custom;

import fi.vm.sade.oppija.lomake.domain.I18nText;
import fi.vm.sade.oppija.lomake.domain.elements.questions.DropdownSelect;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Question;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Radio;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Hannu Lyytikainen
 */
public class DiscretionaryQuestion extends Radio {

    // id for the follow up question container div
    private String followUpContainerId;

    private DropdownSelect followUp;

    public DiscretionaryQuestion(@JsonProperty(value = "id") String id, @JsonProperty(value = "i18nText") I18nText i18nText,
                                 @JsonProperty(value = "followUpId") final DropdownSelect followUp,
                                 @JsonProperty(value = "followUpContainerId") final String followUpContainerId) {
        super(id, i18nText);
        this.followUp = followUp;
        this.followUpContainerId = followUpContainerId;
    }

    public DropdownSelect getFollowUp() {
        return followUp;
    }

    public String getFollowUpContainerId() {
        return followUpContainerId;
    }
}
