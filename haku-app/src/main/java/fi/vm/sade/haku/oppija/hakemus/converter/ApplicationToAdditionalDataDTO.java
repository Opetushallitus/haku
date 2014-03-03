/*
 * Copyright (c) 2014 The Finnish Board of Education - Opetushallitus
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

package fi.vm.sade.haku.oppija.hakemus.converter;

import com.google.common.base.Function;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationAdditionalDataDTO;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;

import java.util.Map;

/**
 * @author majapuro
 */
public class ApplicationToAdditionalDataDTO implements Function<Application, ApplicationAdditionalDataDTO> {

    @Override
    public ApplicationAdditionalDataDTO apply(Application application) {
        ApplicationAdditionalDataDTO dto = new ApplicationAdditionalDataDTO();
        dto.setOid(application.getOid());
        dto.setPersonOid(application.getPersonOid());
        final Map<String, String> henkilotiedot = application.getAnswers().get("henkilotiedot");
        dto.setFirstNames(henkilotiedot.get(OppijaConstants.ELEMENT_ID_FIRST_NAMES));
        dto.setLastName(henkilotiedot.get(OppijaConstants.ELEMENT_ID_LAST_NAME));
        dto.setAdditionalData(application.getAdditionalInfo());
        return dto;
    }
}
