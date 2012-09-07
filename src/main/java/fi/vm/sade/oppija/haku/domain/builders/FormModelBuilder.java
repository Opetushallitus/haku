package fi.vm.sade.oppija.haku.domain.builders;

import fi.vm.sade.oppija.haku.domain.ApplicationPeriod;
import fi.vm.sade.oppija.haku.domain.FormModel;

/**
 * @author jukka
 * @version 9/7/1212:47 PM}
 * @since 1.1
 */
public class FormModelBuilder {

    FormModel formModel = new FormModel();

    public FormModel build() {
        return formModel;
    }

    public FormModelBuilder withApplicationPeriods(ApplicationPeriod... periods) {
        for (ApplicationPeriod applicationPeriod : periods) {
            this.formModel.addApplicationPeriod(applicationPeriod);
        }
        return this;
    }

}
