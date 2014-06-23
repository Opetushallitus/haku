package fi.vm.sade.haku.virkailija.viestintapalvelu.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.ws.rs.core.MediaType;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import fi.vm.sade.generic.rest.CachingRestClient;
import fi.vm.sade.haku.RemoteServiceException;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.virkailija.authentication.AuthenticationService;
import fi.vm.sade.haku.virkailija.authentication.Person;
import fi.vm.sade.haku.virkailija.viestintapalvelu.EmailService;
import fi.vm.sade.haku.virkailija.viestintapalvelu.PDFService;
import fi.vm.sade.haku.virkailija.viestintapalvelu.constants.ViestintapalveluConstants;
import fi.vm.sade.ryhmasahkoposti.api.dto.EmailAttachment;
import fi.vm.sade.ryhmasahkoposti.api.dto.EmailData;
import fi.vm.sade.ryhmasahkoposti.api.dto.EmailMessage;
import fi.vm.sade.ryhmasahkoposti.api.dto.EmailRecipient;
import fi.vm.sade.ryhmasahkoposti.api.dto.EmailSendId;

@Service
public class EmailServiceImpl implements EmailService {
    @Value("${web.url.cas}")
    private String casUrl;
    @Value("${cas.service.viestintapalvelu}")
    private String targetService;
    @Value("${haku.app.username.to.viestintapalvelu}")
    private String clientAppUser;
    @Value("${haku.app.password.to.viestintapalvelu}")
    private String clientAppPass;
    private MessageSource messageSource;
    private ApplicationService applicationService;
    private AuthenticationService authenticationService;
    private PDFService pdfService;
    private CachingRestClient cachingRestClient;

    @Autowired
    public EmailServiceImpl(MessageSource messageSource, ApplicationService applicationService, 
    	AuthenticationService authenticationService, PDFService pdfService) {
    	this.messageSource = messageSource;
    	this.applicationService = applicationService;
    	this.authenticationService = authenticationService;
    	this.pdfService = pdfService;
    }

	@Override
	public String sendApplicationByEmail(String applicationOID) throws IOException {
		Application application = applicationService.getApplicationByOid(applicationOID);
		
		Person user = authenticationService.getCurrentHenkilo();
		Person applicant = authenticationService.getHenkilo(application.getPersonOid());
		
		byte[] pdf = getPDF(applicationOID);
		
		EmailData emailData = new EmailData();
		emailData.setRecipient(getEmailRecipientList(applicant));
		
		EmailMessage emailMessage = getEmailMessage(user, applicant);
		emailMessage.setAttachments(getEmailAttachmentList(pdf));
		emailData.setEmail(emailMessage);
				
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

	private List<EmailAttachment> getEmailAttachmentList(byte[] pdf) {
		List<EmailAttachment> emailAttachments = new ArrayList<EmailAttachment>();
		
		EmailAttachment emailAttachment = new EmailAttachment();
		emailAttachment.setContentType(ViestintapalveluConstants.APPLICATION_CONTENT_TYPE);
		emailAttachment.setData(pdf);
		emailAttachment.setName(ViestintapalveluConstants.APPLICATION_ATTACHMENT_NAME);
		
		emailAttachments.add(emailAttachment);
		return emailAttachments;
	}

	private EmailMessage getEmailMessage(Person user, Person applicant) {		
		EmailMessage emailMessage = new EmailMessage();
		
		Locale locale = new Locale(applicant.getContactLanguage());
		
		emailMessage.setBody(messageSource.getMessage("application.email.body", null, locale));
		emailMessage.setCallingProcess(ViestintapalveluConstants.APPLICATION_CALLING_PROCESS);
		emailMessage.setCharset(ViestintapalveluConstants.APPLICATION_CHARSET);
		emailMessage.setFrom(ViestintapalveluConstants.APPLICATION_FROM);
		emailMessage.setHtml(false);
		emailMessage.setLanguageCode(applicant.getContactLanguage());
		emailMessage.setOrganizationOid("");
		emailMessage.setReplyTo("");
		emailMessage.setSenderOid(user.getPersonOid());
		emailMessage.setSubject(messageSource.getMessage("application.email.subject", null, locale));
		emailMessage.setTemplateName("");
				
		return emailMessage;
	}

	private List<EmailRecipient> getEmailRecipientList(Person applicant) {
		List<EmailRecipient> emailRecipients = new ArrayList<EmailRecipient>();
		
		EmailRecipient emailRecipient = new EmailRecipient();
		emailRecipient.setEmail(applicant.getEmail());
		emailRecipient.setLanguageCode(applicant.getContactLanguage());
		emailRecipient.setOid(applicant.getPersonOid());
		emailRecipient.setOidType("");
		emailRecipient.setRecipientReplacements(null);
		
		emailRecipients.add(emailRecipient);
		return emailRecipients;
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
