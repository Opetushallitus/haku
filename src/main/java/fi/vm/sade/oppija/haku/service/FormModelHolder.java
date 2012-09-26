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

    public FormModel getModel() {
        return formModel;
    }

    /**
     * this must be triggered when model changes!
     *
     * @param model new model
     */
    public void updateModel(final FormModel model) {
        new FormModelInitializer(model).initModel();
        this.formModel = model;
    }

}
