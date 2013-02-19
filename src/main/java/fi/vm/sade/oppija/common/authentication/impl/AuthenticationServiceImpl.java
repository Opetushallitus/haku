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

package fi.vm.sade.oppija.common.authentication.impl;

import fi.vm.sade.authentication.service.UserManagementService;
import fi.vm.sade.authentication.service.types.AddHenkiloDataType;
import fi.vm.sade.authentication.service.types.dto.*;
import fi.vm.sade.oppija.common.authentication.AuthenticationService;
import fi.vm.sade.oppija.common.authentication.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * @author Hannu Lyytikainen
 */
@Service
@Profile("default")
public class AuthenticationServiceImpl implements AuthenticationService {

    public static final long ID_117 = 117L;
    public static final long ID_120 = 120L;
    public static final String KIELITYYPPI_SUOMI = "suomi";
    public static final String KIELI_KOODI = "fi";
    private UserManagementService userManagementService;

    @Autowired
    public AuthenticationServiceImpl(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    public String addPerson(Person person) {
        AddHenkiloDataType addHenkiloDataType = new AddHenkiloDataType();
        addHenkiloDataType.setEiSuomalaistaHetua(Boolean.FALSE);
        addHenkiloDataType.setEtunimet(person.getFirstNames());
        addHenkiloDataType.setHenkiloTyyppi(HenkiloTyyppiType.OPPIJA);
        addHenkiloDataType.setHetu(person.getSocialSecurityNumber());
        addHenkiloDataType.setKayttajatunnus(person.getEmail());
        addHenkiloDataType.setKotikunta(person.getHomeCity());
        addHenkiloDataType.setKutsumanimi(person.getNickName());
        addHenkiloDataType.setSukunimi(person.getLastName());
        addHenkiloDataType.setSukupuoli(resolveSexType(person.getSex()));
        addHenkiloDataType.setTurvakielto(person.isSecurityOrder());

        // TODO: resolve proper language when user management service
        // allows adding people with koodisto languahe codes
        KielisyysType contactLanguageType = new KielisyysType();
        contactLanguageType.setKieliKoodi("fi");
        contactLanguageType.setId(ID_117);
        contactLanguageType.setKieliTyyppi(KIELITYYPPI_SUOMI);
        addHenkiloDataType.setAsiointiKieli(contactLanguageType);
        KielisyysType lang = new KielisyysType();
        lang.setId(ID_117);
        lang.setKieliKoodi(KIELI_KOODI);
        lang.setKieliTyyppi(KIELITYYPPI_SUOMI);
        KansalaisuusType nat = new KansalaisuusType();
        nat.setId(ID_120);
        nat.setKansalaisuusKoodi(KIELI_KOODI);
        addHenkiloDataType.getKielisyys().add(lang);
        addHenkiloDataType.getKansalaisuus().add(nat);

        HenkiloType henkiloType = userManagementService.addHenkilo(addHenkiloDataType);

        return henkiloType.getOidHenkilo();
    }

    private SukupuoliType resolveSexType(String sex) {
        //TODO: is there a koodisto for sex?
        return sex.equals("m") ? SukupuoliType.MIES : SukupuoliType.NAINEN;
    }
}
