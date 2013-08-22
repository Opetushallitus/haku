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

package fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013;

import com.google.common.collect.Lists;
import fi.vm.sade.oppija.common.koodisto.KoodistoService;
import fi.vm.sade.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.oppija.lomake.domain.I18nText;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.esikatselu.EsikatseluPhase;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.hakutoiveet.HakutoiveetPhase;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.henkilotiedot.HenkilotiedotPhase;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.koulutustausta.KoulutustaustaPhase;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.lisatiedot.LisatiedotPhase;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.osaaminen.OsaaminenPhase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class Yhteishaku2013 {

    private final ApplicationSystem applicationSystem;

    @Autowired
    public Yhteishaku2013(
            final KoodistoService koodistoService,
            final @Value("${asid}") String asid,
            final @Value("${aoid}") String aoid) { // NOSONAR
        Date start = new Date();
        final Calendar instance = Calendar.getInstance();
        instance.roll(Calendar.YEAR, 1);
        Date end = new Date(instance.getTimeInMillis());
        List<ApplicationPeriod> applicationPeriods = Lists.newArrayList(new ApplicationPeriod(start, end));
        I18nText name = ElementUtil.createI18NAsIs(asid);
        Form form = createForm(asid, koodistoService, aoid, applicationPeriods.get(0).getStart(), name);
        this.applicationSystem = new ApplicationSystem(asid, form, name, applicationPeriods);
    }

    public Yhteishaku2013(
            final KoodistoService koodistoService,
            final String asid,
            final String aoid,
            final I18nText name,
            final List<ApplicationPeriod> applicationPeriods) { // NOSONAR

        Date start = applicationPeriods != null && !applicationPeriods.isEmpty() ? applicationPeriods.get(0).getStart() :
                new Date();
        Form form = createForm(asid, koodistoService, aoid, start, name);
        this.applicationSystem = new ApplicationSystem(asid, form, name, applicationPeriods);
    }


    private Form createForm(final String asid,
                            final KoodistoService koodistoService,
                            final String aoidAdditionalQuestion,
                            final Date start,
                            final I18nText name) {
        try {
            Form form = new Form(asid, name);
            form.addChild(HenkilotiedotPhase.create(koodistoService));
            form.addChild(KoulutustaustaPhase.create(koodistoService));
            form.addChild(HakutoiveetPhase.create(aoidAdditionalQuestion));
            form.addChild(OsaaminenPhase.create(koodistoService));
            form.addChild(LisatiedotPhase.create(start));
            form.addChild(EsikatseluPhase.create(form));
            return form;
        } catch (Exception e) {
            throw new RuntimeException(Yhteishaku2013.class.getCanonicalName() + " init failed", e);
        }
    }

    public ApplicationSystem getApplicationSystem() {
        return applicationSystem;
    }
}
