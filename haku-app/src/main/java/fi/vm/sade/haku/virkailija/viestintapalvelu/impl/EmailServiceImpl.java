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
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.virkailija.authentication.AuthenticationService;
import fi.vm.sade.haku.virkailija.authentication.Person;
import fi.vm.sade.haku.virkailija.viestintapalvelu.EmailDataBuilder;
import fi.vm.sade.haku.virkailija.viestintapalvelu.EmailService;
import fi.vm.sade.haku.virkailija.viestintapalvelu.PDFService;
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
    private ApplicationService applicationService;
    private AuthenticationService authenticationService;
    private PDFService pdfService;
    private EmailDataBuilder emailDataBuilder;
    private CachingRestClient cachingRestClient;

    @Autowired
    public EmailServiceImpl(ApplicationService applicationService, 
    	AuthenticationService authenticationService, PDFService pdfService, EmailDataBuilder emailDataBuilder) {
    	this.applicationService = applicationService;
    	this.authenticationService = authenticationService;
    	this.pdfService = pdfService;
    	this.emailDataBuilder = emailDataBuilder;
    }

	@Override
	public String sendApplicationByEmail(String applicationOID) throws IOException {
		Application application = applicationService.getApplicationByOid(applicationOID);
		
		Person user = authenticationService.getCurrentHenkilo();
		Person applicant = authenticationService.getHenkilo(application.getPersonOid());
		LOGGER.info("authenticationService.getHenkilo(" + application.getPersonOid() + ", Name: " + 
			applicant.getLastName() + ", " + applicant.getFirstNames() + ", Language: " + applicant.getContactLanguage());
		
		byte[] pdf = getPDF(applicationOID);
		
		EmailData emailData = emailDataBuilder.build(applicant, user, pdf);				
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
