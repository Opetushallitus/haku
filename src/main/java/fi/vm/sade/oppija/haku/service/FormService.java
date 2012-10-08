package fi.vm.sade.oppija.haku.service;

import fi.vm.sade.oppija.haku.domain.ApplicationPeriod;
import fi.vm.sade.oppija.haku.domain.HakemusId;
import fi.vm.sade.oppija.haku.domain.elements.Category;
import fi.vm.sade.oppija.haku.domain.elements.Form;
import fi.vm.sade.oppija.haku.validation.Validator;

import java.util.List;
import java.util.Map;

public interface FormService {

    Form getActiveForm(final String applicationPeriodId, final String formId);

    Category getFirstCategory(final String applicationPeriodId, final String formId);

    Map<String, ApplicationPeriod> getApplicationPerioidMap();

    ApplicationPeriod getApplicationPeriodById(final String applicationPeriodId);

    List<Validator> getCategoryValidators(HakemusId hakemusId);
}
