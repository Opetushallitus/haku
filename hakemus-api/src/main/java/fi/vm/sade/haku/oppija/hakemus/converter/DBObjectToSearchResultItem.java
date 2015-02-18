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

package fi.vm.sade.haku.oppija.hakemus.converter;

import com.google.common.base.Function;
import com.mongodb.DBObject;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationSearchResultItemDTO;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.SocialSecurityNumber;
import fi.vm.sade.haku.oppija.lomake.service.EncrypterService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * @author Mikko Majapuro
 */
@Service
public class DBObjectToSearchResultItem implements Function<DBObject, ApplicationSearchResultItemDTO> {

    public static final String[] KEYS = {
            "oid",
            "received",
            "state",
            "personOid",
            "answers.henkilotiedot.Henkilotunnus",
            "answers.henkilotiedot.Etunimet",
            "answers.henkilotiedot.Sukunimi",
            "answers.henkilotiedot.syntymaaika" };

    private final EncrypterService encrypterService;

    @Autowired
    public DBObjectToSearchResultItem(@Qualifier("aesEncrypter") EncrypterService encrypterService) {
        this.encrypterService = encrypterService;
    }

    @Override
    public ApplicationSearchResultItemDTO apply(DBObject dbObject) {
        if (dbObject != null) {
            Object dbReceived = dbObject.get("received");
            Date received = dbReceived != null ? new Date((long) dbObject.get("received")) : null;
            ApplicationSearchResultItemDTO item = new ApplicationSearchResultItemDTO();
            item.setOid((String) dbObject.get("oid"));
            item.setPersonOid((String) dbObject.get("personOid"));
            item.setReceived(received);
            if (dbObject.containsField("state")) {
                item.setState(Application.State.valueOf((String) dbObject.get("state")));
            }

            final Map<String, Map<String, String>> answers = (Map<String, Map<String, String>>) dbObject.get("answers");
            if (answers != null) {
                final Map<String, String> henkilotiedot = answers.get("henkilotiedot");
                if (henkilotiedot != null) {
                    item.setFirstNames(henkilotiedot.get(OppijaConstants.ELEMENT_ID_FIRST_NAMES));
                    item.setLastName(henkilotiedot.get(OppijaConstants.ELEMENT_ID_LAST_NAME));
                    if (henkilotiedot.containsKey(SocialSecurityNumber.HENKILOTUNNUS)) {
                        final String hetu = henkilotiedot.get(SocialSecurityNumber.HENKILOTUNNUS);
                        item.setSsn(encrypterService.decrypt(hetu));
                    } else if (henkilotiedot.containsKey(OppijaConstants.ELEMENT_ID_DATE_OF_BIRTH)) {
                        item.setSsn(henkilotiedot.get(OppijaConstants.ELEMENT_ID_DATE_OF_BIRTH));
                    }
                }
            }
            return item;
        }
        return null;
    }
}
