package fi.vm.sade.oppija.common.organisaatio.impl;

import com.google.common.collect.Lists;
import fi.vm.sade.oppija.common.organisaatio.Organization;
import fi.vm.sade.oppija.common.organisaatio.SearchCriteria;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioSearchCriteriaDTO;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
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
        List<OrganisaatioDTO> serviceResult = new ArrayList<OrganisaatioDTO>();
        OrganisaatioDTO organisaatioDTO= new OrganisaatioDTO();
        organisaatioDTO.setOid(OID);
        organisaatioDTO.setNimi(new MonikielinenTekstiTyyppi(Lists.newArrayList(new MonikielinenTekstiTyyppi.Teksti("fi", "nimi"))));
        serviceResult.add(organisaatioDTO);
        OrganisaatioService organisaatioService = mock(OrganisaatioService.class);
        organizationServiceImpl = new OrganizationServiceImpl(organisaatioService);
        when(organisaatioService.searchOrganisaatios(any(OrganisaatioSearchCriteriaDTO.class))).thenReturn(serviceResult);
    }


    @Test
    public void testSearch() throws IOException {
        List<Organization> search = organizationServiceImpl.search(new SearchCriteria());
        assertEquals("Wrong oid ", OID, search.get(0).getOid());
    }
}
