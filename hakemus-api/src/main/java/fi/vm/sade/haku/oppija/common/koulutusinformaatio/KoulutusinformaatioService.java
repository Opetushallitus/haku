package fi.vm.sade.haku.oppija.common.koulutusinformaatio;

import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionDTO;

import java.util.ArrayList;
import java.util.List;

public abstract class KoulutusinformaatioService {
	public abstract ApplicationOptionDTO getApplicationOption(String oid);

	public abstract ApplicationOptionDTO getApplicationOption(String aoOid, String lang);


	public List<ApplicationOptionDTO> getApplicationOptions(List<String> oids) {
		List<ApplicationOptionDTO> applicationOptions = new ArrayList<ApplicationOptionDTO>();
		for (String oid : oids) {
			applicationOptions.add(getApplicationOption(oid));
		}
		return applicationOptions;
	}
}
