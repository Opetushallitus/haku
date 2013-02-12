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
package fi.vm.sade.oppija.common.valintaperusteet.impl;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

import com.google.common.collect.Lists;

import fi.vm.sade.oppija.common.valintaperusteet.AdditionalQuestions;

public class ValinaperusteetServiceMockImplTest {

    @Test
    public void testBuildInData() throws IOException {
        ValintaperusteetServiceMockImpl mock = new ValintaperusteetServiceMockImpl();
        AdditionalQuestions aq = mock.retrieveAdditionalQuestions(Lists.newArrayList(new String[] { "1234567" }));
        Assert.assertEquals(2, aq.getQuestistionsForHakukohde("1234567").size());
        Assert.assertEquals(0, aq.getQuestistionsForHakukohde("12345678").size());
    }

}
