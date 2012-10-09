package fi.vm.sade.oppija.haku.service;

import fi.vm.sade.oppija.haku.domain.FormModel;
import org.springframework.stereotype.Service;

/**
 * @author jukka
 * @version 9/11/122:47 PM}
 * @since 1.1
 */
@Service
public class FormModelHolder {

    private FormModel formModel;
    private ValidatorContainer validatorContainer;

    public FormModel getModel() {
        return formModel;
    }

    public ValidatorContainer getValidatorContainer() {
        return this.validatorContainer;
    }

    /**
     * this must be triggered when model changes!
     *
     * @param model new model
     */
    public void updateModel(final FormModel model) {
        FormModelInitializer formModelInitializer = new FormModelInitializer(model);
        formModelInitializer.initModel();
        this.formModel = model;
        this.validatorContainer = new ValidatorCollector(this.formModel.getApplicationPerioidMap()).collect();

    }


}
