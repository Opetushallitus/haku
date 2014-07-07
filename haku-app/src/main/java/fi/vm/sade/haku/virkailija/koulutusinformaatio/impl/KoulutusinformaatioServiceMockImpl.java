package fi.vm.sade.haku.virkailija.koulutusinformaatio.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import fi.vm.sade.haku.virkailija.koulutusinformaatio.KoulutusinformaatioService;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionDTO;

@Service
@Profile(value = {"dev", "it"})
public class KoulutusinformaatioServiceMockImpl implements
		KoulutusinformaatioService {

	@Override
	public ApplicationOptionDTO getApplicationOption(String oid) {
		ApplicationOptionDTO applicationOption = new ApplicationOptionDTO();
		return applicationOption;
	}

	@Override
	public List<ApplicationOptionDTO> getApplicationOptions(List<String> oids) {
		List<ApplicationOptionDTO> applicationOptions = new ArrayList<ApplicationOptionDTO>();
		applicationOptions.add(new ApplicationOptionDTO());
		return applicationOptions;
	}

}
