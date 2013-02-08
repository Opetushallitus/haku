package fi.vm.sade.oppija.common.organisaatio.impl;

import java.util.Date;
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
        if(dto.getNimiFi()!=null) name.put(FI, dto.getNimiFi());
        if(dto.getNimiSv()!=null) name.put(SV, dto.getNimiSv());
        if(dto.getNimiEn()!=null) name.put(EN, dto.getNimiEn());
        final String oid = dto.getOid();
        final String parentOid = dto.getParentOid();

        final List<String> types = Lists.transform(dto.getTyypit(),
                new Function<OrganisaatioTyyppi, String>() {
                    public String apply(OrganisaatioTyyppi src) {
                        return src.toString();
                    }
                });
        
        final Date startDate = dto.getAlkuPvm();
        final Date endDate = dto.getLakkautusPvm();
        
        final Organization entity = new Organization(new I18nText("name", name), oid, parentOid, types, startDate, endDate);
        return entity;
    }
}
