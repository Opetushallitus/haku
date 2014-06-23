package fi.vm.sade.haku.virkailija.viestintapalvelu.impl;

import fi.vm.sade.haku.virkailija.viestintapalvelu.ApplicationPrintViewService;
import fi.vm.sade.haku.virkailija.viestintapalvelu.PDFService;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile(value = {"dev", "it"})
public class PDFServiceMockImpl implements PDFService {

    @Autowired
    public PDFServiceMockImpl(ApplicationPrintViewService applicationPrintViewService) {
    }
    
	@Override
	public HttpResponse getUriToPDF(String applicationOID) {
        return null;
	}

    @Override
	public HttpResponse getPDF(String applicationOID) {
		return null;
	}
}
