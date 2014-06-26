package fi.vm.sade.haku.virkailija.viestintapalvelu;

/**
 * The interface for getting the print view of application
 * 
 * @author vehei1
 *
 */
public interface ApplicationPrintViewService {
	/**
	 * Gets the print view of application
	 * 
	 * @param applicationOID The oid of application
	 * @return Application print view in HTML
	 */
	public String getApplicationPrintView(String applicationOID);
}
