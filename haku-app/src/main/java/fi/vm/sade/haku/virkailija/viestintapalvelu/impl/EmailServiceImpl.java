package fi.vm.sade.haku.virkailija.viestintapalvelu.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import fi.vm.sade.generic.rest.CachingRestClient;
import fi.vm.sade.haku.RemoteServiceException;
import fi.vm.sade.haku.virkailija.viestintapalvelu.EmailDataBuilder;
import fi.vm.sade.haku.virkailija.viestintapalvelu.EmailService;
import fi.vm.sade.haku.virkailija.viestintapalvelu.PDFService;
import fi.vm.sade.haku.virkailija.viestintapalvelu.dto.ApplicationByEmailDTO;
import fi.vm.sade.ryhmasahkoposti.api.dto.EmailData;
import fi.vm.sade.ryhmasahkoposti.api.dto.EmailSendId;

@Service
@Profile(value = {"default", "devluokka"})
public class EmailServiceImpl implements EmailService {
	private Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);
    @Value("${web.url.cas}")
    private String casUrl;
    @Value("${cas.service.ryhmasahkoposti}")
    private String targetService;
    @Value("${haku.app.username.to.viestintapalvelu}")
    private String clientAppUser;
    @Value("${haku.app.password.to.viestintapalvelu}")
    private String clientAppPass;
    private PDFService pdfService;
    private EmailDataBuilder emailDataBuilder;
    private CachingRestClient cachingRestClient;

    @Autowired
    public EmailServiceImpl(PDFService pdfService, EmailDataBuilder emailDataBuilder) {
    	this.pdfService = pdfService;
    	this.emailDataBuilder = emailDataBuilder;
    }

	@Override
	public String sendApplicationByEmail(ApplicationByEmailDTO applicationByEmail) throws IOException {
		LOGGER.info("EmailServiceImpl.sendApplicationByEmail [applicationOID: " + applicationByEmail.getApplicationOID() + "]");
		byte[] pdf = getPDF(applicationByEmail.getApplicationOID());		
		EmailData emailData = emailDataBuilder.build(applicationByEmail, pdf);
		return sendGroupEmail(emailData);
	}

	private synchronized CachingRestClient getCachingRestClient() {
	    if (cachingRestClient == null) {
	        cachingRestClient = new CachingRestClient();
	        cachingRestClient.setWebCasUrl(casUrl);
	        cachingRestClient.setCasService(targetService);
	        cachingRestClient.setUsername(clientAppUser);
	        cachingRestClient.setPassword(clientAppPass);
	    }
	    return cachingRestClient;
	}
	
	private byte[] getPDF(String applicationOID) throws IOException {
		HttpResponse response = pdfService.getPDF(applicationOID);
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		response.getEntity().writeTo(outputStream);
		byte[] pdf = outputStream.toByteArray();
		
		return pdf;
	}
	
	private String sendGroupEmail(EmailData emailData) {
		Gson gson = new Gson();
		String emailDataJson = gson.toJson(emailData);
		
		String url = "/email/sendPdf";
		CachingRestClient cachingRestClient = getCachingRestClient();

		try {
			HttpResponse response = cachingRestClient.post(url, MediaType.APPLICATION_JSON, emailDataJson);
			String responseJson = EntityUtils.toString(response.getEntity());
			EmailSendId emailSendId = gson.fromJson(responseJson, EmailSendId.class);
			return emailSendId.getId();
		} catch (IOException e) {
            throw new RemoteServiceException(targetService + url, e);
        }
	}
}
