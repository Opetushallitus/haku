package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.lisatiedot;

import com.google.common.collect.Lists;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Theme;
import fi.vm.sade.haku.oppija.lomake.domain.elements.TitledGroup;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.CheckBox;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Radio;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.TextArea;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.TextQuestion;
import fi.vm.sade.haku.oppija.lomake.domain.rules.RelatedQuestionComplexRule;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.And;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.Not;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.Expr;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.OlderThan;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.Value;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ExprUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.osaaminen.OsaaminenPhaseYhteishakuKevat.createPohjakoilutusUlkomainenTaiKeskeyttanyt;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.*;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.createI18NText;

public class LisatiedotPhase {

    public static final String REQUIRED_EDUCATION_DEGREE = "32";
    public static final String MIN_AGE_REQUIRED_TO_WORK_EXPERIENCE_AGE = "16";
    public static final String TYOKOKEMUS_PATTERN = "^$|^([0-9]|[1-9][0-9]|[1-9][0-9][0-9]|1000)$";

    private LisatiedotPhase() {
    }

    public static Phase create(final String formMessagesBundle, final String formErrorsBundle, final String formVerboseHelpBundle) {
        Phase lisatiedot = new Phase("lisatiedot", createI18NText("form.lisatiedot.otsikko", formMessagesBundle), false,
                Lists.newArrayList("APP_HAKEMUS_READ_UPDATE", "APP_HAKEMUS_CRUD", "APP_HAKEMUS_OPO"));
        lisatiedot.addChild(createTyokokemus(formMessagesBundle,formErrorsBundle,formVerboseHelpBundle));
        lisatiedot.addChild(createLupatiedot(formMessagesBundle,formErrorsBundle,formVerboseHelpBundle));
        lisatiedot.addChild(createUrheilijanLisakysymykset(formMessagesBundle,formErrorsBundle,formVerboseHelpBundle));
        return lisatiedot;
    }

    static RelatedQuestionComplexRule createTyokokemus(final String formMessagesBundle, final String formErrorsBundle, final String formVerboseHelpBundle) {
        Expr isEducation32 = ExprUtil.atLeastOneVariableEqualsToValue(REQUIRED_EDUCATION_DEGREE, OppijaConstants.AO_EDUCATION_DEGREE_KEYS);
        Expr olderThan16 = new OlderThan(new Value(MIN_AGE_REQUIRED_TO_WORK_EXPERIENCE_AGE));
        Expr pohjakoulutusKeskeyttanytTaiUlkomaillasuoritettu = createPohjakoilutusUlkomainenTaiKeskeyttanyt();

        Expr rules = new And(new Not(pohjakoulutusKeskeyttanytTaiUlkomaillasuoritettu), new And(isEducation32, olderThan16));

        Theme workExperienceTheme = new Theme("WorkExperienceTheme", createI18NText("form.lisatiedot.tyokokemus", formMessagesBundle), true);
        workExperienceTheme.setHelp(createI18NText("form.tyokokemus.help", formMessagesBundle));

        TextQuestion tyokokemuskuukaudet = new TextQuestion("TYOKOKEMUSKUUKAUDET",
                createI18NText("form.tyokokemus.kuukausina", formMessagesBundle));
        tyokokemuskuukaudet
                .setHelp(createI18NText("form.tyokokemus.kuukausina.help", formMessagesBundle));
        tyokokemuskuukaudet.setValidator(createRegexValidator(tyokokemuskuukaudet.getId(), TYOKOKEMUS_PATTERN,
                formErrorsBundle, "lisatiedot.tyokokemus.virhe"));
        addSizeAttribute(tyokokemuskuukaudet, 8);
        tyokokemuskuukaudet.addAttribute("maxlength", "4");
        setVerboseHelp(tyokokemuskuukaudet, "form.tyokokemus.kuukausina.verboseHelp", formVerboseHelpBundle);
        workExperienceTheme.addChild(tyokokemuskuukaudet);
        RelatedQuestionComplexRule naytetaankoTyokokemus = new RelatedQuestionComplexRule(ElementUtil.randomId(), rules);
        naytetaankoTyokokemus.addChild(workExperienceTheme);
        return naytetaankoTyokokemus;
    }

    static Theme createLupatiedot(final String formMessagesBundle, final String formErrorsBundle, final String formVerboseHelpBundle) {
        Theme lupatiedotTheme = new Theme("lupatiedotGrp", createI18NText("form.lisatiedot.lupatiedot", formMessagesBundle), true);
        CheckBox lupaMarkkinointi = new CheckBox(
                "lupaMarkkinointi",
                createI18NText("form.lupatiedot.saaMarkkinoida", formMessagesBundle));
        CheckBox lupaJulkaisu = new CheckBox("lupaJulkaisu",
                createI18NText("form.lupatiedot.saaJulkaista", formMessagesBundle));
        CheckBox lupaSahkoisesti = new CheckBox("lupaSahkoisesti",
                createI18NText("form.lupatiedot.saaLahettaaSahkoisesti", formMessagesBundle));
        CheckBox lupaSms = new CheckBox(
                "lupaSms",
                createI18NText("form.lupatiedot.saaLahettaaTekstiviesteja", formMessagesBundle));

        TitledGroup lupaGroup = new TitledGroup("permissionCheckboxes", createI18NText("form.lupatiedot.otsikko",
                formMessagesBundle));

        lupaGroup.addChild(lupaMarkkinointi);
        lupaGroup.addChild(lupaJulkaisu);
        lupaGroup.addChild(lupaSahkoisesti);
        lupaGroup.addChild(lupaSms);
        lupatiedotTheme.addChild(lupaGroup);
        setVerboseHelp(lupatiedotTheme, "form.lisatiedot.lupatiedot.verboseHelp", formVerboseHelpBundle);

        Radio asiointikieli = new Radio(OppijaConstants.ELEMENT_ID_CONTACT_LANGUAGE,
                createI18NText("form.asiointikieli.otsikko", formMessagesBundle));
        asiointikieli.setHelp(createI18NText("form.asiointikieli.help", formMessagesBundle));
        asiointikieli.addOption(createI18NText("form.asiointikieli.suomi", formMessagesBundle), "suomi");
        asiointikieli.addOption(createI18NText("form.asiointikieli.ruotsi", formMessagesBundle), "ruotsi");
        addRequiredValidator(asiointikieli, formErrorsBundle);
        setVerboseHelp(asiointikieli, "form.asiointikieli.otsikko.verboseHelp", formVerboseHelpBundle);
        lupatiedotTheme.addChild(asiointikieli);
        return lupatiedotTheme;
    }

    static Element createUrheilijanLisakysymykset(final String formMessagesBundle, final String formErrorsBundle, final String formVerboseHelpBundle) {
        Theme urheilijanLisakysymyksetTeema = new Theme(ElementUtil.randomId(),
                createI18NText("form.lisatiedot.urheilija", formMessagesBundle), true);
        ElementUtil.setVerboseHelp(urheilijanLisakysymyksetTeema, "form.lisatiedot.urheilija.verboseHelp", formVerboseHelpBundle);

        Expr onkoUrheilija = ExprUtil.atLeastOneVariableEqualsToValue(ElementUtil.KYLLA,
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

        TitledGroup opinnotGroup = createAiemmatOpinnotRyhma(formMessagesBundle, formErrorsBundle);
        TitledGroup urheilulajitGroup = createUrheilulajitRyhma(formMessagesBundle);
        TitledGroup saavutuksetGroup = createSaavutuksetRyhma(formMessagesBundle, formErrorsBundle);

        TitledGroup valmentajaGroup = new TitledGroup(ElementUtil.randomId(),
                createI18NText("form.lisatiedot.urheilija.valmentajan.yhteystiedot", formMessagesBundle));

        TextQuestion valmentajanNimi = createTextQuestion("valmentajan-nimi", "form.lisatiedot.urheilija.valmentajan.yhteystiedot.nimi", 50, formMessagesBundle, formErrorsBundle);
        valmentajaGroup.addChild(valmentajanNimi);

        TextQuestion valmentajanPuhelinnumero = createTextQuestion("valmentajan-puhelinnumero", "form.lisatiedot.urheilija.valmentajan.yhteystiedot.puhelinnumero", 50, formMessagesBundle, formErrorsBundle);
        valmentajaGroup.addChild(valmentajanPuhelinnumero);

        TextQuestion email = createTextQuestion("valmentajan-sähköpostiosoite", "form.lisatiedot.urheilija.valmentajan.yhteystiedot.sahkopostiosoite", 50, formMessagesBundle, formErrorsBundle);
        email.setValidator(createRegexValidator(email.getId(), EMAIL_REGEX, formErrorsBundle));
        valmentajaGroup.addChild(email);

        TitledGroup valmennusryhmaGroup = new TitledGroup(ElementUtil.randomId(),
                createI18NText("form.lisatiedot.urheilija.valmennusryhma", formMessagesBundle));

        TextQuestion lajiliitto = createTextQuestion("lajiliitto-maajoukkue", "form.lisatiedot.urheilija.valmennusryhma.lajiliitto", 100, formMessagesBundle,formErrorsBundle);
        valmennusryhmaGroup.addChild(lajiliitto);

        TextQuestion piiri = createTextQuestion("alue-piiri", "form.lisatiedot.urheilija.valmennusryhma.alue", 100, formMessagesBundle,formErrorsBundle);
        valmennusryhmaGroup.addChild(piiri);

        TextQuestion seura = createTextQuestion("seura", "form.lisatiedot.urheilija.valmennusryhma.seura", 100, formMessagesBundle,formErrorsBundle);
        valmennusryhmaGroup.addChild(seura);

        urheilijanLisakysymyksetTeema.addChild(opinnotGroup, urheilulajitGroup,
                saavutuksetGroup, valmentajaGroup, valmennusryhmaGroup);
        return urheilijanLisakysymyksetSaanto;
    }

    private static TitledGroup createUrheilulajitRyhma(final String formMessagesBundle) {
        TitledGroup urheilulajit = new TitledGroup(ElementUtil.randomId(),
                createI18NText("form.lisatiedot.urheilija.urheilu", formMessagesBundle));

        TextQuestion urheilulaji1 = createTextQuestion("Urheilulaji1", "form.lisatiedot.urheilija.urheilu.urheilulaji", 50, formMessagesBundle, formMessagesBundle);
        TextQuestion lajiliitto1 = createTextQuestion("lajiliitto1", "form.lisatiedot.urheilija.urheilu.lajiliitto", 50, formMessagesBundle, formMessagesBundle);

        TextQuestion urheilulaji2 = createTextQuestion("Urheilulaji2", "form.lisatiedot.urheilija.urheilu.urheilulaji2", 50, formMessagesBundle, formMessagesBundle);
        urheilulaji2.setHelp(createI18NText("form.lisatiedot.urheilija.urheilu.urheilulaji2.help", formMessagesBundle));
        TextQuestion lajiliitto2 = createTextQuestion("lajiliitto2", "form.lisatiedot.urheilija.urheilu.lajiliitto2", 50, formMessagesBundle, formMessagesBundle);

        urheilulajit.addChild(urheilulaji1, lajiliitto1, urheilulaji2, lajiliitto2);
        return urheilulajit;
    }

    private static TitledGroup createAiemmatOpinnotRyhma(final String formMessagesBundle, final String formErrorsBundle) {
        TitledGroup opinnotGroup = new TitledGroup(ElementUtil.randomId(),
                createI18NText("form.lisatiedot.urheilija.opinnot", formMessagesBundle));

        TextQuestion liikunnanopettaja = createTextQuestion("liikunnanopettajan-nimi",
                "form.lisatiedot.urheilija.opinnot.liikunnanopettajan.nimi", 50, formMessagesBundle, formErrorsBundle);

        TextQuestion lukuaineidenKeskiarvo = createTextQuestion("lukuaineiden-keskiarvo",
                "form.lisatiedot.urheilija.opinnot.lukuaineiden.keskiarvo", 4, formMessagesBundle, formErrorsBundle);

        TextQuestion pakollinenLiikunnanNumero = createTextQuestion("pakollinen-liikunnan-numero",
                "form.lisatiedot.urheilija.opinnot.pakollisen.liikunnan.numero", 2, formMessagesBundle, formErrorsBundle);

        opinnotGroup.addChild(liikunnanopettaja, lukuaineidenKeskiarvo, pakollinenLiikunnanNumero);
        return opinnotGroup;
    }

    private static TitledGroup createSaavutuksetRyhma(final String formMessagesBundle, final String formErrorsBundle) {
        TitledGroup saavutuksetGroup = new TitledGroup(ElementUtil.randomId(),
                createI18NText("form.lisatiedot.urheilija.saavutukset", formMessagesBundle));

        TextArea saavutukset = new TextArea("saavutukset",
                createI18NText("form.lisatiedot.urheilija.saavutukset.saavutukset", formMessagesBundle));
        addMaxLengthAttributeAndLengthValidator(saavutukset, 2000, formErrorsBundle);
        saavutukset.setInline(true);
        saavutukset.setHelp(createI18NText("form.lisatiedot.urheilija.saavutukset.saavutukset.help", formMessagesBundle));
        saavutuksetGroup.addChild(saavutukset);
        return saavutuksetGroup;
    }

    private static TextQuestion createTextQuestion(final String id, final String messageKey, int maxlength, final String formMessagesBundle, final String formErrorsBundle) {
        TextQuestion textQuestion = new TextQuestion(id, createI18NText(messageKey, formMessagesBundle));
        textQuestion.setInline(true);
        addSizeAttribute(textQuestion, 30);
        addMaxLengthAttributeAndLengthValidator(textQuestion, maxlength, formErrorsBundle);
        return textQuestion;
    }
}
