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

package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja;

import com.google.common.collect.Lists;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystemBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.FormGenerator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;

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
        asList.add(createApplicationSystem());
        return asList;
    }

    public ApplicationSystem createApplicationSystem() {
        Form form = YhteishakuSyksy.generateForm(generateInitialApplicationSystemBuilder()
                .get(), koodistoService);
        return generateInitialApplicationSystemBuilder().addForm(form).get();
    }

    private ApplicationSystemBuilder generateInitialApplicationSystemBuilder(){
        final Calendar instance = Calendar.getInstance();
        instance.roll(Calendar.YEAR, -1);
        Date start = new Date(instance.getTimeInMillis());
        instance.roll(Calendar.YEAR, 2);
        Date end = new Date(instance.getTimeInMillis());
        Integer hakuvuosi = instance.get(Calendar.YEAR);
        List<ApplicationPeriod> applicationPeriods = Lists.newArrayList(new ApplicationPeriod(start, end));
        I18nText name = ElementUtil.createI18NAsIs(asId);
        return new ApplicationSystemBuilder().addId(asId).addName(name).addApplicationPeriods(applicationPeriods)
                .addHakukausiVuosi(hakuvuosi).addApplicationSystemType(OppijaConstants.VARSINAINEN_HAKU)
                .addApplicationCompleteElements(YhteishakuSyksy.generateApplicationCompleteElements());
    }
}
