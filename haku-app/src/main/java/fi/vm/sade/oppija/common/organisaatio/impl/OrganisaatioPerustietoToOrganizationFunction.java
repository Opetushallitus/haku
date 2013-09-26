package fi.vm.sade.oppija.common.organisaatio.impl;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import fi.vm.sade.oppija.common.organisaatio.Organization;
import fi.vm.sade.oppija.lomake.domain.I18nText;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioTyyppi;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;

import java.util.Date;
import java.util.List;

public class OrganisaatioPerustietoToOrganizationFunction implements
        Function<OrganisaatioPerustieto, Organization> {

    private static final String FI = "fi";
    private static final String SV = "sv";
    private static final String EN = "en";

    @Override
    public Organization apply(OrganisaatioPerustieto input) {
        final String oid = input.getOid();
        final String parentOid = input.getParentOid();

        final List<String> types = Lists.transform(input.getOrganisaatiotyypit(),
                new Function<OrganisaatioTyyppi, String>() {
                    public String apply(OrganisaatioTyyppi src) {
                        return src.toString();
                    }
                });

        final Date startDate = input.getAlkuPvm();
        final Date endDate = input.getLakkautusPvm();

        return new Organization(new I18nText(input.getNimi()), oid, parentOid, types, startDate, endDate);
    }
}
