/*
 *
 *  * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *  *
 *  * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 *  * soon as they will be approved by the European Commission - subsequent versions
 *  * of the EUPL (the "Licence");
 *  *
 *  * You may not use this work except in compliance with the Licence.
 *  * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * European Union Public Licence for more details.
 *
 */
package fi.vm.sade.oppija.common.organisaatio.impl;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

import fi.vm.sade.oppija.common.organisaatio.Organization;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioPerustietoType;

public class OrganisaatioPerustietoTypeToOrganizationFunctionTest {

    final OrganisaatioPerustietoTypeToOrganizationFunction function = new OrganisaatioPerustietoTypeToOrganizationFunction();

    @Test
    public void test() {
        final OrganisaatioPerustietoType dto = new OrganisaatioPerustietoType();
        dto.setNimiFi("fi");
        dto.setNimiEn("en");
        dto.setNimiSv("sv");
        dto.setOid("oid");
        dto.setParentOid("parentOid");
        final Date start = new Date(0);
        final Date end = new Date(1000); 
        dto.setAlkuPvm(start);
        dto.setLakkautusPvm(end);

        final Organization org = function.apply(dto);

        assertEquals("fi", org.getName().getTranslations().get("fi"));
        assertEquals("en", org.getName().getTranslations().get("en"));
        assertEquals("sv", org.getName().getTranslations().get("sv"));
        assertEquals("oid", org.getOid());
        assertEquals("parentOid", org.getParentOid());
        assertEquals(0l, org.getStartDate().getTime());
        assertEquals(1000l, org.getEndDate().getTime());
        
    }
}
