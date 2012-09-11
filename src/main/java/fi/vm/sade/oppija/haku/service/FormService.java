package fi.vm.sade.oppija.haku.service;

import fi.vm.sade.oppija.haku.domain.Form;
import fi.vm.sade.oppija.haku.domain.FormModel;

public interface FormService {
    FormModel getModel();
    Form getActiveForm(final String applicationPeriodId, final String formId);
}
