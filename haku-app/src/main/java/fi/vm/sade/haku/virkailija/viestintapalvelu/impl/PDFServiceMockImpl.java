package fi.vm.sade.haku.virkailija.viestintapalvelu.impl;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import fi.vm.sade.haku.virkailija.viestintapalvelu.ApplicationPrintViewService;
import fi.vm.sade.haku.virkailija.viestintapalvelu.PDFService;

@Service
@Profile(value = {"dev", "it"})
public class PDFServiceMockImpl implements PDFService {

    @Autowired
    public PDFServiceMockImpl(ApplicationPrintViewService applicationPrintViewService) {
    }
    
	@Override
	public HttpResponse getUriToPDF(String url) {
        return getPDF(url);
	}

    @Override
	public HttpResponse getPDF(String url) {
    	HttpEntity entity = new StringEntity("pdf", ContentType.create("application/pdf", "UTF-8"));
    	HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
    	response.setEntity(entity);
		return response;
	}
}
