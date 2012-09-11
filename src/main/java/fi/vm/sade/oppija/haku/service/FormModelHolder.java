package fi.vm.sade.oppija.haku.service;

import fi.vm.sade.oppija.haku.domain.ApplicationPeriod;
import fi.vm.sade.oppija.haku.domain.Form;
import fi.vm.sade.oppija.haku.domain.FormModel;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

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

    public synchronized void updateModel(FormModel model) {
        this.formModel = model;

        //init forms
        for (Map.Entry<String, ApplicationPeriod> stringApplicationPeriodEntry : model.getApplicationPerioidMap().entrySet()) {
            final Set<Map.Entry<String, Form>> entries = stringApplicationPeriodEntry.getValue().getForms().entrySet();
            for (Map.Entry<String, Form> entry : entries) {
                entry.getValue().init();
            }
        }
    }
}
