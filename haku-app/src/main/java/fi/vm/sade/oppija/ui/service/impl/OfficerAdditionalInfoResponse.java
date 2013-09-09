package fi.vm.sade.oppija.ui.service.impl;

import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.ui.service.UIServiceResponse;

public class OfficerAdditionalInfoResponse extends UIServiceResponse {
    public static final String APPLICATION = "application";

    @Override
    public void setApplication(final Application application) {
        this.addObjectToModel(APPLICATION, application);
    }
}
