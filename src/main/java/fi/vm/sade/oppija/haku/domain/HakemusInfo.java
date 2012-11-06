package fi.vm.sade.oppija.haku.domain;

import fi.vm.sade.oppija.haku.domain.elements.Form;

import java.util.List;

/**
 * @author jukka
 * @version 10/17/1212:50 PM}
 * @since 1.1
 */
public class HakemusInfo {
    final Hakemus hakemus;
    final Form form;
    final ApplicationPeriod applicationPeriod;
    private List<Preference> preferences;


    public HakemusInfo(Hakemus hakemus, Form form, ApplicationPeriod applicationPeriod) {
        this.hakemus = hakemus;
        this.form = form;
        this.applicationPeriod = applicationPeriod;
        this.preferences = new PreferenceHelper(hakemus.getValues()).getOpetuspisteet();
    }

    public Hakemus getHakemus() {
        return hakemus;
    }

    public Form getForm() {
        return form;
    }

    public ApplicationPeriod getApplicationPeriod() {
        return applicationPeriod;
    }

    public List<Preference> getPreferences() {
        return preferences;
    }
}
