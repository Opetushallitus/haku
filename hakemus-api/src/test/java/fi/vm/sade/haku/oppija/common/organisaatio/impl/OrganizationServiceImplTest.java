package fi.vm.sade.haku.oppija.common.organisaatio.impl;

import fi.vm.sade.haku.oppija.common.organisaatio.Organization;
import fi.vm.sade.organisaatio.api.search.OrganisaatioHakutulos;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.organisaatio.api.search.OrganisaatioSearchCriteria;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class OrganizationServiceImplTest {


    public static final String OID = "1";
    private OrganizationServiceImpl organizationServiceImpl;

    @Before
    public void setUp() throws Exception {
        List<OrganisaatioPerustieto> serviceResult = new ArrayList<OrganisaatioPerustieto>();
        OrganisaatioPerustieto organisaatioDTO = new OrganisaatioPerustieto();
        organisaatioDTO.setOid(OID);
        HashMap<String, String> nimiMap = new HashMap<String, String>();
        nimiMap.put("fi", "nimi");
        organisaatioDTO.setNimi(nimiMap);
        serviceResult.add(organisaatioDTO);
        organizationServiceImpl = new OrganizationServiceImpl();
        // when(organisaatioService.searchBasicOrganisaatios(any(SearchCriteria.class))).thenReturn(serviceResult);
    }


    @Test
    @Ignore
    public void testSearch() throws IOException {
//        CachingRestClient client = mock(CachingRestClient.class);
        OrganisaatioHakutulos hakutulos = new OrganisaatioHakutulos();
        OrganisaatioPerustieto pt = new OrganisaatioPerustieto();
        pt.setOid("1");
//        when(client.get(any(String.class), eq(OrganisaatioHakutulos.class))).thenReturn(hakutulos);
//        OrganizationServiceImpl.setCachingRestClient(client);

        List<Organization> search = organizationServiceImpl.search(new OrganisaatioSearchCriteria());
        assertEquals("Wrong oid ", OID, search.get(0).getOid());
    }


}
