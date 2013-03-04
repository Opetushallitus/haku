package fi.vm.sade.oppija.ui.service.impl;

import fi.vm.sade.oppija.lomake.domain.elements.Element;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.ui.service.UIServiceResponse;

public class OfficerApplicationPreviewResponse extends UIServiceResponse {
    public static final String FORM = "form";
    public static final String ELEMENT = "element";
    public static final String ADDITIONAL_QUESTIONS = "additionalQuestions";

    public void setForm(final Form form) {
        this.addObjectToModel(FORM, form);
    }

    public void setElement(final Element element) {
        this.addObjectToModel(ELEMENT, element);
    }


    public void setAdditionalQuestions(final Object object) {
        this.addObjectToModel(ADDITIONAL_QUESTIONS, object);
    }
}
