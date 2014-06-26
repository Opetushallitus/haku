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
	 * @param applicationOID The oid of application
	 * @return The URI of created PDF document
	 */
	public HttpResponse getUriToPDF(String applicationOID);
	
	/**
	 * Gets the PDF document of application
	 * 
	 * @param applicationOID The oid of application
	 * @return The HttpResponse which contains the PDF document
	 */
	public HttpResponse getPDF(String applicationOID);
}
