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

package fi.vm.sade.oppija.lomake.service;

import fi.vm.sade.oppija.lomake.Yhteishaku2013;
import fi.vm.sade.oppija.lomake.domain.FormModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FormModelHolder {

    private FormModel formModel;

    @Autowired
    public FormModelHolder(final Yhteishaku2013 yhteishaku2013) {
        formModel = new FormModel();
        formModel.addApplicationPeriod(yhteishaku2013.getApplicationPeriod());
        FormModelInitializer formModelInitializer = new FormModelInitializer(this.formModel);
        formModelInitializer.initModel();
    }

    public FormModel getModel() {
        return formModel;
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
    }


}
