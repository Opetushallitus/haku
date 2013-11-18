package fi.vm.sade.haku.oppija.ui.service.impl;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.ui.service.UIServiceResponse;

public class OfficerAdditionalInfoResponse extends UIServiceResponse {
    public static final String APPLICATION = "application";

    @Override
    public void setApplication(final Application application) {
        this.addObjectToModel(APPLICATION, application);
    }
}
