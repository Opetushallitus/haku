package fi.vm.sade.oppija.common.organisaatio.impl;

import static org.junit.Assert.assertEquals;

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

        final Organization org = function.apply(dto);

        assertEquals("fi", org.getName().getTranslations().get("fi"));
        assertEquals("en", org.getName().getTranslations().get("en"));
        assertEquals("sv", org.getName().getTranslations().get("sv"));
        assertEquals("oid", org.getOid());
        assertEquals("parentOid", org.getParentOid());
    }
}
