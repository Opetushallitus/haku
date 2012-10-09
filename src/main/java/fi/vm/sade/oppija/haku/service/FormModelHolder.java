/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */

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
