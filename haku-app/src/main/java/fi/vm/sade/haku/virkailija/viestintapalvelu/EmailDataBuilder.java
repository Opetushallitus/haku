package fi.vm.sade.haku.virkailija.viestintapalvelu;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import fi.vm.sade.haku.virkailija.authentication.Person;
import fi.vm.sade.haku.virkailija.viestintapalvelu.constants.ViestintapalveluConstants;
import fi.vm.sade.ryhmasahkoposti.api.dto.EmailAttachment;
import fi.vm.sade.ryhmasahkoposti.api.dto.EmailData;
import fi.vm.sade.ryhmasahkoposti.api.dto.EmailMessage;
import fi.vm.sade.ryhmasahkoposti.api.dto.EmailRecipient;

@Component
public class EmailDataBuilder {
    private MessageSource messageSource;
    
    @Autowired
    public EmailDataBuilder(MessageSource messageSource) {
    	this.messageSource = messageSource;
    }
    
	public EmailData build(Person applicant, Person user, byte[] pdf) {
		EmailData emailData = new EmailData();
		emailData.setRecipient(getEmailRecipientList(applicant));
		
		EmailMessage emailMessage = getEmailMessage(user, applicant);
		emailMessage.setAttachments(getEmailAttachmentList(pdf));
		emailData.setEmail(emailMessage);
		
		return emailData;
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
		
		Locale locale = new Locale("FI");
		if (applicant.getContactLanguage() != null && !applicant.getContactLanguage().isEmpty()) {
			locale = new Locale(applicant.getContactLanguage());
		}
		
		emailMessage.setBody(messageSource.getMessage("application.email.body", null, locale));
		emailMessage.setCallingProcess(ViestintapalveluConstants.APPLICATION_CALLING_PROCESS);
		emailMessage.setCharset(ViestintapalveluConstants.APPLICATION_CHARSET);
		emailMessage.setFrom(ViestintapalveluConstants.APPLICATION_FROM);
		emailMessage.setHtml(false);
		emailMessage.setLanguageCode(locale.getLanguage());
		emailMessage.setOrganizationOid("");
		emailMessage.setReplyTo("");
		emailMessage.setSenderOid(user.getPersonOid());
		emailMessage.setSubject(messageSource.getMessage("application.email.subject", null, locale));
		emailMessage.setTemplateName("");
				
		return emailMessage;
	}

	private List<EmailRecipient> getEmailRecipientList(Person applicant) {
		List<EmailRecipient> emailRecipients = new ArrayList<EmailRecipient>();

		Locale locale = new Locale("FI");
		if (applicant.getContactLanguage() != null && !applicant.getContactLanguage().isEmpty()) {
			locale = new Locale(applicant.getContactLanguage());
		}
		
		EmailRecipient emailRecipient = new EmailRecipient();
		emailRecipient.setEmail(applicant.getEmail());
		emailRecipient.setLanguageCode(locale.getLanguage());
		emailRecipient.setOid(applicant.getPersonOid());
		emailRecipient.setOidType("");
		emailRecipient.setRecipientReplacements(null);
		
		emailRecipients.add(emailRecipient);
		return emailRecipients;
	}
}
