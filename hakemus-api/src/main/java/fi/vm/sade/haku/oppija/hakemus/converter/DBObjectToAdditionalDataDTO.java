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
import com.mongodb.DBObject;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationAdditionalDataDTO;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * @author majapuro
 */
public class DBObjectToAdditionalDataDTO implements Function<DBObject, ApplicationAdditionalDataDTO> {

    public static final String[] KEYS = {"oid", "personOid", "answers.henkilotiedot", "additionalInfo" };

    @Override
    public ApplicationAdditionalDataDTO apply(DBObject applicationObject) {
        ApplicationAdditionalDataDTO dto = new ApplicationAdditionalDataDTO();
        dto.setOid((String)applicationObject.get("oid"));
        dto.setPersonOid((String)applicationObject.get("personOid"));
        Map answers = (Map) applicationObject.get("answers");
        final Map<String, String> henkilotiedot = null != answers ? (Map<String,String>) answers.get("henkilotiedot"): null;
        if (henkilotiedot != null) {
            dto.setFirstNames(henkilotiedot.get(OppijaConstants.ELEMENT_ID_FIRST_NAMES));
            dto.setLastName(henkilotiedot.get(OppijaConstants.ELEMENT_ID_LAST_NAME));
        }
        dto.setAdditionalData((Map<String,String>) applicationObject.get("additionalInfo"));
        return dto;
    }
}
