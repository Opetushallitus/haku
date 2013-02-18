/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */

package fi.vm.sade.oppija.common.organisaatio.impl;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import fi.vm.sade.oppija.common.organisaatio.Organization;
import fi.vm.sade.oppija.lomake.domain.I18nText;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioPerustietoType;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioTyyppi;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class OrganisaatioPerustietoTypeToOrganizationFunction implements
        Function<OrganisaatioPerustietoType, Organization> {

    private static final String FI = "fi";
    private static final String SV = "sv";
    private static final String EN = "en";

    @Override
    public Organization apply(final OrganisaatioPerustietoType dto) {
        final HashMap<String, String> name = new HashMap<String, String>();
        if (dto.getNimiFi() != null) {
            name.put(FI, dto.getNimiFi());
        }
        if (dto.getNimiSv() != null) {
            name.put(SV, dto.getNimiSv());
        }
        if (dto.getNimiEn() != null) {
            name.put(EN, dto.getNimiEn());
        }
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
