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
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.*;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ExprUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.osaaminen.OsaaminenPhaseYhteishakuKevat.createPohjakoilutusUlkomainenTaiKeskeyttanyt;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.*;

public class LisatiedotPhase {

    public static final String REQUIRED_EDUCATION_DEGREE = "32";
    public static final String MIN_AGE_REQUIRED_TO_WORK_EXPERIENCE_AGE = "16";
    public static final String TYOKOKEMUS_PATTERN = "^$|^([0-9]|[1-9][0-9]|[1-9][0-9][0-9]|1000)$";

    private LisatiedotPhase() {
    }

    public static Phase create(final FormParameters formParameters) {
        Phase lisatiedot = new Phase("lisatiedot", createI18NText("form.lisatiedot.otsikko", formParameters.getFormMessagesBundle()), false,
                Lists.newArrayList("APP_HAKEMUS_READ_UPDATE", "APP_HAKEMUS_CRUD", "APP_HAKEMUS_OPO"));
        lisatiedot.addChild(createTyokokemus(formParameters));
        lisatiedot.addChild(createLupatiedot(formParameters));
        lisatiedot.addChild(createUrheilijanLisakysymykset(formParameters));
        return lisatiedot;
    }

    static RelatedQuestionComplexRule createTyokokemus(final FormParameters formParameters) {
        Expr isEducation32 = ExprUtil.atLeastOneVariableEqualsToValue(REQUIRED_EDUCATION_DEGREE, OppijaConstants.AO_EDUCATION_DEGREE_KEYS);
        Expr olderThan16 = new OlderThan(new Value(MIN_AGE_REQUIRED_TO_WORK_EXPERIENCE_AGE));
        Expr pohjakoulutusKeskeyttanytTaiUlkomaillasuoritettu = createPohjakoilutusUlkomainenTaiKeskeyttanyt();

        Expr rules = new And(new Not(pohjakoulutusKeskeyttanytTaiUlkomaillasuoritettu), new And(isEducation32, olderThan16));

        Theme workExperienceTheme = new Theme("WorkExperienceTheme", createI18NText("form.lisatiedot.tyokokemus", formParameters.getFormMessagesBundle()), true);
        workExperienceTheme.setHelp(createI18NText("form.tyokokemus.help", formParameters.getFormMessagesBundle()));

        TextQuestion tyokokemuskuukaudet = new TextQuestion("TYOKOKEMUSKUUKAUDET",
                createI18NText("form.tyokokemus.kuukausina", formParameters.getFormMessagesBundle()));
        tyokokemuskuukaudet
                .setHelp(createI18NText("form.tyokokemus.kuukausina.help", formParameters.getFormMessagesBundle()));
        tyokokemuskuukaudet.setValidator(createRegexValidator(tyokokemuskuukaudet.getId(), TYOKOKEMUS_PATTERN,
                formParameters, "lisatiedot.tyokokemus.virhe"));
        addSizeAttribute(tyokokemuskuukaudet, 8);
        tyokokemuskuukaudet.addAttribute("maxlength", "4");
        setVerboseHelp(tyokokemuskuukaudet, "form.tyokokemus.kuukausina.verboseHelp", formParameters);
        workExperienceTheme.addChild(tyokokemuskuukaudet);
        RelatedQuestionComplexRule naytetaankoTyokokemus = new RelatedQuestionComplexRule(ElementUtil.randomId(), rules);
        naytetaankoTyokokemus.addChild(workExperienceTheme);
        return naytetaankoTyokokemus;
    }

    static Theme createLupatiedot(final FormParameters formParameters) {
        Theme lupatiedotTheme = new Theme("lupatiedotGrp", createI18NText("form.lisatiedot.lupatiedot", formParameters.getFormMessagesBundle()), true);
        CheckBox lupaMarkkinointi = new CheckBox(
                "lupaMarkkinointi",
                createI18NText("form.lupatiedot.saaMarkkinoida", formParameters.getFormMessagesBundle()));
        CheckBox lupaJulkaisu = new CheckBox("lupaJulkaisu",
                createI18NText("form.lupatiedot.saaJulkaista", formParameters.getFormMessagesBundle()));
        CheckBox lupaSahkoisesti = new CheckBox("lupaSahkoisesti",
                createI18NText("form.lupatiedot.saaLahettaaSahkoisesti", formParameters.getFormMessagesBundle()));
        CheckBox lupaSms = new CheckBox(
                "lupaSms",
                createI18NText("form.lupatiedot.saaLahettaaTekstiviesteja", formParameters.getFormMessagesBundle()));

        TitledGroup lupaGroup = new TitledGroup("permissionCheckboxes", createI18NText("form.lupatiedot.otsikko",
                formParameters.getFormMessagesBundle()));

        lupaGroup.addChild(lupaMarkkinointi);
        lupaGroup.addChild(lupaJulkaisu);
        lupaGroup.addChild(lupaSahkoisesti);
        lupaGroup.addChild(lupaSms);
        lupatiedotTheme.addChild(lupaGroup);
        setVerboseHelp(lupatiedotTheme, "form.lisatiedot.lupatiedot.verboseHelp", formParameters);

        Radio asiointikieli = new Radio(OppijaConstants.ELEMENT_ID_CONTACT_LANGUAGE,
                createI18NText("form.asiointikieli.otsikko", formParameters.getFormMessagesBundle()));
        asiointikieli.setHelp(createI18NText("form.asiointikieli.help", formParameters.getFormMessagesBundle()));
        asiointikieli.addOption(createI18NText("form.asiointikieli.suomi", formParameters.getFormMessagesBundle()), "suomi");
        asiointikieli.addOption(createI18NText("form.asiointikieli.ruotsi", formParameters.getFormMessagesBundle()), "ruotsi");
        addRequiredValidator(asiointikieli, formParameters);
        setVerboseHelp(asiointikieli, "form.asiointikieli.otsikko.verboseHelp", formParameters);
        lupatiedotTheme.addChild(asiointikieli);
        return lupatiedotTheme;
    }

    static Element createUrheilijanLisakysymykset(final FormParameters formParameters) {
        Theme urheilijanLisakysymyksetTeema = new Theme(ElementUtil.randomId(),
                createI18NText("form.lisatiedot.urheilija", formParameters.getFormMessagesBundle()), true);
        ElementUtil.setVerboseHelp(urheilijanLisakysymyksetTeema, "form.lisatiedot.urheilija.verboseHelp", formParameters);

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

        TitledGroup opinnotGroup = createAiemmatOpinnotRyhma(formParameters);
        TitledGroup urheilulajitGroup = createUrheilulajitRyhma(formParameters);
        TitledGroup saavutuksetGroup = createSaavutuksetRyhma(formParameters);

        TitledGroup valmentajaGroup = new TitledGroup(ElementUtil.randomId(),
                createI18NText("form.lisatiedot.urheilija.valmentajan.yhteystiedot", formParameters.getFormMessagesBundle()));

        TextQuestion valmentajanNimi = createTextQuestion("valmentajan-nimi", "form.lisatiedot.urheilija.valmentajan.yhteystiedot.nimi", 50, formParameters);
        valmentajaGroup.addChild(valmentajanNimi);

        TextQuestion valmentajanPuhelinnumero = createTextQuestion("valmentajan-puhelinnumero", "form.lisatiedot.urheilija.valmentajan.yhteystiedot.puhelinnumero", 50, formParameters);
        valmentajaGroup.addChild(valmentajanPuhelinnumero);

        TextQuestion email = createTextQuestion("valmentajan-sähköpostiosoite", "form.lisatiedot.urheilija.valmentajan.yhteystiedot.sahkopostiosoite", 50, formParameters);
        email.setValidator(createRegexValidator(email.getId(), EMAIL_REGEX, formParameters));
        valmentajaGroup.addChild(email);

        TitledGroup valmennusryhmaGroup = new TitledGroup(ElementUtil.randomId(),
                createI18NText("form.lisatiedot.urheilija.valmennusryhma", formParameters.getFormMessagesBundle()));

        TextQuestion lajiliitto = createTextQuestion("lajiliitto-maajoukkue", "form.lisatiedot.urheilija.valmennusryhma.lajiliitto", 100, formParameters);
        valmennusryhmaGroup.addChild(lajiliitto);

        TextQuestion piiri = createTextQuestion("alue-piiri", "form.lisatiedot.urheilija.valmennusryhma.alue", 100, formParameters);
        valmennusryhmaGroup.addChild(piiri);

        TextQuestion seura = createTextQuestion("seura", "form.lisatiedot.urheilija.valmennusryhma.seura", 100, formParameters);
        valmennusryhmaGroup.addChild(seura);

        urheilijanLisakysymyksetTeema.addChild(opinnotGroup, urheilulajitGroup,
                saavutuksetGroup, valmentajaGroup, valmennusryhmaGroup);
        return urheilijanLisakysymyksetSaanto;
    }

    private static TitledGroup createUrheilulajitRyhma(final FormParameters formParameters) {
        TitledGroup urheilulajit = new TitledGroup(ElementUtil.randomId(),
                createI18NText("form.lisatiedot.urheilija.urheilu", formParameters.getFormMessagesBundle()));

        TextQuestion urheilulaji1 = createTextQuestion("Urheilulaji1", "form.lisatiedot.urheilija.urheilu.urheilulaji", 50, formParameters);
        TextQuestion lajiliitto1 = createTextQuestion("lajiliitto1", "form.lisatiedot.urheilija.urheilu.lajiliitto", 50, formParameters);

        TextQuestion urheilulaji2 = createTextQuestion("Urheilulaji2", "form.lisatiedot.urheilija.urheilu.urheilulaji2", 50, formParameters);
        urheilulaji2.setHelp(createI18NText("form.lisatiedot.urheilija.urheilu.urheilulaji2.help", formParameters.getFormMessagesBundle()));
        TextQuestion lajiliitto2 = createTextQuestion("lajiliitto2", "form.lisatiedot.urheilija.urheilu.lajiliitto2", 50, formParameters);

        urheilulajit.addChild(urheilulaji1, lajiliitto1, urheilulaji2, lajiliitto2);
        return urheilulajit;
    }

    private static TitledGroup createAiemmatOpinnotRyhma(final FormParameters formParameters) {
        TitledGroup opinnotGroup = new TitledGroup(ElementUtil.randomId(),
                createI18NText("form.lisatiedot.urheilija.opinnot", formParameters.getFormMessagesBundle()));

        TextQuestion liikunnanopettaja = createTextQuestion("liikunnanopettajan-nimi",
                "form.lisatiedot.urheilija.opinnot.liikunnanopettajan.nimi", 50, formParameters);

        TextQuestion lukuaineidenKeskiarvo = createTextQuestion("lukuaineiden-keskiarvo",
                "form.lisatiedot.urheilija.opinnot.lukuaineiden.keskiarvo", 4, formParameters);

        TextQuestion pakollinenLiikunnanNumero = createTextQuestion("pakollinen-liikunnan-numero",
                "form.lisatiedot.urheilija.opinnot.pakollisen.liikunnan.numero", 2, formParameters);

        opinnotGroup.addChild(liikunnanopettaja, lukuaineidenKeskiarvo, pakollinenLiikunnanNumero);
        return opinnotGroup;
    }

    private static TitledGroup createSaavutuksetRyhma(final FormParameters formParameters) {
        TitledGroup saavutuksetGroup = new TitledGroup(ElementUtil.randomId(),
                createI18NText("form.lisatiedot.urheilija.saavutukset", formParameters.getFormMessagesBundle()));

        TextArea saavutukset = new TextArea("saavutukset",
                createI18NText("form.lisatiedot.urheilija.saavutukset.saavutukset", formParameters.getFormMessagesBundle()));
        addMaxLengthAttributeAndLengthValidator(saavutukset, 2000, formParameters);
        saavutukset.setInline(true);
        saavutukset.setHelp(createI18NText("form.lisatiedot.urheilija.saavutukset.saavutukset.help", formParameters.getFormMessagesBundle()));
        saavutuksetGroup.addChild(saavutukset);
        return saavutuksetGroup;
    }

    private static TextQuestion createTextQuestion(final String id, final String messageKey, int maxlength, final FormParameters formParameters) {
        TextQuestion textQuestion = new TextQuestion(id, createI18NText(messageKey, formParameters.getFormMessagesBundle()));
        textQuestion.setInline(true);
        addSizeAttribute(textQuestion, 30);
        addMaxLengthAttributeAndLengthValidator(textQuestion, maxlength, formParameters);
        return textQuestion;
    }
}
