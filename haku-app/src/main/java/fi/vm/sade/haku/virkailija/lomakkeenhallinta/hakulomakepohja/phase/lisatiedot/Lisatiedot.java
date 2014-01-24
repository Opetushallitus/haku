package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.lisatiedot;

import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Theme;
import fi.vm.sade.haku.oppija.lomake.domain.elements.TitledGroup;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.CheckBox;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Radio;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.TextArea;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.TextQuestion;
import fi.vm.sade.haku.oppija.lomake.domain.rules.RelatedQuestionComplexRule;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.And;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.Expr;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.OlderThan;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.Value;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.MessageBundleNames;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.*;

public class Lisatiedot {

    public static final String REQUIRED_EDUCATION_DEGREE = "32";
    public static final String AGE_WORK_EXPERIENCE = "16";
    public static final String TYOKOKEMUS_PATTERN = "^$|^([0-9]|[1-9][0-9]|[1-9][0-9][0-9]|1000)$";

    static RelatedQuestionComplexRule createTyokokemus(final MessageBundleNames mbn) {
        Expr isEducation32 = atLeastOneVariableEqualsToValue(REQUIRED_EDUCATION_DEGREE, OppijaConstants.AO_EDUCATION_DEGREE_KEYS);
        Expr olderThan16 = new OlderThan(new Value(AGE_WORK_EXPERIENCE));
        Expr rules = new And(isEducation32, olderThan16);

        Theme workExperienceTheme = new Theme("WorkExperienceTheme", createI18NText("form.lisatiedot.tyokokemus", mbn.getFormMessages()), true);
        workExperienceTheme.setHelp(createI18NText("form.tyokokemus.help", mbn.getFormMessages()));

        TextQuestion tyokokemuskuukaudet = new TextQuestion("TYOKOKEMUSKUUKAUDET",
                createI18NText("form.tyokokemus.kuukausina", mbn.getFormMessages()));
        tyokokemuskuukaudet
                .setHelp(createI18NText("form.tyokokemus.kuukausina.help", mbn.getFormMessages()));
        tyokokemuskuukaudet.setValidator(createRegexValidator(tyokokemuskuukaudet.getId(), TYOKOKEMUS_PATTERN,
                mbn.getFormErrors()));
        addSizeAttribute(tyokokemuskuukaudet, 8);
        tyokokemuskuukaudet.addAttribute("maxlength", "4");
        setVerboseHelp(tyokokemuskuukaudet, "form.tyokokemus.kuukausina.verboseHelp", mbn.getFormVerboseHelp());
        workExperienceTheme.addChild(tyokokemuskuukaudet);
        RelatedQuestionComplexRule naytetaankoTyokokemus = new RelatedQuestionComplexRule(ElementUtil.randomId(), rules);
        naytetaankoTyokokemus.addChild(workExperienceTheme);
        return naytetaankoTyokokemus;
    }

    static Theme createLupatiedot(final MessageBundleNames mbn) {
        Theme lupatiedotTheme = new Theme("lupatiedotGrp", createI18NText("form.lisatiedot.lupatiedot", mbn.getFormMessages()), true);
        CheckBox lupaMarkkinointi = new CheckBox(
                "lupaMarkkinointi",
                createI18NText("form.lupatiedot.saaMarkkinoida", mbn.getFormMessages()));
        CheckBox lupaJulkaisu = new CheckBox("lupaJulkaisu",
                createI18NText("form.lupatiedot.saaJulkaista", mbn.getFormMessages()));
        CheckBox lupaSahkoisesti = new CheckBox("lupaSahkoisesti",
                createI18NText("form.lupatiedot.saaLahettaaSahkoisesti", mbn.getFormMessages()));
        CheckBox lupaSms = new CheckBox(
                "lupaSms",
                createI18NText("form.lupatiedot.saaLahettaaTekstiviesteja", mbn.getFormMessages()));

        TitledGroup lupaGroup = new TitledGroup("permissionCheckboxes", createI18NText("form.lupatiedot.otsikko",
                mbn.getFormMessages()));

        lupaGroup.addChild(lupaMarkkinointi);
        lupaGroup.addChild(lupaJulkaisu);
        lupaGroup.addChild(lupaSahkoisesti);
        lupaGroup.addChild(lupaSms);
        lupatiedotTheme.addChild(lupaGroup);
        setVerboseHelp(lupatiedotTheme, "form.lisatiedot.lupatiedot.verboseHelp", mbn.getFormVerboseHelp());

        Radio asiointikieli = new Radio(OppijaConstants.ELEMENT_ID_CONTACT_LANGUAGE,
                createI18NText("form.asiointikieli.otsikko", mbn.getFormMessages()));
        asiointikieli.setHelp(createI18NText("form.asiointikieli.help", mbn.getFormMessages()));
        asiointikieli.addOption(createI18NText("form.asiointikieli.suomi", mbn.getFormMessages()), "suomi");
        asiointikieli.addOption(createI18NText("form.asiointikieli.ruotsi", mbn.getFormMessages()), "ruotsi");
        addRequiredValidator(asiointikieli, mbn.getFormErrors());
        setVerboseHelp(asiointikieli, "form.asiointikieli.otsikko.verboseHelp", mbn.getFormVerboseHelp());
        lupatiedotTheme.addChild(asiointikieli);
        return lupatiedotTheme;
    }

    static Element createUrheilijanLisakysymykset(final MessageBundleNames mbn) {
        Theme urheilijanLisakysymyksetTeema = new Theme(ElementUtil.randomId(),
                createI18NText("form.lisatiedot.urheilija", mbn.getFormMessages()), true);
        ElementUtil.setVerboseHelp(urheilijanLisakysymyksetTeema, "form.lisatiedot.urheilija.verboseHelp", mbn.getFormVerboseHelp());

        Expr onkoUrheilija = atLeastOneVariableEqualsToValue(ElementUtil.KYLLA,
                "preference1_urheilijan_ammatillisen_koulutuksen_lisakysymys",
                "preference1_urheilijalinjan_lisakysymys",
                "preference2_urheilijan_ammatillisen_koulutuksen_lisakysymys",
                "preference2_urheilijalinjan_lisakysymys",
                "preference3_urheilijan_ammatillisen_koulutuksen_lisakysymys",
                "preference3_urheilijalinjan_lisakysymys",
                "preference4_urheilijan_ammatillisen_koulutuksen_lisakysymys",
                "preference4_urheilijalinjan_lisakysymys",
                "preference5_urheilijan_ammatillisen_koulutuksen_lisakysymys",
                "preference5_urheilijalinjan_lisakysymys");

        RelatedQuestionComplexRule urheilijanLisakysymyksetSaanto = new RelatedQuestionComplexRule(ElementUtil.randomId(), onkoUrheilija);
        urheilijanLisakysymyksetSaanto.addChild(urheilijanLisakysymyksetTeema);

        TitledGroup opinnotGroup = createAiemmatOpinnotRyhma(mbn);
        TitledGroup urheilulajitGroup = createUrheilulajitRyhma(mbn);
        TitledGroup saavutuksetGroup = createSaavutuksetRyhma(mbn);

        TitledGroup valmentajaGroup = new TitledGroup(ElementUtil.randomId(),
                createI18NText("form.lisatiedot.urheilija.valmentajan.yhteystiedot", mbn.getFormMessages()));

        TextQuestion valmentajanNimi = createTextQuestion("valmentajan-nimi", "form.lisatiedot.urheilija.valmentajan.yhteystiedot.nimi", 50, mbn);
        valmentajaGroup.addChild(valmentajanNimi);

        TextQuestion valmentajanPuhelinnumero = createTextQuestion("valmentajan-puhelinnumero", "form.lisatiedot.urheilija.valmentajan.yhteystiedot.puhelinnumero", 50, mbn);
        valmentajaGroup.addChild(valmentajanPuhelinnumero);

        TextQuestion email = createTextQuestion("valmentajan-sähköpostiosoite", "form.lisatiedot.urheilija.valmentajan.yhteystiedot.sahkopostiosoite", 50, mbn);
        email.setValidator(createRegexValidator(email.getId(), EMAIL_REGEX, mbn.getFormErrors()));
        valmentajaGroup.addChild(email);

        TitledGroup valmennusryhmaGroup = new TitledGroup(ElementUtil.randomId(),
                createI18NText("form.lisatiedot.urheilija.valmennusryhma", mbn.getFormMessages()));

        TextQuestion lajiliitto = createTextQuestion("lajiliitto-maajoukkue", "form.lisatiedot.urheilija.valmennusryhma.lajiliitto", 100, mbn);
        valmennusryhmaGroup.addChild(lajiliitto);

        TextQuestion piiri = createTextQuestion("alue-piiri", "form.lisatiedot.urheilija.valmennusryhma.alue", 100, mbn);
        valmennusryhmaGroup.addChild(piiri);

        TextQuestion seura = createTextQuestion("seura", "form.lisatiedot.urheilija.valmennusryhma.seura", 100, mbn);
        valmennusryhmaGroup.addChild(seura);

        urheilijanLisakysymyksetTeema.addChild(opinnotGroup, urheilulajitGroup,
                saavutuksetGroup, valmentajaGroup, valmennusryhmaGroup);
        return urheilijanLisakysymyksetSaanto;
    }

    private static TitledGroup createUrheilulajitRyhma(final MessageBundleNames mbn) {
        TitledGroup urheilulajit = new TitledGroup(ElementUtil.randomId(),
                createI18NText("form.lisatiedot.urheilija.urheilu", mbn.getFormMessages()));

        TextQuestion urheilulaji1 = createTextQuestion("Urheilulaji1", "form.lisatiedot.urheilija.urheilu.urheilulaji", 50, mbn);
        TextQuestion lajiliitto1 = createTextQuestion("lajiliitto1", "form.lisatiedot.urheilija.urheilu.lajiliitto", 50, mbn);

        TextQuestion urheilulaji2 = createTextQuestion("Urheilulaji2", "form.lisatiedot.urheilija.urheilu.urheilulaji2", 50, mbn);
        urheilulaji2.setHelp(createI18NText("form.lisatiedot.urheilija.urheilu.urheilulaji2.help", mbn.getFormMessages()));
        TextQuestion lajiliitto2 = createTextQuestion("lajiliitto2", "form.lisatiedot.urheilija.urheilu.lajiliitto2", 50, mbn);

        urheilulajit.addChild(urheilulaji1, lajiliitto1, urheilulaji2, lajiliitto2);
        return urheilulajit;
    }

    private static TitledGroup createAiemmatOpinnotRyhma(final MessageBundleNames mbn) {
        TitledGroup opinnotGroup = new TitledGroup(ElementUtil.randomId(),
                createI18NText("form.lisatiedot.urheilija.opinnot", mbn.getFormMessages()));

        TextQuestion liikunnanopettaja = createTextQuestion("liikunnanopettajan-nimi",
                "form.lisatiedot.urheilija.opinnot.liikunnanopettajan.nimi", 50, mbn);

        TextQuestion lukuaineidenKeskiarvo = createTextQuestion("lukuaineiden-keskiarvo",
                "form.lisatiedot.urheilija.opinnot.lukuaineiden.keskiarvo", 4, mbn);

        TextQuestion pakollinenLiikunnanNumero = createTextQuestion("pakollinen-liikunnan-numero",
                "form.lisatiedot.urheilija.opinnot.pakollisen.liikunnan.numero", 2, mbn);

        opinnotGroup.addChild(liikunnanopettaja, lukuaineidenKeskiarvo, pakollinenLiikunnanNumero);
        return opinnotGroup;
    }

    private static TitledGroup createSaavutuksetRyhma(final MessageBundleNames mbn) {
        TitledGroup saavutuksetGroup = new TitledGroup(ElementUtil.randomId(),
                createI18NText("form.lisatiedot.urheilija.saavutukset", mbn.getFormMessages()));

        TextArea saavutukset = new TextArea("saavutukset",
                createI18NText("form.lisatiedot.urheilija.saavutukset.saavutukset", mbn.getFormMessages()));
        saavutukset.addAttribute("cols", "60");
        saavutukset.addAttribute("rows", "4");
        saavutukset.addAttribute("maxlength", "2000");
        saavutukset.setInline(true);
        saavutukset.setHelp(createI18NText("form.lisatiedot.urheilija.saavutukset.saavutukset.help", mbn.getFormMessages()));
        saavutuksetGroup.addChild(saavutukset);
        return saavutuksetGroup;
    }

    private static TextQuestion createTextQuestion(final String id, final String messageKey, int maxlength, final MessageBundleNames mbn) {
        TextQuestion textQuestion = new TextQuestion(id, createI18NText(messageKey, mbn.getFormMessages()));
        textQuestion.setInline(true);
        addSizeAttribute(textQuestion, 30);
        addMaxLengthAttribute(textQuestion, maxlength);
        return textQuestion;
    }
}
