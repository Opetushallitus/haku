package fi.vm.sade.haku.oppija.common.koulutusinformaatio.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import fi.vm.sade.haku.oppija.common.koulutusinformaatio.KoulutusinformaatioService;
import fi.vm.sade.koulutusinformaatio.domain.dto.AddressDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionSearchResultDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.LearningOpportunityProviderDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.LearningOpportunitySearchResultDTO;

@Service
@Profile(value = {"dev", "it"})
public class KoulutusinformaatioServiceMockImpl extends KoulutusinformaatioService {
    final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private List<LearningOpportunitySearchResultDTO> organizations = Arrays.asList(
            mesta("1.2.246.562.10.89537774706", "FAKTIA, Espoo op"),
            mesta("1.2.246.562.10.19001332592", "Kiipulan ammattiopisto, Kiipulan toimipaikka"),
            mesta("1.2.246.562.10.10108401950", "Espoon kaupunki"),
            mesta("1.2.246.562.10.51872958189", "Stadin ammattiopisto, Sturenkadun toimipaikka"),
            mesta("1.2.246.562.10.35241670047", "Anna Tapion koulu"),
            mesta("1.2.246.562.10.35241670048", "Urheilijoiden koulu"),
            mesta("1.2.246.562.10.55918814447", "Oulun yliopisto, Humanistinen tiedekunta"),
            mesta("1.2.246.562.10.62355244518", "Yrkeshögskolan Novia, Raasepori"),
            mesta("1.2.246.562.10.99415780891", "Yrkeshögskolan Novia, Pietarsaari"),
            mesta("1.2.246.562.10.61397511793", "Helsingin yliopisto, Humanistinen tiedekunta"),
            mesta("1.2.246.562.10.75213421979", "Metropolia AMK, Espoo, Vanha maantie (Leppävaara)"),
            mesta("1.2.246.562.10.14842710486", "Diakonia-ammattikorkeakoulu, Järvenpään toimipiste"),
            mesta("1.2.246.562.10.64213824028", "Diakonia-ammattikorkeakoulu, Helsingin toimipiste"),
            mesta("1.2.246.562.10.78522729439", "Taideyliopisto,  Sibelius-Akatemia")
        );

    private Map<String, ApplicationOptionDTO> optionMap() {
        try {
            // Sample URL for this mock data: https://testi.opintopolku.fi/ao/1.2.246.562.20.18131989511
            return objectMapper.readValue(getClass().getResourceAsStream("/mockdata/koulutukset.json"), new TypeReference<Map<String, ApplicationOptionDTO>>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, List<ApplicationOptionSearchResultDTO>> searchOptionMap() {
        try {
            // Sample URL for this mock data: https://testi.opintopolku.fi/ao/search/1.2.246.562.29.95390561488/1.2.246.562.10.75213421979?uiLang=fi&ongoing=true
            return objectMapper.readValue(getClass().getResourceAsStream("/mockdata/koulutukset-search.json"), new TypeReference<Map<String, List<ApplicationOptionSearchResultDTO>>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ApplicationOptionDTO getApplicationOption(final String oid) {
        ApplicationOptionDTO dto = optionMap().get(oid);
        if (dto == null) {
            LoggerFactory.getLogger(KoulutusinformaatioServiceMockImpl.class).warn("ApplicationOption not found: " + oid + " -> returning default");
            dto = new ApplicationOptionDTO();
            dto.setName("MockKoulutus");
            LearningOpportunityProviderDTO provider = new LearningOpportunityProviderDTO();
            provider.setName("MockKoulu");
            provider.setApplicationSystemIds(new HashSet(Arrays.asList("1.2.246.562.5.2014022711042555034240")));
            AddressDTO addressDTO = new AddressDTO();
            addressDTO.setPostOffice("HELSINKI");
            addressDTO.setPostalCode("00100");
            addressDTO.setStreetAddress("Katukuja 1");
            provider.setPostalAddress(addressDTO);
            dto.setProvider(provider);
            dto.setAttachments(new ArrayList());
            dto.setOrganizationGroups(new ArrayList());
            dto.setTeachingLanguages(Arrays.asList("FI"));
            dto.setEducationCodeUri("koulutus_039998");
            dto.setAoIdentifier("019");
            dto.setRequiredBaseEducations(Arrays.asList("1"));
            return dto;
        }
        return dto;
    }


    @Override
    public ApplicationOptionDTO getApplicationOption(final String oid, final String lang) {
        return getApplicationOption(oid);
    }

    public List<LearningOpportunitySearchResultDTO> organizationSearch(final String term, final String baseEducation) {
        List<LearningOpportunitySearchResultDTO> result = new ArrayList<>();
        for (LearningOpportunitySearchResultDTO organization: organizations) {
            if (term.equals("*") || organization.getName().toLowerCase().contains(term.toLowerCase())) {
                if (!organization.getId().equals("1.2.246.562.10.51872958189") || baseEducation.equals("9")) {
                    result.add(organization);
                }
            }
        }
        return result;
    }

    private LearningOpportunitySearchResultDTO mesta(String id, String name) {
        final LearningOpportunitySearchResultDTO mesta = new LearningOpportunitySearchResultDTO();
        mesta.setId(id);
        mesta.setName(name);
        return mesta;
    }

    public List<ApplicationOptionSearchResultDTO> hakukohdeSearch(final String lopId, final String baseEducation) {
        List<ApplicationOptionSearchResultDTO> applicationOptions = searchOptionMap().get(lopId);
        if (applicationOptions == null) applicationOptions = searchOptionMap().get(lopId + "/" + baseEducation);
        if (applicationOptions == null) applicationOptions = Collections.EMPTY_LIST;
        return applicationOptions;
    }
}
