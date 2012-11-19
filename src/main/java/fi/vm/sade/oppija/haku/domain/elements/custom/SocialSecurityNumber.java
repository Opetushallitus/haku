/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * European Union Public Licence for more details.
 */

package fi.vm.sade.oppija.haku.domain.elements.custom;

import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.oppija.haku.domain.elements.questions.Question;
import fi.vm.sade.oppija.haku.domain.elements.questions.Radio;
import fi.vm.sade.oppija.haku.domain.elements.questions.TextQuestion;

/**
 * @author Hannu Lyytikainen
 */
public class SocialSecurityNumber extends Question {

    private TextQuestion ssn;

    private Radio sex;

    private String maleId;
    private String femaleId;

    public SocialSecurityNumber(@JsonProperty(value = "id") final String id, @JsonProperty(value = "title") final String title) {
        super(id, title);
    }

    public TextQuestion getSsn() {
        return ssn;
    }

    public void setSex(Radio sex) {
        this.sex = sex;
    }

    public void setSsn(TextQuestion ssn) {
        this.ssn = ssn;
    }

    public Radio getSex() {
        return sex;
    }

    public String getMaleId() {
        return maleId;
    }

    public String getFemaleId() {
        return femaleId;
    }

    public void setMaleId(String maleId) {
        this.maleId = maleId;
    }

    public void setFemaleId(String femaleId) {
        this.femaleId = femaleId;
    }
}
