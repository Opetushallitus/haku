package fi.vm.sade.haku.virkailija.viestintapalvelu.dto;

import java.io.Serializable;

public class ApplicationReplacementDTO implements Serializable {
	private static final long serialVersionUID = 3877960359848208034L;
	private String name;
	private String value;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
}
