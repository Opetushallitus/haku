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
	 * @param urlToApplicationPrint The url to application printing
	 * @return Application print view in HTML
	 */
	public String getApplicationPrintView(String urlToApplicationPrint);
}
