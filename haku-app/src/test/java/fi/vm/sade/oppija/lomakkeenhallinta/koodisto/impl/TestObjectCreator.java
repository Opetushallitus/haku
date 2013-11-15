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

package fi.vm.sade.oppija.lomakkeenhallinta.koodisto.impl;

import com.google.common.collect.Lists;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;

import java.util.List;

public final class TestObjectCreator {
    public static final String LYHYT_NIMI = "lyhyt nimi";
    public static final String NIMI = "pitkä nimi";
    public static final String KOODI_KOODI_URI_AND_ARVO = "koodi arvo";
    public static final KieliType LANG = KieliType.FI;

    public static KoodiMetadataType createKoodiMetadataType() {
        KoodiMetadataType koodiMetadataType = new KoodiMetadataType();
        koodiMetadataType.setKieli(LANG);
        koodiMetadataType.setLyhytNimi(LYHYT_NIMI);
        koodiMetadataType.setNimi(NIMI);
        return koodiMetadataType;
    }

    public static KoodiType createKoodiType(final String koodiArvo) {
        KoodiType koodiType = new KoodiType();
        koodiType.setKoodiUri(koodiArvo);
        koodiType.setKoodiArvo(koodiArvo);
        koodiType.getMetadata().add(createKoodiMetadataType());
        return koodiType;
    }

    public static List<KoodiType> createKoodiTypeList() {
        return Lists.newArrayList(createKoodiType(KOODI_KOODI_URI_AND_ARVO));
    }
}
