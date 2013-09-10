package fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.henkilotiedot;

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.oppija.common.koodisto.KoodistoService;
import fi.vm.sade.oppija.lomake.domain.PostOffice;
import fi.vm.sade.oppija.lomake.domain.elements.Element;
import fi.vm.sade.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.oppija.lomake.domain.elements.Theme;
import fi.vm.sade.oppija.lomake.domain.elements.custom.PostalCode;
import fi.vm.sade.oppija.lomake.domain.elements.custom.SocialSecurityNumber;
import fi.vm.sade.oppija.lomake.domain.elements.questions.*;
import fi.vm.sade.oppija.lomake.domain.rules.AddElementRule;
import fi.vm.sade.oppija.lomake.domain.rules.RelatedQuestionRule;
import fi.vm.sade.oppija.lomake.validation.validators.ContainedInOtherFieldValidator;
import fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil.*;

public class HenkilotiedotPhase {

    public static final String MOBILE_PHONE_PATTERN =
            "^$|^(?!\\+358|0)[\\+]?[0-9\\-\\s]+$|^(\\+358|0)[\\-\\s]*((4[\\-\\s]*[0-6])|50)[0-9\\-\\s]*$";
    public static final String PHONE_PATTERN = "^$|^\\+?[0-9\\-\\s]+$";
    private static final String NOT_FI = "^((?!FIN)[A-Z]{3})$";
    public static final String AIDINKIELI_ID = "aidinkieli";
    private static final String HETU_PATTERN = "^([0-9]{6}.[0-9]{3}([0-9]|[a-z]|[A-Z]))$";
    private static final String POSTINUMERO_PATTERN = "[0-9]{5}";
    private static final String DATE_PATTERN = "^(0[1-9]|[12][0-9]|3[01])\\.(0[1-9]|1[012])\\.(19|20)\\d\\d$";

    private HenkilotiedotPhase() {
    }

    public static Phase create(final KoodistoService koodistoService) {

        // Henkilötiedot
        Phase henkilotiedot = new Phase("henkilotiedot", createI18NForm("form.henkilotiedot.otsikko"), false);

        Theme henkilotiedotRyhma = new Theme("HenkilotiedotGrp", createI18NForm("form.henkilotiedot.otsikko"), true);

        // Nimet
        Question sukunimi = createRequiredTextQuestion("Sukunimi", "form.henkilotiedot.sukunimi", "30");
        sukunimi.setInline(true);
        sukunimi.setValidator(createRegexValidator(sukunimi.getId(), ElementUtil.ISO88591_NAME_REGEX));
        henkilotiedotRyhma.addChild(sukunimi);

        Question etunimet = createRequiredTextQuestion("Etunimet", "form.henkilotiedot.etunimet", "30");
        etunimet.setInline(true);
        etunimet.setValidator(createRegexValidator(etunimet.getId(), ElementUtil.ISO88591_NAME_REGEX));
        henkilotiedotRyhma.addChild(etunimet);

        TextQuestion kutsumanimi = new TextQuestion("Kutsumanimi", createI18NForm("form.henkilotiedot.kutsumanimi"));
        kutsumanimi.setHelp(createI18NForm("form.henkilotiedot.kutsumanimi.help"));
        kutsumanimi.addAttribute("size", "20");
        kutsumanimi.setValidator(
                new ContainedInOtherFieldValidator(kutsumanimi.getId(),
                        etunimet.getId(),
                        ElementUtil.createI18NTextError("yleinen.virheellinenArvo")));
        kutsumanimi.setValidator(
                createRegexValidator(kutsumanimi.getId(), ISO88591_NAME_REGEX));
        setRequiredInlineAndVerboseHelp(kutsumanimi, "form.henkilotiedot.kutsumanimi.verboseHelp");

        henkilotiedotRyhma.addChild(kutsumanimi);

        // Kansalaisuus, hetu ja sukupuoli suomalaisille
        DropdownSelect kansalaisuus =
                new DropdownSelect("kansalaisuus", createI18NForm("form.henkilotiedot.kansalaisuus"), null);
        kansalaisuus.addOptions(koodistoService.getNationalities());
        setDefaultOption("FIN", kansalaisuus.getOptions());
        kansalaisuus.addAttribute("placeholder", "Valitse kansalaisuus");
        kansalaisuus.setHelp(createI18NForm("form.henkilotiedot.kansalaisuus.help"));
        setRequiredInlineAndVerboseHelp(kansalaisuus, "form.henkilotiedot.kansalaisuus.verboseHelp");
        henkilotiedotRyhma.addChild(kansalaisuus);

        TextQuestion henkilotunnus =
                new TextQuestion("Henkilotunnus", createI18NForm("form.henkilotiedot.henkilotunnus"));
        henkilotunnus.addAttribute("placeholder", "ppkkvv*****");
        henkilotunnus.addAttribute("size", "11");
        henkilotunnus.addAttribute("maxlength", "11");
        henkilotunnus.setHelp(createI18NForm("form.henkilotiedot.henkilotunnus.help"));
        henkilotunnus.setValidator(createRegexValidator(henkilotunnus.getId(), HETU_PATTERN));
        setRequiredInlineAndVerboseHelp(henkilotunnus, "form.henkilotiedot.henkilotunnus.verboseHelp");

        Radio sukupuoli = new Radio("sukupuoli", createI18NForm("form.henkilotiedot.sukupuoli"));
        sukupuoli.addOptions(koodistoService.getGenders());
        setRequiredInlineAndVerboseHelp(sukupuoli, "form.henkilotiedot.sukupuoli.verboseHelp");

        Option male = sukupuoli.getOptions().get(0).getI18nText().getTranslations().get("fi").equalsIgnoreCase("Mies") ?
                sukupuoli.getOptions().get(0) : sukupuoli.getOptions().get(1);
        Option female = sukupuoli.getOptions().get(0).getI18nText().getTranslations().get("fi").equalsIgnoreCase("Nainen") ?
                sukupuoli.getOptions().get(0) : sukupuoli.getOptions().get(1);
        SocialSecurityNumber socialSecurityNumber =
                new SocialSecurityNumber("ssn_question", createI18NForm("form.henkilotiedot.hetu"),
                        sukupuoli.getI18nText(), male, female, sukupuoli.getId(), henkilotunnus);
        addSsnUniqueValidator(socialSecurityNumber);

        RelatedQuestionRule hetuRule = new RelatedQuestionRule("hetuRule", kansalaisuus.getId(), "^$|^FIN$", true);
        hetuRule.addChild(socialSecurityNumber);
        henkilotiedotRyhma.addChild(hetuRule);

        // Ulkomaalaisten tunnisteet
        Radio onkoSinullaSuomalainenHetu = new Radio("onkoSinullaSuomalainenHetu",
                createI18NForm("form.henkilotiedot.hetu.onkoSuomalainen"));
        addDefaultTrueFalseOptions(onkoSinullaSuomalainenHetu);
        setRequiredInlineAndVerboseHelp(onkoSinullaSuomalainenHetu, "form.henkilotiedot.hetu.onkoSuomalainen.verboseHelp");
        RelatedQuestionRule suomalainenHetuRule = new RelatedQuestionRule("suomalainenHetuRule",
                onkoSinullaSuomalainenHetu.getId(), "^true$", false);
        suomalainenHetuRule.addChild(socialSecurityNumber);
        onkoSinullaSuomalainenHetu.addChild(suomalainenHetuRule);

        RelatedQuestionRule eiSuomalaistaHetuaRule = new RelatedQuestionRule("eiSuomalaistaHetuaRule",
                onkoSinullaSuomalainenHetu.getId(), "^false$", false);
        eiSuomalaistaHetuaRule.addChild(sukupuoli);

        DateQuestion syntymaaika = new DateQuestion("syntymaaika", createI18NForm("form.henkilotiedot.syntymaaika"));
        syntymaaika.setValidator(ElementUtil.createRegexValidator(syntymaaika.getId(), DATE_PATTERN));
        addRequiredValidator(syntymaaika);
        syntymaaika.setInline(true);
        eiSuomalaistaHetuaRule.addChild(syntymaaika);

        TextQuestion syntymapaikka =
                new TextQuestion("syntymapaikka", createI18NForm("form.henkilotiedot.syntymapaikka"));
        syntymapaikka.addAttribute("size", "30");
        addRequiredValidator(syntymapaikka);
        syntymapaikka.setInline(true);
        eiSuomalaistaHetuaRule.addChild(syntymapaikka);

        TextQuestion kansallinenIdTunnus =
                new TextQuestion("kansallinenIdTunnus", createI18NForm("form.henkilotiedot.kansallinenId"));
        kansallinenIdTunnus.addAttribute("size", "30");
        kansallinenIdTunnus.setInline(true);
        eiSuomalaistaHetuaRule.addChild(kansallinenIdTunnus);

        TextQuestion passinnumero = new TextQuestion("passinnumero", createI18NForm("form.henkilotiedot.passinnumero"));
        passinnumero.addAttribute("size", "30");
        passinnumero.setInline(true);
        eiSuomalaistaHetuaRule.addChild(passinnumero);

        onkoSinullaSuomalainenHetu.addChild(eiSuomalaistaHetuaRule);

        RelatedQuestionRule ulkomaalaisenTunnisteetRule = new RelatedQuestionRule("ulkomaalaisenTunnisteetRule",
                kansalaisuus.getId(), NOT_FI, false);
        ulkomaalaisenTunnisteetRule.addChild(onkoSinullaSuomalainenHetu);
        henkilotiedotRyhma.addChild(ulkomaalaisenTunnisteetRule);

        // Email
        TextQuestion email = new TextQuestion("Sähköposti", createI18NForm("form.henkilotiedot.email"));
        email.addAttribute("size", "50");
        email.setValidator(createRegexValidator(email.getId(), EMAIL_REGEX));
        email.setHelp(createI18NForm("form.henkilotiedot.email.help"));
        ElementUtil.setVerboseHelp(email, "form.henkilotiedot.email.verboseHelp");
        email.setInline(true);
        henkilotiedotRyhma.addChild(email);

        // Matkapuhelinnumerot

        TextQuestion puhelinnumero1 = new TextQuestion("matkapuhelinnumero1",
                createI18NForm("form.henkilotiedot.matkapuhelinnumero"));
        puhelinnumero1.setHelp(createI18NForm("form.henkilotiedot.matkapuhelinnumero.help"));
        puhelinnumero1.addAttribute("size", "30");
        puhelinnumero1.setValidator(createRegexValidator(puhelinnumero1.getId(), MOBILE_PHONE_PATTERN));
        ElementUtil.setVerboseHelp(puhelinnumero1, "form.henkilotiedot.matkapuhelinnumero.verboseHelp");
        puhelinnumero1.setInline(true);
        henkilotiedotRyhma.addChild(puhelinnumero1);

        TextQuestion prevNum = puhelinnumero1;
        AddElementRule prevRule = null;
        for (int i = 2; i <= 5; i++) {
            TextQuestion extranumero = new TextQuestion("matkapuhelinnumero" + i,
                    createI18NForm("form.henkilotiedot.puhelinnumero"));
            extranumero.addAttribute("size", "30");
            extranumero.setValidator(createRegexValidator(extranumero.getId(), PHONE_PATTERN));
            extranumero.setInline(true);

            AddElementRule extranumeroRule = new AddElementRule("addPuhelinnumero" + i + "Rule", prevNum.getId(),
                    createI18NForm("form.henkilotiedot.puhelinnumero.lisaa"));
            extranumeroRule.addChild(extranumero);
            if (i == 2) {
                henkilotiedotRyhma.addChild(extranumeroRule);
            } else {
                prevRule.addChild(extranumeroRule);
            }
            prevNum = extranumero;
            prevRule = extranumeroRule;
        }


        // Asuinmaa, osoite
        DropdownSelect asuinmaa = new DropdownSelect("asuinmaa", createI18NForm("form.henkilotiedot.asuinmaa"), null);
        asuinmaa.addOptions(koodistoService.getCountries());
        setDefaultOption("FIN", asuinmaa.getOptions());
        asuinmaa.addAttribute("placeholder", "Valitse kansalaisuus");
        setRequiredInlineAndVerboseHelp(asuinmaa, "form.henkilotiedot.asuinmaa.verboseHelp");

        RelatedQuestionRule asuinmaaFI = new RelatedQuestionRule("rule1", asuinmaa.getId(), "FIN", true);
        Question lahiosoite = createRequiredTextQuestion("lahiosoite", "form.henkilotiedot.lahiosoite", "40");
        lahiosoite.setInline(true);
        asuinmaaFI.addChild(lahiosoite);

        Element postinumero = new PostalCode("Postinumero", createI18NForm("form.henkilotiedot.postinumero"),
                createPostOffices(koodistoService));
        postinumero.addAttribute("size", "5");
        postinumero.addAttribute("maxlength", "5");
        addRequiredValidator(postinumero);
        postinumero.addAttribute("placeholder", "#####");
        postinumero.setValidator(createRegexValidator(postinumero.getId(), POSTINUMERO_PATTERN));
        postinumero.setHelp(createI18NForm("form.henkilotiedot.postinumero.help"));
        asuinmaaFI.addChild(postinumero);

        DropdownSelect kotikunta =
                new DropdownSelect("kotikunta", createI18NForm("form.henkilotiedot.kotikunta"), null);
        kotikunta.addOption("eiValittu", ElementUtil.createI18NAsIs(""), "");
        kotikunta.addOptions(koodistoService.getMunicipalities());
        kotikunta.addAttribute("placeholder", "Valitse kotikunta");
        setRequiredInlineAndVerboseHelp(kotikunta, "form.henkilotiedot.kotikunta.verboseHelp");
        kotikunta.setHelp(createI18NForm("form.henkilotiedot.kotikunta.help"));
        asuinmaaFI.addChild(kotikunta);

        /*CheckBox ensisijainenOsoite = new CheckBox("ensisijainenOsoite1",
                createI18NForm("form.henkilotiedot.ensisijainenOsoite"));
        ensisijainenOsoite.setInline(true);
        asuinmaaFI.addChild(ensisijainenOsoite);*/

        RelatedQuestionRule relatedQuestionRule2 =
                new RelatedQuestionRule("rule2", asuinmaa.getId(), NOT_FI, false);
        Question osoiteUlkomaa = createRequiredTextQuestion("osoiteUlkomaa", "form.henkilotiedot.osoite", "40");
        osoiteUlkomaa.setInline(true);
        relatedQuestionRule2.addChild(osoiteUlkomaa);
        Question postinumeroUlkomaa = createRequiredTextQuestion("postinumeroUlkomaa", "form.henkilotiedot.postinumero", "12");
        postinumeroUlkomaa.setInline(true);
        relatedQuestionRule2.addChild(postinumeroUlkomaa);
        Question kaupunkiUlkomaa = createRequiredTextQuestion("kaupunkiUlkomaa", "form.henkilotiedot.kaupunki", "25");
        kaupunkiUlkomaa.setInline(true);
        relatedQuestionRule2.addChild(kaupunkiUlkomaa);

        asuinmaa.addChild(relatedQuestionRule2);
        asuinmaa.addChild(asuinmaaFI);

        henkilotiedotRyhma.addChild(asuinmaa);

        // Äidinkieli
        DropdownSelect aidinkieli =
                new DropdownSelect(AIDINKIELI_ID, createI18NForm("form.henkilotiedot.aidinkieli"),
                        "fi_vm_sade_oppija_language");
        aidinkieli.addOption("eiValittu", ElementUtil.createI18NAsIs(""), "");
        aidinkieli.addOptions(koodistoService.getLanguages());
        aidinkieli.addAttribute("placeholder", "Valitse Äidinkieli");
        setRequiredInlineAndVerboseHelp(aidinkieli, "form.henkilotiedot.aidinkieli.verboseHelp");
        aidinkieli.setHelp(createI18NForm("form.henkilotiedot.aidinkieli.help"));
        henkilotiedotRyhma.addChild(aidinkieli);

        henkilotiedot.addChild(henkilotiedotRyhma);
        return henkilotiedot;
    }

    private static Map<String, PostOffice> createPostOffices(final KoodistoService koodistoService) {
        List<PostOffice> listOfPostOffices = koodistoService.getPostOffices();
        Map<String, PostOffice> postOfficeMap = new HashMap<String, PostOffice>(listOfPostOffices.size());
        for (PostOffice postOffice : listOfPostOffices) {
            postOfficeMap.put(postOffice.getPostcode(), postOffice);
        }
        return ImmutableMap.copyOf(postOfficeMap);
    }
}
