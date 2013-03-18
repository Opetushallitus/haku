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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */

package fi.vm.sade.oppija.lomake.domain.elements.custom;

import fi.vm.sade.oppija.lomake.domain.I18nText;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Question;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Radio;
import fi.vm.sade.oppija.lomake.domain.elements.questions.TextQuestion;
import fi.vm.sade.oppija.lomake.validation.Validator;
import fi.vm.sade.oppija.lomake.validation.validators.SocialSecurityNumberFieldValidator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
public class SocialSecurityNumber extends Question {

    private static final long serialVersionUID = -5573908500482185095L;

    public static final String HENKILOTUNNUS = "Henkilotunnus";

    private TextQuestion ssn;

    private Option maleOption;
    private Option femaleOption;
    private String sexId;
    private I18nText sexI18nText;
    public static final String HENKILOTUNNUS_HASH = "Henkilotunnus_digest";

    public SocialSecurityNumber(@JsonProperty(value = "id") final String id,
                                @JsonProperty(value = "i18nText") final I18nText i18nText,
                                @JsonProperty(value = "sexI18nText") final I18nText sexI18nText,
                                @JsonProperty(value = "maleOption") final Option maleOption,
                                @JsonProperty(value = "femaleOption") final Option femaleOption,
                                @JsonProperty(value = "sexId") final String sexId,
                                @JsonProperty(value = "ssn") final TextQuestion ssn) {
        super(id, i18nText);
        this.ssn = ssn;
        this.maleOption = maleOption;
        this.femaleOption = femaleOption;
        this.sexId = sexId;
        this.sexI18nText = sexI18nText;
    }

    public TextQuestion getSsn() {
        return ssn;
    }

    public Option getMaleOption() {
        return maleOption;
    }

    public Option getFemaleOption() {
        return femaleOption;
    }

    public String getSexId() {
        return sexId;
    }

    public I18nText getSexI18nText() {
        return sexI18nText;
    }

    @Override
    @JsonIgnore
    public List<Validator> getValidators() {
        List<Validator> listOfValidators = new ArrayList<Validator>();
        listOfValidators.addAll(this.ssn.getValidators());
        listOfValidators.add(new SocialSecurityNumberFieldValidator(ssn.getId()));
        listOfValidators.addAll(this.validators);
        return listOfValidators;
    }
}
