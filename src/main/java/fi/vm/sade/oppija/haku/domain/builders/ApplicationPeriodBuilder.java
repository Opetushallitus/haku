package fi.vm.sade.oppija.haku.domain.builders;

import fi.vm.sade.oppija.haku.domain.ApplicationPeriod;
import fi.vm.sade.oppija.haku.domain.elements.Form;

/**
 * @author jukka
 * @version 9/7/1212:56 PM}
 * @since 1.1
 */
public class ApplicationPeriodBuilder {

    ApplicationPeriod applicationPeriod;

    public ApplicationPeriodBuilder(String id) {
        this.applicationPeriod = new ApplicationPeriod(id);
    }

    public ApplicationPeriodBuilder withForm(Form... forms) {
        for (Form form : forms) {
            applicationPeriod.addForm(form);
        }
        return this;
    }

    public ApplicationPeriod build() {
        return applicationPeriod;
    }
}
