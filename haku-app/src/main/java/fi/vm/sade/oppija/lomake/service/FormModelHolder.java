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

import fi.vm.sade.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.oppija.lomake.domain.FormModel;
import fi.vm.sade.oppija.lomakkeenhallinta.FormGenerator;
import fi.vm.sade.oppija.lomakkeenhallinta.Yhteishaku2013;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FormModelHolder {
    private static final Logger LOG = LoggerFactory.getLogger(FormModelHolder.class);
    private final FormGenerator formGenerator;
    private FormModel formModel;

    public FormModelHolder(final Yhteishaku2013 yhteishaku2013) {
        formModel = new FormModel();
        formModel.addApplicationPeriod(yhteishaku2013.getApplicationPeriod());
        this.formGenerator = null;

    }

    @Autowired
    public FormModelHolder(FormGenerator formGenerator) {
        this.formGenerator = formGenerator;
        formModel = new FormModel();
        //generateAndReplace();
    }

    public FormModel getModel() {
        return formModel;
    }

    public synchronized void updateModel(final FormModel model) {
        this.formModel = model;
    }

    public synchronized boolean generateAndReplace() {
        try {
            formModel = new FormModel();
            List<ApplicationPeriod> generate = formGenerator.generate();
            for (ApplicationPeriod applicationPeriod : generate) {
                formModel.addApplicationPeriod(applicationPeriod);
            }
            return true;
        } catch (Exception e) {
            LOG.info("Error generating forms", e);
            return false;
        }
    }


}
