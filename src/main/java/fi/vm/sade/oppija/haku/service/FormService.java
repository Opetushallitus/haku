package fi.vm.sade.oppija.haku.service;

import fi.vm.sade.oppija.haku.domain.Category;
import fi.vm.sade.oppija.haku.domain.Form;
import fi.vm.sade.oppija.haku.domain.FormModel;

public interface FormService {
    FormModel getModel();
    Form getActiveForm(final String applicationPeriodId, final String formId);
    Category getFirstCategory(final String applicationPeriodId, final String formId);
}
