package fi.vm.sade.haku.virkailija.viestintapalvelu.dto;

import java.io.Serializable;

public class ApplicationByEmailDTO implements Serializable {
	private static final long serialVersionUID = 4643046790778076496L;
	private String applicationOID;
	private String applicantOID;
	private String applicantEmailAddress;
	private String applicantLanguageCode;
	private String userOID;
	private String userOrganzationOID;
	private ApplicationTemplateDTO applicationTemplate;
	
	public String getApplicationOID() {
		return applicationOID;
	}
	
	public void setApplicationOID(String applicationOID) {
		this.applicationOID = applicationOID;
	}
	
	public String getApplicantOID() {
		return applicantOID;
	}
	
	public void setApplicantOID(String applicantOID) {
		this.applicantOID = applicantOID;
	}
	
	public String getApplicantEmailAddress() {
		return applicantEmailAddress;
	}
	
	public void setApplicantEmailAddress(String applicantEmailAddress) {
		this.applicantEmailAddress = applicantEmailAddress;
	}
	
	public String getApplicantLanguageCode() {
		return applicantLanguageCode;
	}
	
	public void setApplicantLanguageCode(String applicantLanguageCode) {
		this.applicantLanguageCode = applicantLanguageCode;
	}
	
	public String getUserOID() {
		return userOID;
	}

	public void setUserOID(String userOID) {
		this.userOID = userOID;
	}

	public String getUserOrganzationOID() {
		return userOrganzationOID;
	}
	
	public void setUserOrganzationOID(String userOrganzationOID) {
		this.userOrganzationOID = userOrganzationOID;
	}

	public ApplicationTemplateDTO getApplicationTemplate() {
		return applicationTemplate;
	}

	public void setApplicationTemplate(ApplicationTemplateDTO applicationTemplate) {
		this.applicationTemplate = applicationTemplate;
	}
}
