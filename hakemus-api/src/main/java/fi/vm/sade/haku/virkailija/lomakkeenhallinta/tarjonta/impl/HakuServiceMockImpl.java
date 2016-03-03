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
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
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
                .setId("haku1")
                .setName(ElementUtil.createI18NAsIs("testi haku 1 " + HAKUKAUSI_SYKSY))
                .setApplicationPeriods(Lists.newArrayList(new ApplicationPeriod(new Date(), getDate(100))))
                .setHakukausiUri(HAKUKAUSI_SYKSY)
                .setHakukausiVuosi(2014)
                .setApplicationSystemType(HAKUTYYPPI_VARSINAINEN_HAKU)
                .setHakutapa(HAKUTAPA_YHTEISHAKU)
                .setMaxApplicationOptions(5)
                .setKohdejoukkoUri(KOHDEJOUKKO_AMMATILLINEN_JA_LUKIO)
                .setState("JULKAISTU")
                .get());
        asList.add(new ApplicationSystemBuilder()
                .setId("1.2.246.562.5.50476818906")
                .setName(ElementUtil.createI18NAsIs("testi haku 2 " + OppijaConstants.HAKUKAUSI_KEVAT))
                .setApplicationPeriods(Lists.newArrayList(new ApplicationPeriod(new Date(), getDate(100))))
                .setHakukausiUri(OppijaConstants.HAKUKAUSI_KEVAT)
                .setApplicationSystemType(HAKUTYYPPI_VARSINAINEN_HAKU)
                .setHakutapa(HAKUTAPA_YHTEISHAKU)
                .setMaxApplicationOptions(5)
                .setHakukausiVuosi(2014)
                .setUsePriorities(true)
                .setKohdejoukkoUri(KOHDEJOUKKO_AMMATILLINEN_JA_LUKIO)
                .setState("JULKAISTU")
                .get());
        asList.add(new ApplicationSystemBuilder()
                .setId("haku3")
                .setName(ElementUtil.createI18NAsIs("testi haku 3" + HAKUTYYPPI_LISAHAKU))
                .setApplicationPeriods(Lists.newArrayList(new ApplicationPeriod(new Date(), getDate(100))))
                .setHakukausiUri(HAKUKAUSI_SYKSY)
                .setHakukausiVuosi(2014)
                .setMaxApplicationOptions(5)
                .setApplicationSystemType(HAKUTYYPPI_LISAHAKU)
                .setHakutapa(HAKUTAPA_YHTEISHAKU)
                .setKohdejoukkoUri(KOHDEJOUKKO_AMMATILLINEN_JA_LUKIO)
                .setState("JULKAISTU")
                .get());
        asList.add(new ApplicationSystemBuilder()
                .setId("haku4")
                .setName(ElementUtil.createI18NAsIs("testi haku 4 " + HAKUTYYPPI_LISAHAKU))
                .setApplicationPeriods(Lists.newArrayList(new ApplicationPeriod(getDate(-100), getDate(1000))))
                .setHakukausiUri(HAKUKAUSI_KEVAT)
                .setHakukausiVuosi(2014)
                .setMaxApplicationOptions(5)
                .setApplicationSystemType(HAKUTYYPPI_LISAHAKU)
                .setHakutapa(HAKUTAPA_YHTEISHAKU)
                .setKohdejoukkoUri(KOHDEJOUKKO_AMMATILLINEN_JA_LUKIO)
                .setState("JULKAISTU")
                .get());
        asList.add(new ApplicationSystemBuilder()
                .setId("haku5")
                .setName(ElementUtil.createI18NAsIs("Perusopetuksen jälkeisen valmistavan koulutuksen kesän 2014 haku"))
                .setApplicationPeriods(Lists.newArrayList(new ApplicationPeriod(new Date(), getDate(100))))
                .setHakukausiUri(HAKUKAUSI_KEVAT)
                .setHakukausiVuosi(2014)
                .setKohdejoukkoUri(OppijaConstants.KOHDEJOUKKO_PERUSOPETUKSEN_JALKEINEN_VALMENTAVA)
                .setApplicationSystemType(HAKUTYYPPI_VARSINAINEN_HAKU)
                .setHakutapa(HAKUTAPA_YHTEISHAKU)
                .setMaxApplicationOptions(3)
                .setState("JULKAISTU")
                .get());
        asList.add(new ApplicationSystemBuilder()
                .setId("haku6")
                .setName(ElementUtil.createI18NAsIs("Perusopetuksen jälkeisen valmistavan koulutuksen kuluvan vuoden haku"))
                .setApplicationPeriods(Lists.newArrayList(new ApplicationPeriod(new Date(), getDate(100))))
                .setHakukausiUri(HAKUKAUSI_KEVAT)
                .setHakukausiVuosi(Calendar.getInstance().get(Calendar.YEAR))
                .setKohdejoukkoUri(OppijaConstants.KOHDEJOUKKO_PERUSOPETUKSEN_JALKEINEN_VALMENTAVA)
                .setApplicationSystemType(HAKUTYYPPI_VARSINAINEN_HAKU)
                .setHakutapa(HAKUTAPA_YHTEISHAKU)
                .setMaxApplicationOptions(3)
                .setState("JULKAISTU")
                .get());
        asList.add(new ApplicationSystemBuilder()
                .setId("1.2.246.562.29.173465377510")
                .setName(ElementUtil.createI18NAsIs("Korkkari " + KOHDEJOUKKO_KORKEAKOULU))
                .setApplicationPeriods(Lists.newArrayList(new ApplicationPeriod(getDate(-100), getDate(1000))))
                .setHakukausiUri(HAKUKAUSI_SYKSY)
                .setHakukausiVuosi(2014)
                .setUsePriorities(true)
                .setApplicationSystemType(HAKUTYYPPI_VARSINAINEN_HAKU)
                .setHakutapa(HAKUTAPA_YHTEISHAKU)
                .setMaxApplicationOptions(5)
                .setKohdejoukkoUri(KOHDEJOUKKO_KORKEAKOULU)
                .setState("JULKAISTU")
                .get());
        asList.add(new ApplicationSystemBuilder()
                .setId("1.2.246.562.29.95390561488")
                .setName(ElementUtil.createI18NAsIs("Korkkaritesti 2015"))
                .setApplicationPeriods(Lists.newArrayList(new ApplicationPeriod(getDate(-100), getDate(1000))))
                .setHakukausiUri(HAKUKAUSI_KEVAT)
                .setHakukausiVuosi(2015)
                .setUsePriorities(true)
                .setApplicationSystemType(HAKUTYYPPI_VARSINAINEN_HAKU)
                .setHakutapa(HAKUTAPA_YHTEISHAKU)
                .setMaxApplicationOptions(5)
                .setKohdejoukkoUri(KOHDEJOUKKO_KORKEAKOULU)
                .setState("JULKAISTU")
                .get());
        asList.add(new ApplicationSystemBuilder()
                .setId("1.2.246.562.29.75203638285")
                .setName(ElementUtil.createI18NAsIs("Korkkaritesti 2016"))
                .setApplicationPeriods(Lists.newArrayList(new ApplicationPeriod(getDate(-100), getDate(1000))))
                .setHakukausiUri(HAKUKAUSI_KEVAT)
                .setHakukausiVuosi(2016)
                .setUsePriorities(true)
                .setApplicationSystemType(HAKUTYYPPI_VARSINAINEN_HAKU)
                .setHakutapa(HAKUTAPA_YHTEISHAKU)
                .setMaxApplicationOptions(5)
                .setKohdejoukkoUri(KOHDEJOUKKO_KORKEAKOULU)
                .setState("JULKAISTU")
                .setMaksumuuriKaytossa(true)
                .get());
        asList.add(new ApplicationSystemBuilder()
                .setId("1.2.246.562.20.807716131410")
                .setName(ElementUtil.createI18NAsIs("Erityisopetus " + OppijaConstants.HAKUKAUSI_KEVAT))
                .setApplicationPeriods(Lists.newArrayList(new ApplicationPeriod(new Date(), getDate(100))))
                .setHakukausiUri(OppijaConstants.HAKUKAUSI_KEVAT)
                .setApplicationSystemType(HAKUTYYPPI_VARSINAINEN_HAKU)
                .setHakutapa(HAKUTAPA_YHTEISHAKU)
                .setMaxApplicationOptions(5)
                .setHakukausiVuosi(Calendar.getInstance().get(Calendar.YEAR))
                .setUsePriorities(true)
                .setKohdejoukkoUri(KOHDEJOUKKO_ERITYISOPETUKSENA_JARJESTETTAVA_AMMATILLINEN)
                .setState("JULKAISTU")
                .get());
        asList.add(new ApplicationSystemBuilder()
                .setId("1.2.246.562.29.80306203979")
                .setName(ElementUtil.createI18NAsIs("Ammatillisen koulutuksen ja lukiokoulutuksen syksyn 2015 yhteishaku"))
                .setApplicationPeriods(Lists.newArrayList(new ApplicationPeriod(getDate(-100), getDate(1000))))
                .setHakukausiUri(HAKUKAUSI_SYKSY)
                .setHakukausiVuosi(2015)
                .setUsePriorities(true)
                .setApplicationSystemType(HAKUTYYPPI_VARSINAINEN_HAKU)
                .setHakutapa(HAKUTAPA_YHTEISHAKU)
                .setMaxApplicationOptions(5)
                .setKohdejoukkoUri(KOHDEJOUKKO_AMMATILLINEN_JA_LUKIO)
                .setState("JULKAISTU")
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
        ApplicationSystem defaultAs = new ApplicationSystemBuilder()
                .setId(oid)
                .setName(ElementUtil.createI18NAsIs("Default AS"))
                .setApplicationPeriods(Lists.newArrayList(new ApplicationPeriod(getDate(-100), getDate(1000))))
                .setHakukausiUri(HAKUKAUSI_SYKSY)
                .setHakukausiVuosi(2015)
                .setUsePriorities(true)
                .setApplicationSystemType(HAKUTYYPPI_VARSINAINEN_HAKU)
                .setHakutapa(HAKUTAPA_YHTEISHAKU)
                .setMaxApplicationOptions(5)
                .setKohdejoukkoUri(KOHDEJOUKKO_AMMATILLINEN_JA_LUKIO)
                .setState("JULKAISTU")
                .get();
        return defaultAs;
    }

    @Override
    public HakuV1RDTO getRawApplicationSystem(String oid) {
        return null;
    }

    // TODO: FIX
    public List<String> getRelatedApplicationOptionIds(String oid){
        return null;
    }

    @Override
    public boolean kayttaaJarjestelmanLomaketta(String oid) {
        for (ApplicationSystem as : asList) {
            if (as.getId().equals(oid)) {
                return true;
            }
        }
        return false;
    }

    public static final Date getDate(int years) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, years);
        return cal.getTime();
    }
}
