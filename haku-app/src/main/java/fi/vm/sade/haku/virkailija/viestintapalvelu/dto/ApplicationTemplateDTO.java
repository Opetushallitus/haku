package fi.vm.sade.haku.virkailija.viestintapalvelu.dto;

import java.io.Serializable;
import java.util.List;

public class ApplicationTemplateDTO implements Serializable {
	private static final long serialVersionUID = 7759142763825041089L;
	private String templateName;
	private List<ApplicationReplacementDTO> templateReplacements;
	
	public String getTemplateName() {
		return templateName;
	}
	
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	
	public List<ApplicationReplacementDTO> getTemplateReplacements() {
		return templateReplacements;
	}
	
	public void setTemplateReplacements(List<ApplicationReplacementDTO> templateReplacements) {
		this.templateReplacements = templateReplacements;
	}
}
