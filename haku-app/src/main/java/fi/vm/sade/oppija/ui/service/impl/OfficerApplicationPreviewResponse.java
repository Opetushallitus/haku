package fi.vm.sade.oppija.ui.service.impl;

import fi.vm.sade.oppija.common.valintaperusteet.AdditionalQuestions;
import fi.vm.sade.oppija.lomake.domain.elements.Element;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.ui.service.UIServiceResponse;
import org.apache.commons.lang.Validate;

public class OfficerApplicationPreviewResponse extends UIServiceResponse {

    public static final String FORM = "form";
    public static final String ELEMENT = "element";
    public static final String ADDITIONAL_QUESTIONS = "additionalQuestions";
    public static final String TEMPLATE = "template";

    public void setForm(final Form form) {
        Validate.notNull(form, "Form was null");
        this.addObjectToModel(FORM, form);
    }

    public void setElement(final Element element) {
        Validate.notNull(element, "Element was null");
        this.addObjectToModel(ELEMENT, element);
        this.addObjectToModel(TEMPLATE, element.getType());
    }

    public void setAdditionalQuestions(final AdditionalQuestions additionalQuestions) {
        Validate.notNull(additionalQuestions, "AdditionalQuestions was null");
        this.addObjectToModel(ADDITIONAL_QUESTIONS, additionalQuestions);
    }
}
