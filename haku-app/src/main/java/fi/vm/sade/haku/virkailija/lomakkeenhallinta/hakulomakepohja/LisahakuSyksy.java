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
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.hakutoiveet.HakutoiveetPhaseLisahakuSyksy;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.henkilotiedot.HenkilotiedotPhaseLisahakuSyksy;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.koulutustausta.KoulutustaustaPhaseLisahakuSyksy;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.lisatiedot.LisatiedotPhaseLisahakuSyksy;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.osaaminen.OsaaminenPhaseLisahakuSyksy;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.*;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.EDUCATION_CODE_KEY;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.VALID_EDUCATION_CODES;

public class LisahakuSyksy {

    private static final String FORM_MESSAGES = "form_messages_lisahaku_syksy";
    private static final String REGEX_NON_EMPTY = ".*\\S.*";

    public static Form generateForm(final ApplicationSystem as, final KoodistoService koodistoService) {
        try {
            Form form = new Form(as.getId(), as.getName());
            form.addChild(HenkilotiedotPhaseLisahakuSyksy.create(koodistoService));
            form.addChild(KoulutustaustaPhaseLisahakuSyksy.create(koodistoService));
            form.addChild(HakutoiveetPhaseLisahakuSyksy.create());
            form.addChild(OsaaminenPhaseLisahakuSyksy.create(koodistoService));
            form.addChild(LisatiedotPhaseLisahakuSyksy.create());
            return form;
        } catch (Exception e) {
            throw new RuntimeException(LisahakuSyksy.class.getCanonicalName() + " init failed", e);
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
        Link athleteLink = new Link("athleteLink", createI18NText("form.valmis.haeturheilijana.linkki.url", FORM_MESSAGES),
                createI18NText("form.valmis.haeturheilijana.linkki.text", FORM_MESSAGES));
        athleteLink.addAttribute("target", "_blank");
        athleteGroup.addChild(athleteLink);
        athleteRule.addChild(athleteGroup);
        elements.add(athleteRule);

        //Hait musiikki-, tanssi- tai liikunta-alan koulutukseen.
        RelatedQuestionRule musiikkiTanssiLiikuntaRule = new RelatedQuestionRule("musiikkiTanssiLiikuntaRule",
                Lists.newArrayList(String.format(EDUCATION_CODE_KEY, 1),
                        String.format(EDUCATION_CODE_KEY, 2),
                        String.format(EDUCATION_CODE_KEY, 3),
                        String.format(EDUCATION_CODE_KEY, 4),
                        String.format(EDUCATION_CODE_KEY, 5)),
                StringUtils.join(VALID_EDUCATION_CODES, "|"), false);
        TitledGroup musiikkiTanssiLiikuntaGroup = new TitledGroup("mtlGroup", createI18NText("form.valmis.musiikkitanssiliikunta.header", FORM_MESSAGES));
        musiikkiTanssiLiikuntaGroup.addChild(new Text(randomId(), createI18NText("form.valmis.musiikkitanssiliikunta", FORM_MESSAGES)));
        musiikkiTanssiLiikuntaRule.addChild(musiikkiTanssiLiikuntaGroup);
        elements.add(musiikkiTanssiLiikuntaRule);

        TitledGroup muutoksenTekeminen = new TitledGroup("muutoksenTekeminen", createI18NText("form.valmis.muutoksentekeminen",
                FORM_MESSAGES));

        muutoksenTekeminen.addChild(new Text("muutoksenTekeminenP1", createI18NText("form.valmis.muutoksentekeminen.p1",
                FORM_MESSAGES)));

        elements.add(muutoksenTekeminen);

        elements.add(new Link("backLink", createI18NAsIs("https://opintopolku.fi"), createI18NText("form.valmis.takaisin.opintopolkuun.linkki",
                FORM_MESSAGES)));

        return elements;
    }
}
