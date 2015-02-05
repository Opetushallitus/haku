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

package fi.vm.sade.haku.oppija.lomake.domain;

import com.google.common.collect.Lists;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ApplicationSystemTest {

    @Test
    public void testIsActiveSame() throws Exception {
        Date now = new Date();
        List<ApplicationPeriod> applicationPeriods = Lists.newArrayList(new ApplicationPeriod(now, now));
        ApplicationSystem applicationSystem = new ApplicationSystemBuilder().setId("1")
                .setForm(new Form("", ElementUtil.createI18NAsIs("")))
                .setName(ElementUtil.createI18NAsIs(""))
                .setApplicationPeriods(applicationPeriods)
                .setApplicationSystemType(OppijaConstants.HAKUTYYPPI_VARSINAINEN_HAKU)
                .setName(ElementUtil.createI18NAsIs("")).get();
        assertFalse(applicationSystem.isActive());
    }

    @Test
    public void testIsActiveEnd() throws Exception {
        Date start = new Date();
        final Calendar instance = Calendar.getInstance();
        instance.roll(Calendar.YEAR, 1);
        Date end = new Date(instance.getTimeInMillis());
        List<ApplicationPeriod> applicationPeriods = Lists.newArrayList(new ApplicationPeriod(start, end));
        ApplicationSystem applicationSystem = new ApplicationSystemBuilder().setId("1")
                .setForm(new Form("", ElementUtil.createI18NAsIs("")))
                .setName(ElementUtil.createI18NAsIs(""))
                .setApplicationPeriods(applicationPeriods)
                .setState("JULKAISTU")
                .setApplicationSystemType(OppijaConstants.HAKUTYYPPI_VARSINAINEN_HAKU)
                .setName(ElementUtil.createI18NAsIs("")).get();
        assertTrue(applicationSystem.isActive());
    }
}
