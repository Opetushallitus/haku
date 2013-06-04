/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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

package fi.vm.sade.oppija.hakemus.converter;

import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.domain.dto.ApplicantDTO;
import fi.vm.sade.oppija.lomakkeenhallinta.util.OppijaConstants;
import org.springframework.core.convert.converter.Converter;

import java.util.Map;

/**
 * @author Mikko Majapuro
 */
public class ApplicationToApplicantConverter implements Converter<Application, ApplicantDTO> {

    @Override
    public ApplicantDTO convert(Application application) {
        ApplicantDTO applicant = new ApplicantDTO();
        applicant.setApplicationOid(application.getOid());
        applicant.setPersonOid(application.getPersonOid());
        final Map<String, String> answers = application.getVastauksetMerged();
        applicant.setFirstName(answers.get(OppijaConstants.ELEMENT_ID_FIRST_NAMES));
        applicant.setLastName(answers.get(OppijaConstants.ELEMENT_ID_LAST_NAME));
        applicant.setSsn(answers.get(OppijaConstants.ELEMENT_ID_SOCIAL_SECURITY_NUMBER));
        applicant.setAdditionalData(application.getAdditionalInfo());
        return applicant;
    }
}
