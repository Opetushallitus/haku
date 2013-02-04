package fi.vm.sade.oppija.common.organisaatio.impl;

import java.util.HashMap;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import fi.vm.sade.oppija.common.organisaatio.Organization;
import fi.vm.sade.oppija.lomake.domain.I18nText;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioPerustietoType;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioTyyppi;

public class OrganisaatioPerustietoTypeToOrganizationFunction implements
        Function<OrganisaatioPerustietoType, Organization> {

    private static final String FI = "fi";
    private static final String SV = "sv";
    private static final String EN = "en";

    @Override
    public Organization apply(final OrganisaatioPerustietoType dto) {
        final HashMap<String, String> name = new HashMap<String, String>();
        name.put(FI, dto.getNimiFi());
        name.put(SV, dto.getNimiSv());
        name.put(EN, dto.getNimiEn());
        final String oid = dto.getOid();
        final String parentOid = dto.getParentOid();

        final List<Organization.Type> types = Lists.transform(dto.getTyypit(),
                new Function<OrganisaatioTyyppi, Organization.Type>() {
                    public Organization.Type apply(OrganisaatioTyyppi src) {
                        return Organization.Type.valueOf(src.toString());
                    }
                });
        final Organization entity = new Organization(new I18nText("name", name), oid, parentOid, types);
        return entity;
    }
}
