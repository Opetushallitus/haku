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

package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.lisatiedot;

import com.google.common.collect.ImmutableList;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Theme;
import fi.vm.sade.haku.oppija.lomake.domain.elements.TitledGroup;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.CheckBox;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Radio;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.TextArea;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.TextQuestion;
import fi.vm.sade.haku.oppija.lomake.domain.rules.RelatedQuestionRule;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.lisatiedot.LisatiedotPhaseYhteishakuSyksy.createTyokokemus;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.*;

public final class LisatiedotPhaseYhteishakuKevat {

    private static final String FORM_MESSAGES = "form_messages_yhteishaku_kevat";
    private static final String FORM_ERRORS = "form_errors_yhteishaku_kevat";
    private static final String FORM_VERBOSE_HELP = "form_verboseHelp_yhteishaku_kevat";

    private LisatiedotPhaseYhteishakuKevat() {
    }

    public static Phase create() {
        Phase lisatiedot = new Phase("lisatiedot", createI18NText("form.lisatiedot.otsikko", FORM_MESSAGES), false);
        lisatiedot.addChild(createTyokokemus());
        lisatiedot.addChild(createLupatiedot());
        lisatiedot.addChild(createUrheilijanLisakysymykset());
        return lisatiedot;
    }

    private static Theme createLupatiedot() {
        Theme lupatiedotTheme = new Theme("lupatiedotGrp", createI18NText("form.lisatiedot.lupatiedot", FORM_MESSAGES), true);
        CheckBox lupaMarkkinointi = new CheckBox(
                "lupaMarkkinointi",
                createI18NText("form.lupatiedot.saaMarkkinoida", FORM_MESSAGES));
        CheckBox lupaJulkaisu = new CheckBox("lupaJulkaisu",
                createI18NText("form.lupatiedot.saaJulkaista", FORM_MESSAGES));
        CheckBox lupaSahkoisesti = new CheckBox("lupaSahkoisesti",
                createI18NText("form.lupatiedot.saaLahettaaSahkoisesti", FORM_MESSAGES));
        CheckBox lupaSms = new CheckBox(
                "lupaSms",
                createI18NText("form.lupatiedot.saaLahettaaTekstiviesteja", FORM_MESSAGES));

        TitledGroup lupaGroup = new TitledGroup("permissionCheckboxes", createI18NText("form.lupatiedot.otsikko",
                FORM_MESSAGES));

        lupaGroup.addChild(lupaMarkkinointi);
        lupaGroup.addChild(lupaJulkaisu);
        lupaGroup.addChild(lupaSahkoisesti);
        lupaGroup.addChild(lupaSms);
        lupatiedotTheme.addChild(lupaGroup);
        setVerboseHelp(lupatiedotTheme, "form.lisatiedot.lupatiedot.verboseHelp", FORM_VERBOSE_HELP);

        Radio asiointikieli = new Radio(OppijaConstants.ELEMENT_ID_CONTACT_LANGUAGE,
                createI18NText("form.asiointikieli.otsikko", FORM_MESSAGES));
        asiointikieli.setHelp(createI18NText("form.asiointikieli.help", FORM_MESSAGES));
        asiointikieli.addOption(createI18NText("form.asiointikieli.suomi", FORM_MESSAGES), "suomi");
        asiointikieli.addOption(createI18NText("form.asiointikieli.ruotsi", FORM_MESSAGES), "ruotsi");
        addRequiredValidator(asiointikieli, FORM_ERRORS);
        setVerboseHelp(asiointikieli, "form.asiointikieli.otsikko.verboseHelp", FORM_VERBOSE_HELP);
        lupatiedotTheme.addChild(asiointikieli);
        return lupatiedotTheme;
    }

    private static Element createUrheilijanLisakysymykset() {
        Theme urheilijanLisakysymyksetTeema = new Theme(ElementUtil.randomId(),
                createI18NText("form.lisatiedot.urheilija", FORM_MESSAGES), true);
        ElementUtil.setVerboseHelp(urheilijanLisakysymyksetTeema, "form.lisatiedot.urheilija.verboseHelp", FORM_VERBOSE_HELP);

        ImmutableList<String> ids = ImmutableList.of(
                "preference1_urheilijan_ammatillisen_koulutuksen_lisakysymys",
                "preference1_urheilijalinjan_lisakysymys",
                "preference2_urheilijan_ammatillisen_koulutuksen_lisakysymys",
                "preference2_urheilijalinjan_lisakysymys",
                "preference3_urheilijan_ammatillisen_koulutuksen_lisakysymys",
                "preference3_urheilijalinjan_lisakysymys",
                "preference4_urheilijan_ammatillisen_koulutuksen_lisakysymys",
                "preference4_urheilijalinjan_lisakysymys",
                "preference5_urheilijan_ammatillisen_koulutuksen_lisakysymys",
                "preference5_urheilijalinjan_lisakysymys"
        );
        RelatedQuestionRule urheilijanLisakysymyksetSaanto = new RelatedQuestionRule(ElementUtil.randomId(), ids,
                ElementUtil.KYLLA, false);
        urheilijanLisakysymyksetSaanto.addChild(urheilijanLisakysymyksetTeema);

        TitledGroup opinnotGroup = new TitledGroup(ElementUtil.randomId(),
                createI18NText("form.lisatiedot.urheilija.opinnot", FORM_MESSAGES));
        TextQuestion liikunnanopettaja = new TextQuestion("liikunnanopettajan-nimi",
                createI18NText("form.lisatiedot.urheilija.opinnot.liikunnanopettajan.nimi", FORM_MESSAGES));
        liikunnanopettaja.setInline(true);
        liikunnanopettaja.addAttribute("size", "30");

        TextQuestion lukuaineidenKeskiarvo = new TextQuestion("lukuaineiden-keskiarvo",
                createI18NText("form.lisatiedot.urheilija.opinnot.lukuaineiden.keskiarvo", FORM_MESSAGES));
        lukuaineidenKeskiarvo.setInline(true);
        lukuaineidenKeskiarvo.addAttribute("size", "30");

        TextQuestion pakollinenLiikunnanNumero = createTextQuestion("pakollinen-liikunnan-numero",
                "form.lisatiedot.urheilija.opinnot.pakollisen.liikunnan.numero");

        opinnotGroup.addChild(liikunnanopettaja, lukuaineidenKeskiarvo, pakollinenLiikunnanNumero);

        TitledGroup urheilulajitGroup = createUrheilulajitRyhma();

        TitledGroup saavutuksetGroup = new TitledGroup(ElementUtil.randomId(),
                createI18NText("form.lisatiedot.urheilija.saavutukset", FORM_MESSAGES));
        TextArea saavutukset = new TextArea("saavutukset",
                createI18NText("form.lisatiedot.urheilija.saavutukset.saavutukset", FORM_MESSAGES));
        saavutukset.addAttribute("cols", "50");
        saavutukset.addAttribute("rows", "4");
        saavutukset.addAttribute("maxlength", "200");
        saavutukset.setInline(true);
        saavutukset.setHelp(createI18NText("form.lisatiedot.urheilija.saavutukset.saavutukset.help", FORM_MESSAGES));
        saavutuksetGroup.addChild(saavutukset);

        TitledGroup valmentajaGroup = new TitledGroup(ElementUtil.randomId(),
                createI18NText("form.lisatiedot.urheilija.valmentajan.yhteystiedot", FORM_MESSAGES));
        TextQuestion valmentajanNimi = new TextQuestion("valmentajan-nimi",
                createI18NText("form.lisatiedot.urheilija.valmentajan.yhteystiedot.nimi", FORM_MESSAGES));
        valmentajanNimi.setInline(true);
        valmentajanNimi.addAttribute("size", "30");
        valmentajaGroup.addChild(valmentajanNimi);
        TextQuestion valmentajanPuhelinnumero = new TextQuestion("valmentajan-puhelinnumero",
                createI18NText("form.lisatiedot.urheilija.valmentajan.yhteystiedot.puhelinnumero", FORM_MESSAGES));
        valmentajanPuhelinnumero.setInline(true);
        valmentajanPuhelinnumero.addAttribute("size", "30");
        valmentajaGroup.addChild(valmentajanPuhelinnumero);
        TextQuestion email = new TextQuestion("valmentajan-sähköpostiosoite",
                createI18NText("form.lisatiedot.urheilija.valmentajan.yhteystiedot.sahkopostiosoite", FORM_MESSAGES));
        email.addAttribute("size", "50");
        email.setValidator(createRegexValidator(email.getId(), EMAIL_REGEX, FORM_ERRORS));
        email.setInline(true);
        valmentajaGroup.addChild(email);


        TitledGroup valmennusryhmaGroup = new TitledGroup(ElementUtil.randomId(),
                createI18NText("form.lisatiedot.urheilija.valmennusryhma", FORM_MESSAGES));
        TextQuestion lajiliitto = new TextQuestion("lajiliitto-maajoukkue",
                createI18NText("form.lisatiedot.urheilija.valmennusryhma.lajiliitto", FORM_MESSAGES));
        lajiliitto.setInline(true);
        lajiliitto.addAttribute("size", "30");
        valmennusryhmaGroup.addChild(lajiliitto);
        TextQuestion piiri = new TextQuestion("alue-piiri",
                createI18NText("form.lisatiedot.urheilija.valmennusryhma.alue", FORM_MESSAGES));
        piiri.setInline(true);
        piiri.addAttribute("size", "30");
        valmennusryhmaGroup.addChild(piiri);
        TextQuestion seura = new TextQuestion("seura",
                createI18NText("form.lisatiedot.urheilija.valmennusryhma.seura", FORM_MESSAGES));
        seura.addAttribute("size", "30");
        seura.setInline(true);
        valmennusryhmaGroup.addChild(seura);
        urheilijanLisakysymyksetTeema.addChild(opinnotGroup, urheilulajitGroup,
                saavutuksetGroup, valmentajaGroup, valmennusryhmaGroup);
        return urheilijanLisakysymyksetSaanto;
    }

    private static TitledGroup createUrheilulajitRyhma() {
        TitledGroup urheilulajit = new TitledGroup(ElementUtil.randomId(),
                createI18NText("form.lisatiedot.urheilija.urheilu", FORM_MESSAGES));

        TextQuestion urheilulaji1 = createTextQuestion("Urheilulaji1", "form.lisatiedot.urheilija.urheilu.urheilulaji");
        TextQuestion lajiliitto1 = createTextQuestion("lajiliitto1", "form.lisatiedot.urheilija.urheilu.lajiliitto");

        TextQuestion urheilulaji2 = createTextQuestion("Urheilulaji2",
                "form.lisatiedot.urheilija.urheilu.urheilulaji2");
        urheilulaji2.setHelp(createI18NText("form.lisatiedot.urheilija.urheilu.urheilulaji2.help", FORM_MESSAGES));
        TextQuestion lajiliitto2 = createTextQuestion("lajiliitto2", "form.lisatiedot.urheilija.urheilu.lajiliitto2");

        urheilulajit.addChild(urheilulaji1, lajiliitto1, urheilulaji2, lajiliitto2);
        return urheilulajit;
    }

    private static TextQuestion createTextQuestion(final String id, final String messageKey) {
        TextQuestion textQuestion = new TextQuestion(id, createI18NText(messageKey, FORM_MESSAGES));
        textQuestion.setInline(true);
        addSizeAttribute(textQuestion, 30);
        return textQuestion;
    }
}
