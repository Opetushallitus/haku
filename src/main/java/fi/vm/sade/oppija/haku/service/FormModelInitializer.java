package fi.vm.sade.oppija.haku.service;

import fi.vm.sade.oppija.haku.domain.ApplicationPeriod;
import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.domain.elements.Form;

import java.util.Map;
import java.util.Set;

public class FormModelInitializer {
    private final FormModel model;

    public FormModelInitializer(FormModel model) {
        this.model = model;
    }

    public void initModel() {
        //init forms
        for (Map.Entry<String, ApplicationPeriod> stringApplicationPeriodEntry : model.getApplicationPerioidMap().entrySet()) {
            final Set<Map.Entry<String, Form>> entries = stringApplicationPeriodEntry.getValue().getForms().entrySet();
            for (Map.Entry<String, Form> entry : entries) {
                entry.getValue().init();
            }
        }
    }
}
