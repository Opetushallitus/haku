package fi.vm.sade.haku.virkailija.koulutusinformaatio.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import fi.vm.sade.haku.virkailija.koulutusinformaatio.KoulutusinformaatioService;
import fi.vm.sade.koulutusinformaatio.domain.dto.AddressDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.LearningOpportunityProviderDTO;

@Service
@Profile(value = {"dev", "it"})
public class KoulutusinformaatioServiceMockImpl implements
		KoulutusinformaatioService {

	@Override
	public ApplicationOptionDTO getApplicationOption(String oid) {
		ApplicationOptionDTO applicationOption = new ApplicationOptionDTO();
		applicationOption.setName("MockKoulutus");
        LearningOpportunityProviderDTO provider = new LearningOpportunityProviderDTO();
        provider.setName("MockKoulu");
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setPostOffice("HELSINKI");
        addressDTO.setPostalCode("00100");
        addressDTO.setStreetAddress("Katukuja 1");
        provider.setPostalAddress(addressDTO);
        applicationOption.setProvider(provider);
        applicationOption.setAttachments(new ArrayList());
		return applicationOption;
	}

	@Override
	public List<ApplicationOptionDTO> getApplicationOptions(List<String> oids) {
		List<ApplicationOptionDTO> applicationOptions = new ArrayList<ApplicationOptionDTO>();
		applicationOptions.add(new ApplicationOptionDTO());
		return applicationOptions;
	}

    @Override
    public ApplicationOptionDTO getApplicationOption(String aoOid, String lang) {
        return getApplicationOption(aoOid);
    }

}
