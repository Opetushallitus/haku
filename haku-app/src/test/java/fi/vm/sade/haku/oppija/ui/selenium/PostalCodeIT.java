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

package fi.vm.sade.haku.oppija.ui.selenium;

import com.google.common.collect.ImmutableList;
import fi.vm.sade.haku.oppija.common.selenium.DummyModelBaseItTest;
import fi.vm.sade.haku.oppija.lomake.ApplicationSystemHelper;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Theme;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.PostalCode;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.TextQuestion;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.createI18NAsIs;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.randomId;
import static org.junit.Assert.assertTrue;

public class PostalCodeIT extends DummyModelBaseItTest {

    public static final String POST_CODE = "70100";
    public static final String POST_CODE2 = "80100";
    public static final String POST_OFFICE = "Kuopio";
    public static final String POST_OFFICE2 = "Joensuu";
    private PostalCode postalCode;
    private TextQuestion textQuestion;
    private ApplicationSystem applicationSystem;

    @Before
    public void init() throws IOException {
        Form form = new Form(randomId(), createI18NAsIs(randomId()));
        applicationSystem = ElementUtil.createActiveApplicationSystem(randomId(), form);
        Phase phase = new Phase(randomId(), createI18NAsIs(randomId()), false);
        form.addChild(phase);

        Theme theme = new Theme(randomId(), createI18NAsIs(randomId()), true);
        phase.addChild(theme);
        ImmutableList<Option> options = ImmutableList.of(
                new Option(ElementUtil.createI18NAsIs(POST_OFFICE), POST_CODE),
                new Option(ElementUtil.createI18NAsIs(POST_OFFICE2), POST_CODE2));
        postalCode = new PostalCode(randomId(), createI18NAsIs(randomId()), options);
        theme.addChild(postalCode);
        textQuestion = new TextQuestion(randomId(), createI18NAsIs(randomId()));
        theme.addChild(textQuestion);
        updateApplicationSystem(applicationSystem);
    }

    @Test
    public void testPostalCode() throws InterruptedException, IOException {
        ApplicationSystemHelper applicationSystemHelper1 = new ApplicationSystemHelper(applicationSystem);
        System.out.println("--" + applicationSystemHelper1.getStartUrl());

        driver.get(getBaseUrl() + applicationSystemHelper1.getStartUrl());
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        setValue(postalCode.getId(), POST_CODE);
        setValue(textQuestion.getId(), randomId());
        assertTrue(findByXPath("//span[@class='post-office']").getText().trim().equals(POST_OFFICE));

        setValue(postalCode.getId(), StringUtils.repeat("\b", POST_CODE.length()) + POST_CODE2);
        setValue(textQuestion.getId(), randomId());
        assertTrue(findByXPath("//span[@class='post-office']").getText().trim().equals(POST_OFFICE2));
    }
}
