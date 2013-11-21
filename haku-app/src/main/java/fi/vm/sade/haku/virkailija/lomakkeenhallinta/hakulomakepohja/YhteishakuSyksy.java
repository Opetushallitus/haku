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
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.elements.*;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.Answer;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.DiscretionaryAttachments;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.Print;
import fi.vm.sade.haku.oppija.lomake.domain.rules.RelatedQuestionRule;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.hakutoiveet.HakutoiveetPhaseYhteishakuSyksy;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.henkilotiedot.HenkilotiedotPhaseYhteishakuSyksy;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.koulutustausta.KoulutustaustaPhaseYhteishakuSyksy;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.lisatiedot.LisatiedotPhaseYhteishakuSyksy;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.osaaminen.OsaaminenPhaseYhteishakuSyksy;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;

import java.util.Date;
import java.util.List;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.createI18NText;

public class YhteishakuSyksy {

    private static final String FORM_MESSAGES = "form_messages_yhteishaku_syksy";
    private static final String FORM_ERRORS = "form_errors_yhteishaku_syksy";
    private static final String FORM_VERBOSE_HELP = "form_verboseHelp_yhteishaku_syksy";
    private static final String REGEX_NON_EMPTY = ".*\\S.*";

    public static Form generateForm(final ApplicationSystem as, final KoodistoService koodistoService) {
        try {
            Form form = new Form(as.getId(), as.getName());
            form.addChild(HenkilotiedotPhaseYhteishakuSyksy.create(koodistoService));
            form.addChild(KoulutustaustaPhaseYhteishakuSyksy.create(koodistoService));
            form.addChild(HakutoiveetPhaseYhteishakuSyksy.create());
            form.addChild(OsaaminenPhaseYhteishakuSyksy.create(koodistoService));
            Date start = as.getApplicationPeriods() != null && !as.getApplicationPeriods().isEmpty() ?
                    as.getApplicationPeriods().get(0).getStart() : new Date();
            form.addChild(LisatiedotPhaseYhteishakuSyksy.create(start));
            return form;
        } catch (Exception e) {
            throw new RuntimeException(YhteishakuSyksy.class.getCanonicalName() + " init failed", e);
        }
    }

    public static List<Element> generateApplicationCompleteElements() {
        List<Element> elements = Lists.newArrayList();

        RelatedQuestionRule emailRule = new RelatedQuestionRule("emailRule", "Sähköposti", REGEX_NON_EMPTY, false);
        Text emailP1 = new Text("emailP1", createI18NText("form.valmis.sinulleonlahetettyvahvistussahkopostiisi",
                FORM_MESSAGES));
        emailP1.addChild(new Answer("Sähköposti"));
        emailRule.addChild(emailP1);

        elements.add(emailRule);

        elements.add(new Text("valmisP1", createI18NText("form.lomake.valmis.p1", FORM_MESSAGES)));
        elements.add(new Text("valmisP2", createI18NText("form.lomake.valmis.p2", FORM_MESSAGES)));
        elements.add(new Text("valmisP3", createI18NText("form.lomake.valmis.p3", FORM_MESSAGES)));

        elements.add(new Print("printLink", createI18NText("form.valmis.button.tulosta", FORM_MESSAGES)));

        elements.add(new DiscretionaryAttachments("discretionaryAttachments"));

        RelatedQuestionRule athleteRule = new RelatedQuestionRule("athleteRule", Lists.newArrayList("preference1_urheilijan_ammatillisen_koulutuksen_lisakysymys",
                "preference2_urheilijan_ammatillisen_koulutuksen_lisakysymys", "preference3_urheilijan_ammatillisen_koulutuksen_lisakysymys",
                "preference4_urheilijan_ammatillisen_koulutuksen_lisakysymys", "preference5_urheilijan_ammatillisen_koulutuksen_lisakysymys"),
                "^true", false);
        TitledGroup athleteGroup = new TitledGroup("atheleteGroup", createI18NText("form.valmis.haeturheilijana.header", FORM_MESSAGES));

        athleteGroup.addChild(new Text("athleteP1", createI18NText("form.valmis.haeturheilijana", FORM_MESSAGES)));
        athleteGroup.addChild(new Link("athleteLink", "http://www.noc.fi/huippu-urheilu/opinto-ja_uraohjaus/urheilijoiden_opiskelumahdollisu/",
                createI18NText("form.valmis.haeturheilijana.linkki", FORM_MESSAGES)));
        athleteRule.addChild(athleteGroup);

        elements.add(athleteRule);

        TitledGroup muutoksenTekeminen = new TitledGroup("muutoksenTekeminen", createI18NText("form.valmis.muutoksentekeminen",
                FORM_MESSAGES));

        muutoksenTekeminen.addChild(new Text("muutoksenTekeminenP1", createI18NText("form.valmis.muutoksentekeminen.p1",
                FORM_MESSAGES)));
        muutoksenTekeminen.addChild(new Text("muutoksenTekeminenP2", createI18NText("form.valmis.muutoksentekeminen.p2",
                FORM_MESSAGES)));
        muutoksenTekeminen.addChild(new Text("muutoksenTekeminenP3", createI18NText("form.valmis.muutoksentekeminen.p3",
                FORM_MESSAGES)));

        elements.add(muutoksenTekeminen);

        elements.add(new Link("backLink", "https://opintopolku.fi", createI18NText("form.valmis.takaisin.opintopolkuun.linkki",
                FORM_MESSAGES)));

        return elements;
    }
}
