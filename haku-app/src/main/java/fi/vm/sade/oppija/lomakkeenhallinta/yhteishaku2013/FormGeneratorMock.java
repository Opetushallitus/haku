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

package fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013;

import com.google.common.collect.Lists;
import fi.vm.sade.oppija.common.koodisto.KoodistoService;
import fi.vm.sade.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.oppija.lomake.domain.I18nText;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomakkeenhallinta.FormGenerator;
import fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Mikko Majapuro
 */
public class FormGeneratorMock implements FormGenerator {

    private final KoodistoService koodistoService;
    private final String asId;

    public FormGeneratorMock(final KoodistoService koodistoService, final String asId) {
        this.koodistoService = koodistoService;
        this.asId = asId;
    }

    @Override
    public List<ApplicationSystem> generate() {
        List<ApplicationSystem> asList = Lists.newArrayList();
        final Calendar instance = Calendar.getInstance();
        instance.roll(Calendar.YEAR, -1);
        Date start = new Date(instance.getTimeInMillis());
        instance.roll(Calendar.YEAR, 2);
        Date end = new Date(instance.getTimeInMillis());
        List<ApplicationPeriod> applicationPeriods = Lists.newArrayList(new ApplicationPeriod(start, end));
        I18nText name = ElementUtil.createI18NAsIs(asId);
        Form form = Yhteishaku2013.generateForm(new ApplicationSystem(asId, null, name, applicationPeriods), koodistoService);
        asList.add(new ApplicationSystem(asId, form, name, applicationPeriods));
        return asList;
    }

    public ApplicationSystem createApplicationSystem() {
        final Calendar instance = Calendar.getInstance();
        instance.roll(Calendar.YEAR, -1);
        Date start = new Date(instance.getTimeInMillis());
        instance.roll(Calendar.YEAR, 2);
        Date end = new Date(instance.getTimeInMillis());
        List<ApplicationPeriod> applicationPeriods = Lists.newArrayList(new ApplicationPeriod(start, end));
        I18nText name = ElementUtil.createI18NAsIs(asId);
        Form form = Yhteishaku2013.generateForm(new ApplicationSystem(asId, null, name, applicationPeriods), koodistoService);
        return new ApplicationSystem(asId, form, name, applicationPeriods);
    }
}
