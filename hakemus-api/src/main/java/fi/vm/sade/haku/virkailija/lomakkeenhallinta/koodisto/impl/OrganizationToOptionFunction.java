package fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.impl;

import com.google.common.base.Function;
import fi.vm.sade.haku.oppija.common.organisaatio.Organization;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;

public class OrganizationToOptionFunction implements Function<Organization, Option> {
    public Option apply(Organization organization) {
        return new Option(organization.getName(), organization.getOid());
    }
}
