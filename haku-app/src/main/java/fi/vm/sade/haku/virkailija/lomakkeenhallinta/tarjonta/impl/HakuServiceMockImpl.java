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

package fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.impl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystemBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakuService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.*;

@Service
@Profile(value = {"dev", "it"})
public class HakuServiceMockImpl implements HakuService {

    @Override
    public List<ApplicationSystem> getApplicationSystems() {

        return Lists.newArrayList(
                new ApplicationSystemBuilder()
                        .addId("haku1")
                        .addName(new I18nText(ImmutableMap.of("fi", "testi haku 1 " + HAKUKAUSI_SYKSY)))
                        .addApplicationPeriods(Lists.newArrayList(new ApplicationPeriod(new Date(), getDate(100))))
                        .addHakukausiUri(HAKUKAUSI_SYKSY)
                        .addHakukausiVuosi(2014)
                        .addApplicationSystemType(VARSINAINEN_HAKU)
                        .get(),
                new ApplicationSystemBuilder()
                        .addId("haku2")
                        .addName(new I18nText(ImmutableMap.of("fi", "testi haku 2 " + OppijaConstants.HAKUKAUSI_KEVAT)))
                        .addApplicationPeriods(Lists.newArrayList(new ApplicationPeriod(new Date(), getDate(100))))
                        .addHakukausiUri(OppijaConstants.HAKUKAUSI_KEVAT)
                        .addApplicationSystemType(VARSINAINEN_HAKU)
                        .addHakukausiVuosi(2014)
                        .get(),

                new ApplicationSystemBuilder()
                        .addId("haku3")
                        .addName(new I18nText(ImmutableMap.of("fi", "testi haku 3" + OppijaConstants.LISA_HAKU)))
                        .addApplicationPeriods(Lists.newArrayList(new ApplicationPeriod(new Date(), getDate(100))))
                        .addHakukausiUri(HAKUKAUSI_SYKSY)
                        .addHakukausiVuosi(2014)
                        .addApplicationSystemType(LISA_HAKU)
                        .get(),
                new ApplicationSystemBuilder()
                        .addId("haku4")
                        .addName(new I18nText(ImmutableMap.of("fi", "testi haku 4 " + OppijaConstants.LISA_HAKU)))
                        .addApplicationPeriods(Lists.newArrayList(new ApplicationPeriod(getDate(-100), getDate(-1))))
                        .addHakukausiUri(HAKUKAUSI_SYKSY)
                        .addHakukausiVuosi(2014)
                        .addApplicationSystemType(LISA_HAKU)
                        .get()

        );
    }

    public static final Date getDate(int years) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, years);
        return cal.getTime();
    }
}
