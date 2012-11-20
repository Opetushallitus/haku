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

package fi.vm.sade.oppija.haku.controller;

import fi.vm.sade.oppija.haku.domain.*;
import fi.vm.sade.oppija.haku.service.HakemusService;
import fi.vm.sade.oppija.haku.validation.HakemusState;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class HakemusControllerTest {

    public static final String OID = "oid";
    public static final String APPLICATION_PERIOD_ID = "aid";
    public static final String FORM_ID = "fid";
    public static final String USERNAME = "username";
    public static final Hakemus HAKEMUS = new Hakemus(new HakuLomakeId(APPLICATION_PERIOD_ID, FORM_ID), new User(USERNAME));
    private HakemusController hakemusController;

    @Before
    public void setUp() throws Exception {
        hakemusController = new HakemusController(new HakemusService() {
            @Override
            public List<HakemusInfo> findAll() {
                return null;
            }

            @Override
            public Hakemus getHakemus(HakuLomakeId hakuLomakeId) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }


            @Override
            public HakemusState tallennaVaihe(VaiheenVastaukset vaihe) {
                return null;
            }

            @Override
            public Hakemus getHakemus(String oid) {
                return HAKEMUS;
            }
        });
    }

    @Test
    public void testGetHakemus() throws Exception {
        Hakemus hakemus = hakemusController.getHakemus(OID);
        assertEquals(HAKEMUS.getHakuLomakeId(), hakemus.getHakuLomakeId());
    }
}
