package fi.vm.sade.oppija.haku.dao.impl;

import fi.vm.sade.oppija.haku.dao.FormModelDAO;
import fi.vm.sade.oppija.haku.domain.ApplicationPeriod;
import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.domain.HakemusId;
import fi.vm.sade.oppija.haku.domain.elements.Category;
import fi.vm.sade.oppija.haku.domain.elements.Element;
import fi.vm.sade.oppija.haku.domain.elements.Form;
import fi.vm.sade.oppija.haku.domain.elements.QuestionGroup;
import fi.vm.sade.oppija.haku.domain.elements.custom.*;
import fi.vm.sade.oppija.haku.domain.exception.ResourceNotFoundException;
import fi.vm.sade.oppija.haku.domain.questions.*;
import fi.vm.sade.oppija.haku.domain.rules.SelectingSubmitRule;
import fi.vm.sade.oppija.haku.service.FormService;
import fi.vm.sade.oppija.haku.validation.Validator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        Category hakutoiveet = new Category("hakutoiveet", "Hakutoiveet");
        Category arvosanat = new Category("arvosanat", "Arvosanat");
        Category lisätiedot = new Category("lisatiedot", "Lisätiedot");
        Category esikatselu = new Category("esikatselu", "Esikatselu");
        Category yhteenveto = new Category("yhteenveto", "yhteenveto");

        Form form = new Form(formId, "yhteishaku");
        form.addChild(henkilötiedot);
        form.addChild(koulutustausta);
        form.addChild(hakutoiveet);
        form.addChild(arvosanat);
        form.addChild(lisätiedot);
        form.addChild(esikatselu);
        form.addChild(yhteenveto);
        form.init();

        applicationPeriod.addForm(form);

        QuestionGroup henkilötiedotRyhmä = new QuestionGroup("HenkilotiedotGrp", "Henkilötiedot");
        QuestionGroup koulutustaustaRyhmä = new QuestionGroup("KoulutustaustaGrp", "Koulutustausta");
        QuestionGroup hakutoiveetRyhmä = new QuestionGroup("hakutoiveetGrp", "Hakutoiveet");
        QuestionGroup arvosanatRyhmä = new QuestionGroup("arvosanatGrp", "Arvosanat");
        QuestionGroup lisätiedotRyhmä = new QuestionGroup("lisatiedotGrp", "Lisätiedot");
        QuestionGroup esikatselutRyhmä = new QuestionGroup("esikatseluGrp", "Esikatselu");
        QuestionGroup yhteenvetoRyhmä = new QuestionGroup("yhteenvetoGrp", "yhteenveto");

        henkilötiedot.addChild(henkilötiedotRyhmä);
        koulutustausta.addChild(koulutustaustaRyhmä);
        hakutoiveet.addChild(hakutoiveetRyhmä);
        arvosanat.addChild(arvosanatRyhmä);
        lisätiedot.addChild(lisätiedotRyhmä);
        esikatselu.addChild(esikatselutRyhmä);
        yhteenveto.addChild(yhteenvetoRyhmä);

        DropdownSelect äidinkieli = new DropdownSelect("äidinkieli", "Äidinkieli");
        äidinkieli.addOption("suomi", "Suomi", "Suomi");
        äidinkieli.addOption("ruotsi", "Ruotsi", "Ruotsi");
        äidinkieli.addAttribute("placeholder", "Valitse Äidinkieli");
        äidinkieli.addAttribute("required", "required");

        DropdownSelect kansalaisuus = new DropdownSelect("kansalaisuus", "Kansalaisuus");
        kansalaisuus.addOption("suomi", "Suomi", "Suomi");
        kansalaisuus.addOption("ruotsi", "Ruotsi", "Ruotsi");
        kansalaisuus.addAttribute("placeholder", "Valitse kansalaisuus");
        kansalaisuus.addAttribute("required", "required");

        DropdownSelect kotikunta = new DropdownSelect("kotikunta", "Kotikunta");
        kotikunta.addOption("jalasjarvi, ", "Jalasjärvi", "Jalasjärvi");
        kotikunta.addOption("janakkala", "Janakkala", "Janakkala");
        kotikunta.addOption("joensuu", "Joensuu", "Joensuu");
        kotikunta.addOption("jokioinen", "Jokioinen", "Jokioinen");
        kotikunta.addOption("jomala", "Jomala", "Jomala");
        kotikunta.addAttribute("placeholder", "Valitse kotikunta");
        kotikunta.addAttribute("required", "required");

        TextQuestion henkilötunnus = new TextQuestion("Henkilotunnus", "Henkilötunnus");
        henkilötunnus.addAttribute("placeholder", "ppkkvv*****");
        henkilötunnus.addAttribute("onChange", "submit()");
        henkilötunnus.addAttribute("title", "ppkkvv*****");
        henkilötunnus.addAttribute("required", "required");
        henkilötunnus.addAttribute("pattern", "[0-9]{6}.[0-9]{4}");
        henkilötunnus.setHelp("Jos sinulla ei ole suomalaista henkilötunnusta, täytä tähän syntymäaikasi");
        TextQuestion kutsumanimi = new TextQuestion("Kutsumanimi", "Kutsumanimi");
        kutsumanimi.setHelp("Valitse kutsumanimeksi jokin virallisista etunimistäsi");
        kutsumanimi.addAttribute("required", "required");
        TextQuestion sähköposti = new TextQuestion("Sähköposti", "Sähköposti");
        sähköposti.setHelp("Kirjoita tähän sähköpostiosoite, johon haluat vastaanottaa opiskelijavalintaan liittyviä tietoja ja jota käytät säännöllisesti. Saat vahvistuksen hakemuksen perille menosta tähän sähköpostiosoitteeseen.");

        Radio sukupuoli = new Radio("Sukupuoli", "Sukupuoli");
        sukupuoli.addOption("mies", "Mies", "Mies");
        sukupuoli.addOption("nainen", "Nainen", "Nainen");
        sukupuoli.addAttribute("required", "required");

        SelectingSubmitRule autofillhetu = new SelectingSubmitRule(henkilötunnus.getId(), sukupuoli.getId());
        autofillhetu.addBinding(henkilötunnus, sukupuoli, "\\d{6}\\S\\d{2}[13579]\\w", sukupuoli.getOptions().get(0));
        autofillhetu.addBinding(henkilötunnus, sukupuoli, "\\d{6}\\S\\d{2}[24680]\\w", sukupuoli.getOptions().get(1));

        DropdownSelect asuinmaa = new DropdownSelect("Asuinmaa", "Asuinmaa");
        asuinmaa.addOption("suomi", "Suomi", "Suomi");
        asuinmaa.addOption("ruotsi", "Ruotsi", "Ruotsi");
        asuinmaa.addAttribute("placeholder", "Valitse kansalaisuus");
        asuinmaa.addAttribute("required", "required");
        Element postinumero = createRequiredTextQuestion("Postinumero", "Postinumero");
        postinumero.addAttribute("required", "required");
        postinumero.addAttribute("pattern", "[0-9]{5}");
        postinumero.addAttribute("title", "#####");
        postinumero.setHelp("Kirjoita tähän osoite, johon haluat vastaanottaan opiskelijavalintaan liittyvää postia, kuten valintakirjeen tai kutsun pääsykokeeseen.");
        TextQuestion matkapuhelinnumero = new TextQuestion("matkapuhelinnumero", "Matkapuhelinnumero");
        matkapuhelinnumero.setHelp("Kirjoita tähän matkapuhelinnumerosi, jotta sinuun saadaan tarvittaessa yhteyden.");
        kotikunta.setHelp("Kotikunta on tyypillisesti se kunta, jossa asut.");
        äidinkieli.setHelp("Jos omaa äidinkieltäsi ei löydy valintalistasta, valitse äidinkieleksesi..");
        henkilötiedotRyhmä.addChild(createRequiredTextQuestion("Sukunimi", "Sukunimi"))
                .addChild(createRequiredTextQuestion("Etunimet", "Etunimet"))
                .addChild(kutsumanimi)
                .addChild(autofillhetu)
                .addChild(sähköposti)
                .addChild(matkapuhelinnumero)
                .addChild(asuinmaa)
                .addChild(createRequiredTextQuestion("Lähiosoite", "Lähiosoite"))
                .addChild(postinumero)
                .addChild(kotikunta)
                .addChild(kansalaisuus)
                .addChild(äidinkieli);

        createKoulutustausta(koulutustaustaRyhmä);
        createHakutoiveet(hakutoiveetRyhmä);
        createArvosanat(arvosanatRyhmä);

        TextArea lisätiedotText = new TextArea("vapaa", "Lisätietoja, sana on vapaa");
        lisätiedotRyhmä.addChild(lisätiedotText);

        esikatselutRyhmä.addChild(henkilötiedotRyhmä).
                addChild(koulutustaustaRyhmä).addChild(hakutoiveetRyhmä).addChild(arvosanatRyhmä).addChild(lisätiedotRyhmä);

        yhteenvetoRyhmä.setHelp("Kiitos, hakemuksesi on vastaanotettu");

    }

    private void createArvosanat(QuestionGroup arvosanatRyhmä) {

        List<Integer> gradeRange = new ArrayList<Integer>();
        gradeRange.add(10);
        gradeRange.add(9);
        gradeRange.add(8);
        gradeRange.add(7);
        gradeRange.add(6);
        gradeRange.add(5);
        gradeRange.add(4);

        SubjectRow finnish = new SubjectRow("subject_finnish", "Äidinkieli");
        List<SubjectRow> subjectRowsBefore = new ArrayList<SubjectRow>();
        subjectRowsBefore.add(finnish);

        LanguageRow a1 = new LanguageRow("lang_a1", "A1-kieli");
        LanguageRow b1 = new LanguageRow("lang_b1", "B1-kieli");

        ArrayList<LanguageRow> languageRows = new ArrayList<LanguageRow>();
        languageRows.add(a1);
        languageRows.add(b1);

        SubjectRow math = new SubjectRow("subject_math", "Matematiikka");
        SubjectRow biology = new SubjectRow("subject_biology", "Biologia");
        ArrayList<SubjectRow> subjectRowsAfter = new ArrayList<SubjectRow>();
        subjectRowsAfter.add(math);
        subjectRowsAfter.add(biology);

        List<Option> languageOptions = new ArrayList<Option>();
        languageOptions.add(new Option("langoption_" + "eng", "eng", "englanti"));
        languageOptions.add(new Option("langoption_" + "swe", "swe", "ruotsi"));
        languageOptions.add(new Option("langoption_" + "fra", "fra", "ranska"));
        languageOptions.add(new Option("langoption_" + "ger", "ger", "saksa"));
        languageOptions.add(new Option("langoption_" + "rus", "rus", "venäjä"));
        languageOptions.add(new Option("langoption_" + "fin", "fin", "suomi"));

        List<Option> scopeOptions = new ArrayList<Option>();
        scopeOptions.add(new Option("scopeoption_" + "a1", "a1", "A1"));
        scopeOptions.add(new Option("scopeoption_" + "a2", "a2", "A2"));
        scopeOptions.add(new Option("scopeoption_" + "b1", "b1", "B1"));
        scopeOptions.add(new Option("scopeoption_" + "b2", "b2", "B2"));
        scopeOptions.add(new Option("scopeoption_" + "b3", "b3", "B3"));

        GradeGrid gradeGrid = new GradeGrid("gradegrid", "Arvosanat", "Arvosanat TOR-rekisterissä",
                "Poikkeavat Arvosanat", "Arvosanat", "Oppiaine", "Yhteinen oppiaine", "Valinnaisaine",
                "Kieli", "Lisää kieli", subjectRowsBefore, languageRows,
                subjectRowsAfter, scopeOptions, languageOptions, gradeRange);

        arvosanatRyhmä.addChild(gradeGrid);

    }

    private void createHakutoiveet(QuestionGroup hakutoiveetRyhmä) {
        hakutoiveetRyhmä.setHelp("Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem.");
        SortableTable sortableTable = new SortableTable("preferencelist", "Hakutoiveet", "Ylös", "Alas");
        PreferenceRow pr1 = new PreferenceRow("preference1", "Hakutoive 1", "Tyhjennä", "Koulutus", "Opetuspiste", "Valitse koulutus");
        PreferenceRow pr2 = new PreferenceRow("preference2", "Hakutoive 2", "Tyhjennä", "Koulutus", "Opetuspiste", "Valitse koulutus");
        PreferenceRow pr3 = new PreferenceRow("preference3", "Hakutoive 3", "Tyhjennä", "Koulutus", "Opetuspiste", "Valitse koulutus");
        sortableTable.addChild(pr1);
        sortableTable.addChild(pr2);
        sortableTable.addChild(pr3);
        addEducationsToPreferenceRow(pr1);
        addEducationsToPreferenceRow(pr2);
        addEducationsToPreferenceRow(pr3);
        hakutoiveetRyhmä.addChild(sortableTable);
    }

    private void addEducationsToPreferenceRow(PreferenceRow preferenceRow) {
        preferenceRow.addOption("1", "autoala", "Autoala");
        preferenceRow.addOption("2", "ruotsi", "Ruotsi");
    }

    private void createKoulutustausta(QuestionGroup koulutustaustaRyhmä) {
        Radio millatutkinnolla = new Radio("millatutkinnolla", "Millä tutkinnolla haet opiskelupaikkaa?");
        for (int i = 0; i < 10; i++) {
            millatutkinnolla.addOption("tutkinto" + i, "tutkinto" + i, "Lorem ipsum sit dolor amet " + i);
        }

        Radio peruskoulu2012 = new Radio("peruskoulu2012", "Saatko peruskoulun päättötodistuksen hakukeväänä 2012?");
        peruskoulu2012.addOption("kylla", "Kyllä", "Kyllä");
        peruskoulu2012.addOption("ei", "Ei", "en, lorem ipsum sed diam bla bla bla nonummy nihb euismod");
        peruskoulu2012.addAttribute("required", "required");

        DropdownSelect tutkinnonOpetuskieli = new DropdownSelect("opetuskieli", "Mikä oli tukintosi opetuskieli");
        tutkinnonOpetuskieli.addOption("suomi", "Suomi", "Suomi");
        tutkinnonOpetuskieli.addOption("ruotsi", "Ruotsi", "Ruotsi");
        tutkinnonOpetuskieli.addAttribute("placeholder", "Tutkintosi opetuskieli");
        tutkinnonOpetuskieli.setHelp("Merkitse tähän lorem ipsum sit ame bla bla lorem ipsum sit ame bla bla lorem ipsum sit ame bla bla lorem ipsum sit ame bla bla");

        CheckBox suorittanut = new CheckBox("suorittanut", "Merkitse tähän, jos olet suorittanut jonkun seuraavista");
        for (int i = 0; i < 5; i++) {
            suorittanut.addOption("suorittanut" + i, "suorittanut " + i, "Olen suorittanut opetuksen " + i + ".");
        }

        Radio jotain = new Radio("jotain", "Lorem ipsum sit dolor amet?");
        jotain.addOption("ei", "Ei", "En");
        jotain.addOption("kylla", "Kyllä", "Kyllä");
        jotain.addAttribute("required", "required");

        koulutustaustaRyhmä.addChild(millatutkinnolla);
        koulutustaustaRyhmä.addChild(peruskoulu2012);
        koulutustaustaRyhmä.addChild(suorittanut);
        koulutustaustaRyhmä.addChild(jotain);
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
    public void insertModelAsJsonString(String builder) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private Element createRequiredTextQuestion(final String id, final String name) {
        TextQuestion textQuestion = new TextQuestion(id, name);
        textQuestion.addAttribute("required", "required");
        return textQuestion;
    }


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

    @Override
    public Map<String, ApplicationPeriod> getApplicationPerioidMap() {
        return getModel().getApplicationPerioidMap();
    }

    @Override
    public ApplicationPeriod getApplicationPeriodById(final String applicationPeriodId) {
        return getModel().getApplicationPeriodById(applicationPeriodId);
    }

    @Override
    public Map<String, Validator> getCategoryValidators(HakemusId hakemusId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


}
