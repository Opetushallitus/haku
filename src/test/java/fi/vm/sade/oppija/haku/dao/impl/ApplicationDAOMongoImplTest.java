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

package fi.vm.sade.oppija.haku.dao.impl;

import fi.vm.sade.oppija.haku.dao.AbstractDAOTest;
import fi.vm.sade.oppija.haku.dao.ApplicationDAO;
import fi.vm.sade.oppija.haku.domain.Hakemus;
import fi.vm.sade.oppija.haku.domain.HakuLomakeId;
import fi.vm.sade.oppija.haku.domain.User;
import fi.vm.sade.oppija.haku.validation.HakemusState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
public class ApplicationDAOMongoImplTest extends AbstractDAOTest {

    public static final User TEST_USER = new User("test");
    public static final User TEST_USER2 = new User("test2");
    public static final String ARVO = "arvo";
    public static final String AVAIN = "avain";
    @Autowired
    @Qualifier("applicationDAOMongoImpl")
    private ApplicationDAO applicationDAO;

    private HakuLomakeId hakuLomakeId;

    public ApplicationDAOMongoImplTest() {
        String id = String.valueOf(System.currentTimeMillis());
        hakuLomakeId = new HakuLomakeId(id, id);
    }

    @Test
    public void testTallennaVaihe() {
        final HashMap<String, String> vaiheenVastaukset = new HashMap<String, String>();
        vaiheenVastaukset.put("avain", "arvo");

        final Hakemus hakemus1 = new Hakemus(hakuLomakeId, TEST_USER, "vaihe1", vaiheenVastaukset);
        final HakemusState hakemus = applicationDAO.tallennaVaihe(new HakemusState(hakemus1, "vaihe1"));
        assertEquals("arvo", hakemus.getHakemus().getVastaukset().get("avain"));
    }

    @Test
    public void testFindAll() throws Exception {
        testTallennaVaihe();
        Hakemus hakemus = applicationDAO.find(hakuLomakeId, TEST_USER);
        assertEquals(ARVO, hakemus.getVastaukset().get(AVAIN));
    }

    @Test
    public void testFindAllNotFound() throws Exception {
        Hakemus hakemus = applicationDAO.find(hakuLomakeId, TEST_USER2);
        assertNotSame(ARVO, hakemus.getVastaukset().get(AVAIN));
    }

    @Override
    protected String getCollectionName() {
        return "hakemus";
    }
}
