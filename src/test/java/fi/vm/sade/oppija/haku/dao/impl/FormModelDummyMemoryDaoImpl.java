package fi.vm.sade.oppija.haku.dao.impl;

import fi.vm.sade.oppija.haku.dao.FormModelDAO;
import fi.vm.sade.oppija.haku.domain.*;
import fi.vm.sade.oppija.haku.domain.exception.ResourceNotFoundException;
import fi.vm.sade.oppija.haku.domain.questions.*;
import fi.vm.sade.oppija.haku.service.FormService;
import org.springframework.stereotype.Service;

@Service("FormModelDummyMemoryDao")
public class FormModelDummyMemoryDaoImpl implements FormModelDAO, FormService {

    final ApplicationPeriod applicationPeriod;
    private FormModel formModel;

    public FormModelDummyMemoryDaoImpl() {
        this("yhteishaku", "henkilotiedot");
    }

    public FormModelDummyMemoryDaoImpl(final String formId, final String firstCategoryId) {
        this.applicationPeriod = new ApplicationPeriod("test");
        formModel = new FormModel();
        formModel.addApplicationPeriod(applicationPeriod);
        Category henkilötiedot = new Category(firstCategoryId, "Henkilötiedot");
        Category koulutustausta = new Category("koulutustausta", "Koulutustausta");
        Category yhteenveto = new Category("yhteenveto", "yhteenveto");

        Form form = new Form(formId, "yhteishaku");
        form.addChild(henkilötiedot);
        form.addChild(koulutustausta);
        form.addChild(yhteenveto);
        form.init();

        applicationPeriod.addForm(form);

        QuestionGroup henkilötiedotRyhmä = new QuestionGroup("Henkilotiedot", "Henkilötiedot");
        QuestionGroup koulutustaustaRyhmä = new QuestionGroup("Koulutustausta", "Koulutustausta");
        QuestionGroup yhteenvetoRyhmä = new QuestionGroup("yhteenveto", "yhteenveto");
        henkilötiedot.addChild(henkilötiedotRyhmä);
        koulutustausta.addChild(koulutustaustaRyhmä);
        yhteenveto.addChild(yhteenvetoRyhmä);

        DropdownSelect äidinkieli = new DropdownSelect("äidinkieli", "Äidinkieli", "äidinkieli");
        äidinkieli.addOption("Suomi", "Suomi");
        äidinkieli.addOption("Ruotsi", "Ruotsi");
        äidinkieli.addAttribute("placeholder", "Valitse Äidinkieli");
        äidinkieli.addAttribute("required", "required");

        DropdownSelect kansalaisuus = new DropdownSelect("kansalaisuus", "Kansalaisuus", "kansalaisuus");
        kansalaisuus.addOption("Suomi", "Suomi");
        kansalaisuus.addOption("Ruotsi", "Ruotsi");
        kansalaisuus.addAttribute("placeholder", "Valitse kansalaisuus");
        kansalaisuus.addAttribute("required", "required");

        DropdownSelect kotikunta = new DropdownSelect("kotikunta", "Kotikunta", "kotikunta");
        kotikunta.addOption("Jalasjärvi", "Jalasjärvi");
        kotikunta.addOption("Janakkala", "Janakkala");
        kotikunta.addOption("Joensuu", "Joensuu");
        kotikunta.addOption("Jokioinen", "Jokioinen");
        kotikunta.addOption("Jomala", "Jomala");
        kotikunta.addAttribute("placeholder", "Valitse kotikunta");
        kotikunta.addAttribute("required", "required");

        TextQuestion henkilötunnus = new TextQuestion("Henkilötunnus", "Henkilötunnus", "Henkilötunnus");
        henkilötunnus.addAttribute("placeholder", "ppkkvv*****");
        henkilötunnus.addAttribute("title", "ppkkvv*****");
        henkilötunnus.addAttribute("required", "required");
        henkilötunnus.addAttribute("pattern", "[0-9]{6}.[0-9]{4}");
        TextQuestion kutsumanimi = new TextQuestion("Kutsumanimi", "Kutsumanimi", "Kutsumanimi");
        kutsumanimi.setHelp("Valitse kutsumanimeksi jokin virallisista etunimistäsi");
        kutsumanimi.addAttribute("required", "required");
        TextQuestion sähköposti = new TextQuestion("Sähköposti", "Sähköposti", "Sähköposti");
        sähköposti.setHelp("Kirjoita tähän sähköopstiosoite, johon haluat vastaanottaa opiskelijavalintaan liittyviä tietoja ja jota käytät säännöllisesti.");

        Radio sukupuoli = new Radio("Sukupuoli", "Sukupuoli", "Sukupuoli");
        sukupuoli.addOption("Nainen", "Nainen");
        sukupuoli.addOption("Mies", "Mies");
        sukupuoli.addAttribute("required", "required");

        DropdownSelect asuinmaa = new DropdownSelect("Asuinmaa", "Asuinmaa", "Asuinmaa");
        asuinmaa.addOption("Suomi", "Suomi");
        asuinmaa.addOption("Ruotsi", "Ruotsi");
        asuinmaa.addAttribute("placeholder", "Valitse kansalaisuus");
        asuinmaa.addAttribute("required", "required");
        Element postinumero = createRequiredTextQuestion("Postinumero", "Postinumero");
        postinumero.addAttribute("required", "required");
        postinumero.addAttribute("pattern", "[0-9]{5}");
        postinumero.addAttribute("title", "#####");
        henkilötiedotRyhmä.addChild(createRequiredTextQuestion("Sukunimi", "Sukunimi"))
                .addChild(createRequiredTextQuestion("Etunimet", "Etunimet"))
                .addChild(kutsumanimi)
                .addChild(henkilötunnus)
                .addChild(sukupuoli)
                .addChild(sähköposti)
                .addChild(new TextQuestion("9", "Matkapuhelinnumero", "Matkapuhelinnumero"))
                .addChild(asuinmaa)
                .addChild(createRequiredTextQuestion("Lähiosoite", "Lähiosoite"))
                .addChild(postinumero)
                .addChild(kotikunta)
                .addChild(kansalaisuus)
                .addChild(äidinkieli);

        Radio voimassaoleva = new Radio("voimassaoleva", "Onko sinulla voimassa oleva tutkinnonsuoritusoikeus korkeakouluasteella?", "voimassaoleva");
        voimassaoleva.addOption("Ei", "Ei ole");
        voimassaoleva.addOption("Kyllä", "Kyllä seuraavaan tutkintoon");
        voimassaoleva.addAttribute("required", "required");

        CheckBox checkBox = new CheckBox("tausta", "Merkitse, josta väitä vastaa koulutustaustaasi.", "tausta");
        checkBox.addOption("korkeakoulu_avoin", "Olen suorittanyt korkeakoulun edellyttämät avoimen korkeakoulun opinnot.");
        checkBox.addOption("korkeakoulu_muu", "Minulla on muu korkeakoulu kelpoisuus");
        koulutustaustaRyhmä.addChild(voimassaoleva);
        koulutustaustaRyhmä.addChild(checkBox);

        TextArea textArea = new TextArea("vapaa", "Kerro miksi haet juuri meille", "name");
        koulutustaustaRyhmä.addChild(textArea);

        yhteenvetoRyhmä.addChild(henkilötiedotRyhmä).addChild(koulutustaustaRyhmä);

    }

    @Override
    public FormModel find() {
        return formModel;
    }

    @Override
    public void insert(FormModel formModel) {
        throw new RuntimeException("Insert not implemented");
    }

    @Override
    public void insertModelAsJsonString(StringBuilder builder) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private Element createRequiredTextQuestion(final String id, final String name) {
        TextQuestion textQuestion = new TextQuestion(id, name, name);
        textQuestion.addAttribute("required", "required");
        return textQuestion;
    }

    @Override
    public FormModel getModel() {
        return formModel;
    }

    @Override
    public void delete(FormModel formModel) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Form getActiveForm(String applicationPeriodId, String formId) {
        try {
            return formModel.getApplicationPeriodById(applicationPeriodId).getFormById(formId);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Not found");
        }
    }

    @Override
    public Category getFirstCategory(String applicationPeriodId, String formId) {
        try {
            return this.getActiveForm(applicationPeriodId, formId).getFirstCategory();
        } catch (Exception e) {
            throw new ResourceNotFoundException("Not found");
        }
    }

}