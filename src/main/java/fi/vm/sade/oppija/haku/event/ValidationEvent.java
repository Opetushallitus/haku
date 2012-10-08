package fi.vm.sade.oppija.haku.event;

import fi.vm.sade.oppija.haku.domain.Hakemus;
import fi.vm.sade.oppija.haku.service.FormService;
import fi.vm.sade.oppija.haku.validation.FormValidator;
import fi.vm.sade.oppija.haku.validation.HakemusState;
import fi.vm.sade.oppija.haku.validation.ValidationResult;
import fi.vm.sade.oppija.haku.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author jukka
 * @version 9/28/122:42 PM}
 * @since 1.1
 */
@Service
public class ValidationEvent extends AbstractEvent {

    private final FormService formService;

    @Autowired
    public ValidationEvent(EventHandler eventHandler, @Qualifier("formServiceImpl") FormService formService) {
        this.formService = formService;
        eventHandler.addValidationEvent(this);
    }

    @Override
    public void process(HakemusState hakemusState) {
        Hakemus hakemus = hakemusState.getHakemus();
        List<Validator> validators = getValidators(hakemus);
        System.out.println(validators.size());
        ValidationResult validationResult = FormValidator.validate(validators, hakemus.getValues());
        hakemusState.addError(validationResult.getErrorMessages());
    }

    protected List<Validator> getValidators(Hakemus hakemus) {
        return formService.getCategoryValidators(hakemus.getHakemusId());
    }
}
