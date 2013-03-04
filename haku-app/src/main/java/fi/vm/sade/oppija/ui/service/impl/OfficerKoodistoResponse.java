package fi.vm.sade.oppija.ui.service.impl;

import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.oppija.ui.service.UIServiceResponse;

import java.util.List;

public class OfficerKoodistoResponse extends UIServiceResponse {
    public static final String ORGANIZATION_TYPES = "organizationTypes";
    public static final String LEARNINGINSTITUTION_TYPES = "learningInstitutionTypes";


    public void setOrganizationTypes(final List<Option> listOfOptions) {
        this.addObjectToModel(ORGANIZATION_TYPES, listOfOptions);
    }

    public void setLearninginstitutionTypes(final List<Option> listOfOptions) {
        this.addObjectToModel(LEARNINGINSTITUTION_TYPES, listOfOptions);
    }
}
