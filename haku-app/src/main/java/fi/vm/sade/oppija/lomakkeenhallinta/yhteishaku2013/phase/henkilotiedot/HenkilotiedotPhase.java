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

    public static Phase create(final KoodistoService koodistoService) {

        // Henkilötiedot
        Phase henkilotiedot = new Phase("henkilotiedot", createI18NForm("form.henkilotiedot.otsikko"), false);

        Theme henkilotiedotRyhma = new Theme("HenkilotiedotGrp", createI18NForm("form.henkilotiedot.otsikko"), null, true);

        // Nimet
        Question sukunimi = createRequiredTextQuestion("Sukunimi", "form.henkilotiedot.sukunimi", "30");
        sukunimi.setInline(true);
        sukunimi.addAttribute("pattern", ElementUtil.ISO88591_NAME_REGEX);
        henkilotiedotRyhma.addChild(sukunimi);

        Question etunimet = createRequiredTextQuestion("Etunimet", "form.henkilotiedot.etunimet", "30");
        etunimet.setInline(true);
        sukunimi.addAttribute("pattern", ElementUtil.ISO88591_NAME_REGEX);
        henkilotiedotRyhma.addChild(etunimet);

        TextQuestion kutsumanimi = new TextQuestion("Kutsumanimi", createI18NForm("form.henkilotiedot.kutsumanimi"));
        kutsumanimi.setHelp(createI18NForm("form.henkilotiedot.kutsumanimi.help"));
        kutsumanimi.addAttribute("size", "20");
        kutsumanimi.addAttribute("containedInOther", "Etunimet");
        kutsumanimi.addAttribute("pattern", ElementUtil.ISO88591_NAME_REGEX);
        setRequiredInlineAndVerboseHelp(kutsumanimi);

        henkilotiedotRyhma.addChild(kutsumanimi);

        // Kansalaisuus, hetu ja sukupuoli suomalaisille
        DropdownSelect kansalaisuus =
                new DropdownSelect("kansalaisuus", createI18NForm("form.henkilotiedot.kansalaisuus"), null);
        kansalaisuus.addOptions(koodistoService.getNationalities());
        setDefaultOption("FIN", kansalaisuus.getOptions());
        kansalaisuus.addAttribute("placeholder", "Valitse kansalaisuus");
        kansalaisuus.setHelp(createI18NForm("form.henkilotiedot.kansalaisuus.help"));
        setRequiredInlineAndVerboseHelp(kansalaisuus);
        henkilotiedotRyhma.addChild(kansalaisuus);

        TextQuestion henkilotunnus =
                new TextQuestion("Henkilotunnus", createI18NForm("form.henkilotiedot.henkilotunnus"));
        henkilotunnus.addAttribute("placeholder", "ppkkvv*****");
        henkilotunnus.addAttribute("pattern", "^([0-9]{6}.[0-9]{3}([0-9]|[a-z]|[A-Z]))$");
        henkilotunnus.addAttribute("size", "11");
        henkilotunnus.addAttribute("maxlength", "11");
        setRequiredInlineAndVerboseHelp(henkilotunnus);

        Radio sukupuoli = new Radio("sukupuoli", createI18NForm("form.henkilotiedot.sukupuoli"));
        sukupuoli.addOptions(koodistoService.getGenders());
        setRequiredInlineAndVerboseHelp(sukupuoli);

        SocialSecurityNumber socialSecurityNumber =
                new SocialSecurityNumber("ssn_question", createI18NForm("form.henkilotiedot.hetu"),
                        sukupuoli.getI18nText(), sukupuoli.getOptions().get(0),
                        sukupuoli.getOptions().get(1), sukupuoli.getId(), henkilotunnus);

        RelatedQuestionRule hetuRule = new RelatedQuestionRule("hetuRule", kansalaisuus.getId(), "^$|^FIN$", true);
        hetuRule.addChild(socialSecurityNumber);
        henkilotiedotRyhma.addChild(hetuRule);

        // Ulkomaalaisten tunnisteet
        Radio onkoSinullaSuomalainenHetu = new Radio("onkoSinullaSuomalainenHetu",
                createI18NForm("form.henkilotiedot.hetu.onkoSuomalainen"));
        addDefaultTrueFalseOptions(onkoSinullaSuomalainenHetu);
        setRequiredInlineAndVerboseHelp(onkoSinullaSuomalainenHetu);
        RelatedQuestionRule suomalainenHetuRule = new RelatedQuestionRule("suomalainenHetuRule",
                onkoSinullaSuomalainenHetu.getId(), "^true", false);
        suomalainenHetuRule.addChild(socialSecurityNumber);
        onkoSinullaSuomalainenHetu.addChild(suomalainenHetuRule);

        RelatedQuestionRule eiSuomalaistaHetuaRule = new RelatedQuestionRule("eiSuomalaistaHetuaRule",
                onkoSinullaSuomalainenHetu.getId(), "^false", false);
        eiSuomalaistaHetuaRule.addChild(sukupuoli);

        DateQuestion syntymaaika = new DateQuestion("syntymaaika", createI18NForm("form.henkilotiedot.syntymaaika"));
        syntymaaika.addAttribute("required", "required");
        syntymaaika.setInline(true);
        eiSuomalaistaHetuaRule.addChild(syntymaaika);

        TextQuestion syntymapaikka =
                new TextQuestion("syntymapaikka", createI18NForm("form.henkilotiedot.syntymapaikka"));
        syntymapaikka.addAttribute("size", "30");
        syntymapaikka.addAttribute("required", "required");
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
        email.addAttribute("pattern", "^[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$|^$");
        email.setHelp(createI18NForm("form.henkilotiedot.email.help"));
        ElementUtil.setVerboseHelp(email);
        email.setInline(true);
        henkilotiedotRyhma.addChild(email);

        // Matkapuhelinnumerot

        TextQuestion puhelinnumero1 = new TextQuestion("matkapuhelinnumero1",
                createI18NForm("form.henkilotiedot.matkapuhelinnumero"));
        puhelinnumero1.setHelp(createI18NForm("form.henkilotiedot.matkapuhelinnumero.help"));
        puhelinnumero1.addAttribute("size", "30");
        puhelinnumero1.addAttribute("pattern", MOBILE_PHONE_PATTERN);
        ElementUtil.setVerboseHelp(puhelinnumero1);
        puhelinnumero1.setInline(true);
        henkilotiedotRyhma.addChild(puhelinnumero1);

        TextQuestion prevNum = puhelinnumero1;
        AddElementRule prevRule = null;
        for (int i = 2; i <= 5; i++) {
            TextQuestion extranumero = new TextQuestion("matkapuhelinnumero" + i,
                    createI18NForm("form.henkilotiedot.puhelinnumero"));
            extranumero.addAttribute("size", "30");
            extranumero.addAttribute("pattern", PHONE_PATTERN);
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
        setRequiredInlineAndVerboseHelp(asuinmaa);

        RelatedQuestionRule asuinmaaFI = new RelatedQuestionRule("rule1", asuinmaa.getId(), "FIN", true);
        Question lahiosoite = createRequiredTextQuestion("lahiosoite", "form.henkilotiedot.lahiosoite", "40");
        lahiosoite.setInline(true);
        asuinmaaFI.addChild(lahiosoite);

        Element postinumero = new PostalCode("Postinumero", createI18NForm("form.henkilotiedot.postinumero"),
                createPostOffices(koodistoService));
        postinumero.addAttribute("size", "5");
        postinumero.addAttribute("required", "required");
        postinumero.addAttribute("pattern", "[0-9]{5}");
        postinumero.addAttribute("placeholder", "#####");
        postinumero.addAttribute("maxlength", "5");
        postinumero.setHelp(createI18NForm("form.henkilotiedot.postinumero.help"));
        asuinmaaFI.addChild(postinumero);

        DropdownSelect kotikunta =
                new DropdownSelect("kotikunta", createI18NForm("form.henkilotiedot.kotikunta"), null);
        kotikunta.addOption("eiValittu", ElementUtil.createI18NForm(null), "");
        kotikunta.addOptions(koodistoService.getMunicipalities());
        kotikunta.addAttribute("placeholder", "Valitse kotikunta");
        setRequiredInlineAndVerboseHelp(kotikunta);
        kotikunta.setHelp(createI18NForm("form.henkilotiedot.kotikunta.help"));
        asuinmaaFI.addChild(kotikunta);

        CheckBox ensisijainenOsoite = new CheckBox("ensisijainenOsoite1",
                createI18NForm("form.henkilotiedot.ensisijainenOsoite"));
        ensisijainenOsoite.setInline(true);
        asuinmaaFI.addChild(ensisijainenOsoite);

        TextArea osoite = new TextArea("osoite", createI18NForm("form.henkilotiedot.osoite"));
        osoite.addAttribute("rows", "6");
        osoite.addAttribute("cols", "40");
        osoite.addAttribute("style", "height: 8em");
        RelatedQuestionRule relatedQuestionRule2 =
                new RelatedQuestionRule("rule2", asuinmaa.getId(), NOT_FI, false);
        relatedQuestionRule2.addChild(osoite);
        asuinmaa.addChild(relatedQuestionRule2);
        setRequiredInlineAndVerboseHelp(osoite);

        asuinmaa.addChild(asuinmaaFI);

        henkilotiedotRyhma.addChild(asuinmaa);

        // Äidinkieli
        DropdownSelect aidinkieli =
                new DropdownSelect(AIDINKIELI_ID, createI18NForm("form.henkilotiedot.aidinkieli"),
                        "fi_vm_sade_oppija_language");
        aidinkieli.addOption("eiValittu", ElementUtil.createI18NForm(null), "");
        aidinkieli.addOptions(koodistoService.getLanguages());
        aidinkieli.addAttribute("placeholder", "Valitse Äidinkieli");
        setRequiredInlineAndVerboseHelp(aidinkieli);
        aidinkieli.setHelp(createI18NForm("form.henkilotiedot.aidinkieli.help"));
        henkilotiedotRyhma.addChild(aidinkieli);

        henkilotiedot.addChild(henkilotiedotRyhma);
        return henkilotiedot;
    }

    private static final Map<String, PostOffice> createPostOffices(final KoodistoService koodistoService) {
        List<PostOffice> listOfPostOffices = koodistoService.getPostOffices();
        Map<String, PostOffice> postOfficeMap = new HashMap<String, PostOffice>(listOfPostOffices.size());
        for (PostOffice postOffice : listOfPostOffices) {
            postOfficeMap.put(postOffice.getPostcode(), postOffice);
        }
        return ImmutableMap.copyOf(postOfficeMap);
    }
}
