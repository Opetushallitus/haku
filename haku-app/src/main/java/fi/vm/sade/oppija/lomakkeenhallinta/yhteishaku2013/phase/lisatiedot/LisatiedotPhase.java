package fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.lisatiedot;

import com.google.common.collect.ImmutableList;
import fi.vm.sade.oppija.lomake.domain.elements.Element;
import fi.vm.sade.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.oppija.lomake.domain.elements.Theme;
import fi.vm.sade.oppija.lomake.domain.elements.TitledGroup;
import fi.vm.sade.oppija.lomake.domain.elements.custom.WorkExperienceTheme;
import fi.vm.sade.oppija.lomake.domain.elements.questions.CheckBox;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Radio;
import fi.vm.sade.oppija.lomake.domain.elements.questions.TextArea;
import fi.vm.sade.oppija.lomake.domain.elements.questions.TextQuestion;
import fi.vm.sade.oppija.lomake.domain.rules.RelatedQuestionRule;
import fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.oppija.lomakkeenhallinta.util.OppijaConstants;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil.*;

public class LisatiedotPhase {
    public static final int AGE_WORK_EXPERIENCE = 16;
    public static final String TYOKOKEMUS_PATTERN = "^$|^([0-9]|[1-9][0-9]|[1-9][0-9][0-9]|1000)$";

    public static Phase create(final Date start) {
        Phase lisatiedot = new Phase("lisatiedot", createI18NForm("form.lisatiedot.otsikko"), false);
        lisatiedot.addChild(createTyokokemus(start));
        lisatiedot.addChild(createLupatiedot());
        lisatiedot.addChild(createUrheilijanLisakysymykset());
        return lisatiedot;
    }

    private static WorkExperienceTheme createTyokokemus(final Date start) {
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(start);
        cal.roll(Calendar.YEAR, -AGE_WORK_EXPERIENCE);
        WorkExperienceTheme workExperienceTheme = new WorkExperienceTheme("tyokokemusGrp",
                createI18NForm("form.lisatiedot.tyokokemus"), "32", cal.getTime());
        workExperienceTheme.setHelp(createI18NForm("form.tyokokemus.help"));
        TextQuestion tyokokemuskuukaudet = new TextQuestion("TYOKOKEMUSKUUKAUDET",
                createI18NForm("form.tyokokemus.kuukausina"));
        tyokokemuskuukaudet
                .setHelp(createI18NForm("form.tyokokemus.kuukausina.help"));
        tyokokemuskuukaudet.setValidator(createRegexValidator(tyokokemuskuukaudet.getId(), TYOKOKEMUS_PATTERN));
        tyokokemuskuukaudet.addAttribute("size", "8");
        setVerboseHelp(tyokokemuskuukaudet, "form.tyokokemus.kuukausina.verboseHelp");
        workExperienceTheme.addChild(tyokokemuskuukaudet);
        return workExperienceTheme;
    }

    private static Theme createLupatiedot() {
        Theme lupatiedotTheme = new Theme("lupatiedotGrp", createI18NForm("form.lisatiedot.lupatiedot"), true);
        CheckBox lupaMarkkinointi = new CheckBox(
                "lupaMarkkinointi",
                createI18NForm("form.lupatiedot.saaMarkkinoida"));
        CheckBox lupaJulkaisu = new CheckBox("lupaJulkaisu",
                createI18NForm("form.lupatiedot.saaJulkaista"));
        CheckBox lupaSahkoisesti = new CheckBox("lupaSahkoisesti",
                createI18NForm("form.lupatiedot.saaLahettaaSahkoisesti"));
        CheckBox lupaSms = new CheckBox(
                "lupaSms",
                createI18NForm("form.lupatiedot.saaLahettaaTekstiviesteja"));

        TitledGroup lupaGroup = new TitledGroup("permissionCheckboxes", createI18NForm("form.lupatiedot.otsikko"));

        lupaGroup.addChild(lupaMarkkinointi);
        lupaGroup.addChild(lupaJulkaisu);
        lupaGroup.addChild(lupaSahkoisesti);
        lupaGroup.addChild(lupaSms);
        lupatiedotTheme.addChild(lupaGroup);
        setVerboseHelp(lupatiedotTheme, "form.lisatiedot.lupatiedot.verboseHelp");

        Radio asiointikieli = new Radio(OppijaConstants.ELEMENT_ID_CONTACT_LANGUAGE, createI18NForm("form.asiointikieli.otsikko"));
        asiointikieli.setHelp(createI18NForm("form.asiointikieli.help"));
        asiointikieli.addOption("suomi", createI18NForm("form.asiointikieli.suomi"), "suomi");
        asiointikieli.addOption("ruotsi", createI18NForm("form.asiointikieli.ruotsi"), "ruotsi");
        addRequiredValidator(asiointikieli);
        setVerboseHelp(asiointikieli, "form.asiointikieli.otsikko.verboseHelp");
        lupatiedotTheme.addChild(asiointikieli);
        return lupatiedotTheme;
    }

    private static Element createUrheilijanLisakysymykset() {
        Theme urheilijanLisakysymyksetTeema = new Theme(ElementUtil.randomId(), createI18NForm("form.lisatiedot.urheilija"), true);

        ImmutableList<String> ids = ImmutableList.of(
                "preference1_urheilijan_ammatillisen_koulutuksen_lisakysymys",
                "preference2_urheilijan_ammatillisen_koulutuksen_lisakysymys",
                "preference3_urheilijan_ammatillisen_koulutuksen_lisakysymys",
                "preference4_urheilijan_ammatillisen_koulutuksen_lisakysymys",
                "preference5_urheilijan_ammatillisen_koulutuksen_lisakysymys"
        );
        RelatedQuestionRule urheilijanLisakysymyksetSaanto = new RelatedQuestionRule(ElementUtil.randomId(), ids, ElementUtil.KYLLA, false);
        urheilijanLisakysymyksetSaanto.addChild(urheilijanLisakysymyksetTeema);

        TitledGroup opinnotGroup = new TitledGroup(ElementUtil.randomId(), createI18NForm("form.lisatiedot.urheilija.opinnot"));
        TextQuestion liikunnanopettaja = new TextQuestion("liikunnanopettajan-nimi", createI18NForm("form.lisatiedot.urheilija.opinnot.liikunnanopettajan.nimi"));
        liikunnanopettaja.setInline(true);
        liikunnanopettaja.addAttribute("size", "30");

        TextQuestion lukuaineidenKeskiarvo = new TextQuestion("lukuaineiden-keskiarvo", createI18NForm("form.lisatiedot.urheilija.opinnot.lukuaineiden.keskiarvo"));
        lukuaineidenKeskiarvo.setInline(true);
        lukuaineidenKeskiarvo.addAttribute("size", "30");

        TextQuestion pakollinenLiikunnanNumero = createTextQuestion("pakollinen-liikunnan-numero", "form.lisatiedot.urheilija.opinnot.pakollisen.liikunnan.numero");

        opinnotGroup.addChild(liikunnanopettaja, lukuaineidenKeskiarvo, pakollinenLiikunnanNumero);

        TitledGroup urheilulajitGroup = createUrheilulajitRyhma();

        TitledGroup saavutuksetGroup = new TitledGroup(ElementUtil.randomId(), createI18NForm("form.lisatiedot.urheilija.saavutukset"));
        TextArea saavutukset = new TextArea("saavutukset", createI18NForm("form.lisatiedot.urheilija.saavutukset.saavutukset"));
        saavutukset.addAttribute("cols", "50");
        saavutukset.addAttribute("rows", "4");
        saavutukset.addAttribute("maxlength", "200");
        saavutukset.setInline(true);
        saavutukset.setHelp(createI18NForm("form.lisatiedot.urheilija.saavutukset.saavutukset.help"));
        saavutuksetGroup.addChild(saavutukset);

        TitledGroup valmentajaGroup = new TitledGroup(ElementUtil.randomId(), createI18NForm("form.lisatiedot.urheilija.valmentajan.yhteystiedot"));
        TextQuestion valmentajanNimi = new TextQuestion("valmentajan-nimi", createI18NForm("form.lisatiedot.urheilija.valmentajan.yhteystiedot.nimi"));
        valmentajanNimi.setInline(true);
        valmentajanNimi.addAttribute("size", "30");
        valmentajaGroup.addChild(valmentajanNimi);
        TextQuestion valmentajanPuhelinnumero = new TextQuestion("valmentajan-puhelinnumero", createI18NForm("form.lisatiedot.urheilija.valmentajan.yhteystiedot.puhelinnumero"));
        valmentajanPuhelinnumero.setInline(true);
        valmentajanPuhelinnumero.addAttribute("size", "30");
        valmentajaGroup.addChild(valmentajanPuhelinnumero);
        TextQuestion email = new TextQuestion("valmentajan-sähköpostiosoite", createI18NForm("form.lisatiedot.urheilija.valmentajan.yhteystiedot.sahkopostiosoite"));
        email.addAttribute("size", "50");
        email.setValidator(createRegexValidator(email.getId(), EMAIL_REGEX));
        email.setInline(true);
        valmentajaGroup.addChild(email);


        TitledGroup valmennusryhmaGroup = new TitledGroup(ElementUtil.randomId(), createI18NForm("form.lisatiedot.urheilija.valmennusryhma"));
        TextQuestion lajiliitto = new TextQuestion("lajiliitto-maajoukkue", createI18NForm("form.lisatiedot.urheilija.valmennusryhma.lajiliitto"));
        lajiliitto.setInline(true);
        lajiliitto.addAttribute("size", "30");
        valmennusryhmaGroup.addChild(lajiliitto);
        TextQuestion piiri = new TextQuestion("alue-piiri", createI18NForm("form.lisatiedot.urheilija.valmennusryhma.alue"));
        piiri.setInline(true);
        piiri.addAttribute("size", "30");
        valmennusryhmaGroup.addChild(piiri);
        TextQuestion seura = new TextQuestion("seura", createI18NForm("form.lisatiedot.urheilija.valmennusryhma.seura"));
        seura.addAttribute("size", "30");
        seura.setInline(true);
        valmennusryhmaGroup.addChild(seura);
        urheilijanLisakysymyksetTeema.addChild(opinnotGroup, urheilulajitGroup,
                saavutuksetGroup, valmentajaGroup, valmennusryhmaGroup);
        return urheilijanLisakysymyksetSaanto;
    }

    private static TitledGroup createUrheilulajitRyhma() {
        TitledGroup urheilulajit = new TitledGroup(ElementUtil.randomId(), createI18NForm("form.lisatiedot.urheilija.urheilu"));

        TextQuestion urheilulaji1 = createTextQuestion("Urheilulaji1", "form.lisatiedot.urheilija.urheilu.urheilulaji");
        TextQuestion lajiliitto1 = createTextQuestion("lajiliitto1", "form.lisatiedot.urheilija.urheilu.lajiliitto");

        TextQuestion urheilulaji2 = createTextQuestion("Urheilulaji2", "form.lisatiedot.urheilija.urheilu.urheilulaji2");
        urheilulaji2.setHelp(createI18NForm("form.lisatiedot.urheilija.urheilu.urheilulaji2.help"));
        TextQuestion lajiliitto2 = createTextQuestion("lajiliitto2", "form.lisatiedot.urheilija.urheilu.lajiliitto2");

        urheilulajit.addChild(urheilulaji1, lajiliitto1, urheilulaji2, lajiliitto2);
        return urheilulajit;
    }

    private static TextQuestion createTextQuestion(final String id, final String messageKey) {
        TextQuestion textQuestion = new TextQuestion(id, createI18NForm(messageKey));
        textQuestion.setInline(true);
        textQuestion.addAttribute("size", "30");
        return textQuestion;
    }
}
