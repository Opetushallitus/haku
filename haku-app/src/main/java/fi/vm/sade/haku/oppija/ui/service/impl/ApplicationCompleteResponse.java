/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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

package fi.vm.sade.haku.oppija.ui.service.impl;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.oppija.ui.service.UIServiceResponse;

import java.util.List;

/**
 * @author Mikko Majapuro
 */
public class ApplicationCompleteResponse extends UIServiceResponse {

    public static final String FORM = "form";
    public static final String DISCRETIONARY_ATTACHMENT_AO_IDS = "discretionaryAttachmentAOIds";
    public static final String APPLICATION_COMPLETE_ELEMENTS = "applicationCompleteElements";

    public void setForm(final Form form) {
        this.addObjectToModel(FORM, form);
    }

    public void setDiscretionaryAttachmentAOIds(final List<String> discretionaryAttachmentAOIds) {
        this.addObjectToModel(DISCRETIONARY_ATTACHMENT_AO_IDS, discretionaryAttachmentAOIds);
    }

    @Override
    public void setApplication(final Application application) {
        this.addObjectToModel(APPLICATION, application);
    }

    public void setApplicationCompleteElements(final List<Element> applicationCompleteElements) {
        this.addObjectToModel(APPLICATION_COMPLETE_ELEMENTS, applicationCompleteElements);
    }
}
