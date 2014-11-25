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

import com.google.common.collect.Lists;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystemBuilder;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakuService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.*;

@Service
@Profile(value = {"dev", "it"})
public class HakuServiceMockImpl implements HakuService {

    private static final List<ApplicationSystem> asList = new ArrayList<ApplicationSystem>(4);

    static {
        asList.add(new ApplicationSystemBuilder()
                .addId("haku1")
                .addName(ElementUtil.createI18NAsIs("testi haku 1 " + HAKUKAUSI_SYKSY))
                .addApplicationPeriods(Lists.newArrayList(new ApplicationPeriod(new Date(), getDate(100))))
                .addHakukausiUri(HAKUKAUSI_SYKSY)
                .addHakukausiVuosi(2014)
                .addApplicationSystemType(HAKUTYYPPI_VARSINAINEN_HAKU)
                .addHakutapa(HAKUTAPA_YHTEISHAKU)
                .addMaxApplicationOptions(5)
                .addKohdejoukkoUri("haunkohdejoukko_1")
                .get());
        asList.add(new ApplicationSystemBuilder()
                .addId("1.2.246.562.5.50476818906")
                .addName(ElementUtil.createI18NAsIs("testi haku 2 " + OppijaConstants.HAKUKAUSI_KEVAT))
                .addApplicationPeriods(Lists.newArrayList(new ApplicationPeriod(new Date(), getDate(100))))
                .addHakukausiUri(OppijaConstants.HAKUKAUSI_KEVAT)
                .addApplicationSystemType(HAKUTYYPPI_VARSINAINEN_HAKU)
                .addHakutapa(HAKUTAPA_YHTEISHAKU)
                .addMaxApplicationOptions(5)
                .addHakukausiVuosi(2014)
                .addKohdejoukkoUri("haunkohdejoukko_1")
                .get());
        asList.add(new ApplicationSystemBuilder()
                .addId("haku3")
                .addName(ElementUtil.createI18NAsIs("testi haku 3" + HAKUTYYPPI_LISAHAKU))
                .addApplicationPeriods(Lists.newArrayList(new ApplicationPeriod(new Date(), getDate(100))))
                .addHakukausiUri(HAKUKAUSI_SYKSY)
                .addHakukausiVuosi(2014)
                .addMaxApplicationOptions(5)
                .addApplicationSystemType(HAKUTYYPPI_LISAHAKU)
                .addHakutapa(HAKUTAPA_YHTEISHAKU)
                .addKohdejoukkoUri("haunkohdejoukko_1")
                .get());
        asList.add(new ApplicationSystemBuilder()
                .addId("haku4")
                .addName(ElementUtil.createI18NAsIs("testi haku 4 " + HAKUTYYPPI_LISAHAKU))
                .addApplicationPeriods(Lists.newArrayList(new ApplicationPeriod(getDate(-100), getDate(1000))))
                .addHakukausiUri(HAKUKAUSI_KEVAT)
                .addHakukausiVuosi(2014)
                .addMaxApplicationOptions(5)
                .addApplicationSystemType(HAKUTYYPPI_LISAHAKU)
                .addHakutapa(HAKUTAPA_YHTEISHAKU)
                .addKohdejoukkoUri("haunkohdejoukko_1")
                .get());

        asList.add(new ApplicationSystemBuilder()
                .addId("haku5")
                .addName(ElementUtil.createI18NAsIs("Perusopetuksen jälkeisen valmistavan koulutuksen kesän 2014 haku"))
                .addApplicationPeriods(Lists.newArrayList(new ApplicationPeriod(new Date(), getDate(100))))
                .addHakukausiUri(HAKUKAUSI_KEVAT)
                .addHakukausiVuosi(2014)
                .addKohdejoukkoUri(OppijaConstants.KOHDEJOUKKO_PERVAKO)
                .addApplicationSystemType(HAKUTYYPPI_VARSINAINEN_HAKU)
                .addHakutapa(HAKUTAPA_YHTEISHAKU)
                .addMaxApplicationOptions(3)
                .get());
        asList.add(new ApplicationSystemBuilder()
                .addId("haku6")
                .addName(ElementUtil.createI18NAsIs("Korkkari " + KOHDEJOUKKO_KORKEAKOULU))
                .addApplicationPeriods(Lists.newArrayList(new ApplicationPeriod(getDate(-100), getDate(1000))))
                .addHakukausiUri(HAKUKAUSI_SYKSY)
                .addHakukausiVuosi(2014)
                .addApplicationSystemType(HAKUTYYPPI_VARSINAINEN_HAKU)
                .addHakutapa(HAKUTAPA_YHTEISHAKU)
                .addMaxApplicationOptions(5)
                .addKohdejoukkoUri(KOHDEJOUKKO_KORKEAKOULU)
                .get());
    }

    @Override
    public List<ApplicationSystem> getApplicationSystems() {
        return asList;
    }

    @Override
    public ApplicationSystem getApplicationSystem(String oid) {
        for (ApplicationSystem as : asList) {
            if (as.getId().equals(oid)) {
                return as;
            }
        }
        return null;
    }

    // TODO: FIX
    public List<String> getRelatedApplicationOptionIds(String oid){
        return null;
    }

    public static final Date getDate(int years) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, years);
        return cal.getTime();
    }
}
