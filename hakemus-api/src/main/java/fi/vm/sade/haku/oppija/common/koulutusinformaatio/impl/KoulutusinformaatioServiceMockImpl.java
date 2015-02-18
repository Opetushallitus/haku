package fi.vm.sade.haku.oppija.common.koulutusinformaatio.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    @Override
    public ApplicationOptionDTO getApplicationOption(final String oid) {
        ApplicationOptionDTO dto = optionMap.get(oid);
        if (dto == null) {
            LoggerFactory.getLogger(KoulutusinformaatioServiceMockImpl.class).warn("ApplicationOption not found: " + oid + " -> returning default");
            dto = new ApplicationOptionDTO();
            dto.setName("MockKoulutus");
            LearningOpportunityProviderDTO provider = new LearningOpportunityProviderDTO();
            provider.setName("MockKoulu");
            AddressDTO addressDTO = new AddressDTO();
            addressDTO.setPostOffice("HELSINKI");
            addressDTO.setPostalCode("00100");
            addressDTO.setStreetAddress("Katukuja 1");
            provider.setPostalAddress(addressDTO);
            dto.setProvider(provider);
            dto.setAttachments(new ArrayList());
            return dto;
        }
        return dto;
    }


    @Override
    public ApplicationOptionDTO getApplicationOption(final String oid, final String lang) {
        return getApplicationOption(oid);
    }

    final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    Map<String, List<ApplicationOptionSearchResultDTO>> searchOptionMap;
    Map<String, ApplicationOptionDTO> optionMap;
    {
        try {
            searchOptionMap = objectMapper.readValue(getClass().getResourceAsStream("/mockdata/koulutukset-search.json"), new TypeReference<Map<String, List<ApplicationOptionSearchResultDTO>>>() {});
            optionMap = objectMapper.readValue(getClass().getResourceAsStream("/mockdata/koulutukset.json"), new TypeReference<Map<String, ApplicationOptionDTO>>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<LearningOpportunitySearchResultDTO> organizations = Arrays.asList(mesta("1.2.246.562.10.89537774706", "FAKTIA, Espoo op"), mesta("1.2.246.562.10.10108401950", "Espoon kaupunki"), mesta("1.2.246.562.10.51872958189", "Stadin ammattiopisto, Sturenkadun toimipaikka"), mesta("1.2.246.562.10.35241670047", "Anna Tapion koulu"), mesta("1.2.246.562.10.35241670048", "Urheilijoiden koulu"));

    public List<LearningOpportunitySearchResultDTO> organizationSearch(final String term, final String baseEducation) {
        List<LearningOpportunitySearchResultDTO> result = new ArrayList<>();
        for (LearningOpportunitySearchResultDTO organization: organizations) {
            if (organization.getName().toLowerCase().contains(term.toLowerCase())) {
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
        List<ApplicationOptionSearchResultDTO> applicationOptions = searchOptionMap.get(lopId);
        if (applicationOptions == null) applicationOptions = searchOptionMap.get(lopId + "/" + baseEducation);
        if (applicationOptions == null) applicationOptions = Collections.EMPTY_LIST;
        return applicationOptions;
    }
}
