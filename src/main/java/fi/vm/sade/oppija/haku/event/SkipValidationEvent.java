package fi.vm.sade.oppija.haku.event;

import fi.vm.sade.oppija.haku.domain.Hakemus;
import fi.vm.sade.oppija.haku.domain.elements.Form;
import fi.vm.sade.oppija.haku.domain.elements.Vaihe;
import fi.vm.sade.oppija.haku.service.FormService;
import fi.vm.sade.oppija.haku.validation.HakemusState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author jukka
 * @version 10/12/125:40 PM}
 * @since 1.1
 */
@Service
public class SkipValidationEvent extends AbstractEvent {

    private final FormService formService;

    @Autowired
    public SkipValidationEvent(EventHandler eventHandler, @Qualifier("formServiceImpl") FormService formService) {
        this.formService = formService;
        eventHandler.addBeforeValidationEvent(this);
    }

    @Override
    public void process(HakemusState hakemusState) {
        Hakemus hakemus = hakemusState.getHakemus();
        if (hakemusState.getHakemus().getValues().containsKey("enabling-submit")) {
            hakemusState.skipValidation();
            Form activeForm = formService.getActiveForm(hakemus.getHakemusId().getApplicationPeriodId(), hakemus.getHakemusId().getFormId());

            Vaihe category = activeForm.getCategory(hakemus.getHakemusId().getCategoryId());
            hakemusState.addModelObject("category", category);
        }
    }
}