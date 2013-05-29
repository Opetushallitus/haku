package fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.lisatiedot;

import fi.vm.sade.oppija.lomake.domain.elements.Group;
import fi.vm.sade.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.oppija.lomake.domain.elements.Theme;
import fi.vm.sade.oppija.lomake.domain.elements.custom.WorkExperienceTheme;
import fi.vm.sade.oppija.lomake.domain.elements.questions.CheckBox;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Radio;
import fi.vm.sade.oppija.lomake.domain.elements.questions.TextQuestion;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.FormConstants;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static fi.vm.sade.oppija.lomake.domain.util.ElementUtil.createI18NForm;

public class LisatiedotPhase {
    public static final int AGE_WORK_EXPERIENCE = 16;
    private final Phase lisatiedot;
    private final Theme lupatiedot;
    private final WorkExperienceTheme tyokokemus;

    public LisatiedotPhase(final Date start) {
        lisatiedot = new Phase("lisatiedot", createI18NForm("form.lisatiedot.otsikko"), false);
        tyokokemus = createTyokokemus(start);
        lupatiedot = createLupatiedot();
        lisatiedot.addChild(tyokokemus);
        lisatiedot.addChild(lupatiedot);
    }

    private static WorkExperienceTheme createTyokokemus(final Date start) {
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(start);
        cal.roll(Calendar.YEAR, -AGE_WORK_EXPERIENCE);
        WorkExperienceTheme workExperienceTheme = new WorkExperienceTheme("tyokokemusGrp",
                createI18NForm("form.lisatiedot.tyokokemus"), null, "32", cal.getTime());
        workExperienceTheme.setHelp(createI18NForm("form.tyokokemus.help"));
        TextQuestion tyokokemuskuukaudet = new TextQuestion("TYOKOKEMUSKUUKAUDET",
                createI18NForm("form.tyokokemus.kuukausina"));
        tyokokemuskuukaudet
                .setHelp(createI18NForm("form.tyokokemus.kuukausina.help"));
        tyokokemuskuukaudet.addAttribute("placeholder", "kuukautta");
        tyokokemuskuukaudet.addAttribute("pattern", "^$|^([0-9]|[1-9][0-9]|[1-9][0-9][0-9]|1000)$");
        tyokokemuskuukaudet.addAttribute("size", "8");
        tyokokemuskuukaudet.setVerboseHelp(FormConstants.VERBOSE_HELP);
        workExperienceTheme.addChild(tyokokemuskuukaudet);
        return workExperienceTheme;
    }

    private Theme createLupatiedot() {
        Theme lupatiedotTheme = new Theme("lupatiedotGrp", createI18NForm("form.lisatiedot.lupatiedot"), null, true);
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

        Group lupaGroup = new Group("permissionCheckboxes", createI18NForm("form.lupatiedot.otsikko"));

        lupaGroup.addChild(lupaMarkkinointi);
        lupaGroup.addChild(lupaJulkaisu);
        lupaGroup.addChild(lupaSahkoisesti);
        lupaGroup.addChild(lupaSms);
        lupatiedotTheme.addChild(lupaGroup);
        lupatiedotTheme.setVerboseHelp(FormConstants.VERBOSE_HELP);

        Radio asiointikieli = new Radio("asiointikieli", createI18NForm("form.asiointikieli.otsikko"));
        asiointikieli.setHelp(createI18NForm("form.asiointikieli.help"));
        asiointikieli.addOption("suomi", createI18NForm("form.asiointikieli.suomi"), "suomi");
        asiointikieli.addOption("ruotsi", createI18NForm("form.asiointikieli.ruotsi"), "ruotsi");
        asiointikieli.addAttribute("required", "required");
        asiointikieli.setVerboseHelp(FormConstants.VERBOSE_HELP);
        lupatiedotTheme.addChild(asiointikieli);
        return lupatiedotTheme;
    }

    public Phase getLisatiedot() {
        return lisatiedot;
    }

    public Theme getLupatiedot() {
        return lupatiedot;
    }

    public WorkExperienceTheme getTyokokemus() {
        return tyokokemus;
    }
}
