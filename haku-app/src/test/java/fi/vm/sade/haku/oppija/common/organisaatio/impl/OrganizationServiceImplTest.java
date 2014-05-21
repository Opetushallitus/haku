package fi.vm.sade.haku.oppija.common.organisaatio.impl;

import fi.vm.sade.haku.oppija.common.organisaatio.Organization;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.organisaatio.service.search.OrganisaatioSearchService;
import fi.vm.sade.organisaatio.service.search.SearchCriteria;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        OrganisaatioSearchService organisaatioService = mock(OrganisaatioSearchService.class);
        organizationServiceImpl = new OrganizationServiceImpl(organisaatioService);
        when(organisaatioService.searchBasicOrganisaatios(any(SearchCriteria.class))).thenReturn(serviceResult);
    }


    @Test
    public void testSearch() throws IOException {
        List<Organization> search = organizationServiceImpl.search(new SearchCriteria());
        assertEquals("Wrong oid ", OID, search.get(0).getOid());
    }
}
