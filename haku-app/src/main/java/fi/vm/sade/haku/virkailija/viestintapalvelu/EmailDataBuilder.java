package fi.vm.sade.haku.virkailija.viestintapalvelu;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import fi.vm.sade.haku.virkailija.viestintapalvelu.constants.ViestintapalveluConstants;
import fi.vm.sade.haku.virkailija.viestintapalvelu.dto.ApplicationByEmailDTO;
import fi.vm.sade.haku.virkailija.viestintapalvelu.dto.ApplicationReplacementDTO;
import fi.vm.sade.ryhmasahkoposti.api.dto.EmailAttachment;
import fi.vm.sade.ryhmasahkoposti.api.dto.EmailData;
import fi.vm.sade.ryhmasahkoposti.api.dto.EmailMessage;
import fi.vm.sade.ryhmasahkoposti.api.dto.EmailRecipient;
import fi.vm.sade.ryhmasahkoposti.api.dto.ReplacementDTO;

@Component
public class EmailDataBuilder {
	public EmailData build(ApplicationByEmailDTO applicationByEmail, byte[] pdf) {
		EmailData emailData = new EmailData();
		List<ReplacementDTO> replacements = getReplacements(applicationByEmail);
		emailData.setReplacements(replacements);
		
		emailData.setRecipient(getEmailRecipientList(applicationByEmail));
		
		EmailMessage emailMessage = getEmailMessage(applicationByEmail);
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

	private EmailMessage getEmailMessage(ApplicationByEmailDTO applicationByEmail) {		
		EmailMessage emailMessage = new EmailMessage();
		
		Locale locale = new Locale("FI");
		if (applicationByEmail.getApplicantLanguageCode() != null) {
			locale = new Locale(applicationByEmail.getApplicantLanguageCode());
		}
		
		emailMessage.setBody("");
		emailMessage.setCallingProcess(ViestintapalveluConstants.APPLICATION_CALLING_PROCESS);
		emailMessage.setCharset(ViestintapalveluConstants.APPLICATION_CHARSET);
		emailMessage.setFrom(ViestintapalveluConstants.APPLICATION_FROM);
		emailMessage.setHtml(false);
		emailMessage.setLanguageCode(locale.getLanguage().toUpperCase());
		emailMessage.setOrganizationOid(applicationByEmail.getUserOrganzationOID());
		emailMessage.setReplyTo("");
		emailMessage.setSenderOid(applicationByEmail.getUserOID());
		emailMessage.setSubject("");
		
		if (applicationByEmail.getApplicationTemplate() != null && 
			applicationByEmail.getApplicationTemplate().getTemplateName() != null) {
			emailMessage.setTemplateName(applicationByEmail.getApplicationTemplate().getTemplateName());
		} else {
			emailMessage.setTemplateName(ViestintapalveluConstants.APPLICATION_TEMPLATE_NAME);
		}
				
		return emailMessage;
	}

	private List<EmailRecipient> getEmailRecipientList(ApplicationByEmailDTO applicationByEmail) {
		List<EmailRecipient> emailRecipients = new ArrayList<EmailRecipient>();

		Locale locale = new Locale("FI");
		if (applicationByEmail.getApplicantLanguageCode() != null) {
			locale = new Locale(applicationByEmail.getApplicantLanguageCode());
		}
		
		EmailRecipient emailRecipient = new EmailRecipient();
		emailRecipient.setEmail(applicationByEmail.getApplicantEmailAddress());
		emailRecipient.setLanguageCode(locale.getLanguage().toUpperCase());
		emailRecipient.setOid(applicationByEmail.getApplicantOID());
		emailRecipient.setOidType("");
		emailRecipient.setRecipientReplacements(null);
		
		emailRecipients.add(emailRecipient);
		return emailRecipients;
	}
	
	private List<ReplacementDTO> getReplacements(ApplicationByEmailDTO applicationByEmail) {
		List<ReplacementDTO> replacements = new ArrayList<ReplacementDTO>();
		
		if (applicationByEmail.getApplicationTemplate().getTemplateReplacements() == null || 
			applicationByEmail.getApplicationTemplate().getTemplateReplacements().isEmpty()) {
			return replacements;
		}
		
		for (ApplicationReplacementDTO templateReplacement : 
			applicationByEmail.getApplicationTemplate().getTemplateReplacements()) {
			ReplacementDTO replacement = new ReplacementDTO();
			replacement.setName(templateReplacement.getName());
			replacement.setDefaultValue(templateReplacement.getValue());
			
			replacements.add(replacement);
		}
		
		return replacements;
	}
}
