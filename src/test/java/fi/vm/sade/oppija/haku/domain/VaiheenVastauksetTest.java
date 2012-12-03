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

package fi.vm.sade.oppija.haku.domain;

import fi.vm.sade.oppija.lomake.domain.HakuLomakeId;
import fi.vm.sade.oppija.lomake.domain.VaiheenVastaukset;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class VaiheenVastauksetTest {
    private final HakuLomakeId hakuLomakeId = new HakuLomakeId("appid", "formid");
    private final HashMap<String, String> vastaukset = new HashMap<String, String>();
    private final String vaiheid = "vaiheid";
    private final VaiheenVastaukset vaihe = new VaiheenVastaukset(hakuLomakeId, vaiheid, vastaukset);

    @Test
    public void testGetHakemusId() throws Exception {
        assertEquals(hakuLomakeId, vaihe.getHakuLomakeId());
    }

    @Test
    public void testGetVaiheId() throws Exception {
        assertEquals(vaiheid, vaihe.getVaiheId());

    }

    @Test
    public void testGetVastaukset() throws Exception {
        assertEquals(vastaukset, vaihe.getVastaukset());
    }
}
