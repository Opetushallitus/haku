package fi.vm.sade.haku.virkailija.viestintapalvelu;

import org.apache.http.HttpResponse;

/**
 * Interface for creating PDF document
 * @author vehei1
 *
 */
public interface PDFService {
	/**
	 * Gets an URI to created PDF document
	 *  
	 * @param urlToApplicationPrint The url to application printing
	 * @return The URI of created PDF document
	 */
	public HttpResponse getUriToPDF(String urlToApplicationPrint);
	
	/**
	 * Gets the PDF document of application
	 * 
	 * @param applicationOid Oid for PDF printing
	 * @return The HttpResponse which contains the PDF document
	 */
	public HttpResponse getPDF(String applicationOid);
}
