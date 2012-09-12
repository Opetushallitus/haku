package fi.vm.sade.oppija.haku.service;

import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.domain.elements.Category;
import fi.vm.sade.oppija.haku.domain.elements.Form;

public interface FormService {
    FormModel getModel();

    Form getActiveForm(final String applicationPeriodId, final String formId);

    Category getFirstCategory(final String applicationPeriodId, final String formId);
}
