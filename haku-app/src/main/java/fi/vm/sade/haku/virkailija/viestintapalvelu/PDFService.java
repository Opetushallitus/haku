package fi.vm.sade.haku.virkailija.viestintapalvelu;

import org.apache.http.HttpResponse;

public interface PDFService {
	public HttpResponse getUriToPDF(String applicationOID);
	public HttpResponse getPDF(String applicationOID);
}
