package fi.vm.sade.haku.virkailija.koulutusinformaatio;

import java.util.List;

import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionDTO;

/**
 * The interface for getting education information
 * 
 * @author vehei1
 *
 */
public interface KoulutusinformaatioService {
	/**
	 * Gets application option data by using id
	 * 
	 * @param oid The id of application option
	 * @return The instance of application option data
	 */
	public ApplicationOptionDTO getApplicationOption(String oid);

	/**
	 * Gets the list of application option data by using id's
	 * 
	 * @param oids The list of application option id's
	 * @return The list of application option data
	 */
	public List<ApplicationOptionDTO> getApplicationOptions(List<String> oids);
}
