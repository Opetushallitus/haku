package fi.vm.sade.oppija.haku.dao.impl;

import fi.vm.sade.oppija.haku.dao.FormModelDAO;
import fi.vm.sade.oppija.haku.domain.*;
import fi.vm.sade.oppija.haku.domain.questions.TextQuestion;
import org.springframework.stereotype.Service;

@Service("FormModelDummyMemoryDao")
public class FormModelDummyMemoryDaoImpl implements FormModelDAO {

    final ApplicationPeriod applicationPeriod;
    private FormModel formModel;

    public FormModelDummyMemoryDaoImpl() {
        this.applicationPeriod = new ApplicationPeriod("test");
        formModel = new FormModel();
        formModel.addApplicationPeriod(applicationPeriod);
        Category category1 = new Category("222", "Henkilötiedot");
        Category category2 = new Category("223", "Koulutustausta");
        Category category3 = new Category("224", "Hakutoiveet");
        Category category4 = new Category("225", "Osaaminen");
        Form form = new Form("yhteishaku", "yhteishaku");
        form.addChild(category1);
        form.addChild(category2);
        form.addChild(category3);
        form.addChild(category4);
        form.produceCategoryMap();

        applicationPeriod.addForm(form);

        QuestionGroup questionGroup = new QuestionGroup("1", "Henkilötiedot");
        category1.addChild(questionGroup);
        category2.addChild(questionGroup);
        category3.addChild(questionGroup);
        category4.addChild(questionGroup);

        TextQuestion äidinkieli = new TextQuestion("15", "Äidinkieli", "Äidinkieli");
        äidinkieli.setHelp("Minkä värinen on äitisi kieli?");
        questionGroup.addChild(new TextQuestion("2", "Sukunimi", "Sukunimi"))
                .addChild(createRequiredTextQuestion("3", "Etunimi"))
                .addChild(new TextQuestion("4", "Kutsumanimi", "Kutsumanimi"))
                .addChild(new TextQuestion("5", "Henkilötunnus", "Henkilötunnus"))
                .addChild(new TextQuestion("6", "Sukupuoli", "Sukupuoli"))
                .addChild(new TextQuestion("8", "Sähköposti", "Sähköposti"))
                .addChild(new TextQuestion("9", "Matkapuhelinnumero", "Matkapuhelinnumero"))
                .addChild(new TextQuestion("10", "Asuinmaa", "Asuinmaa"))
                .addChild(new TextQuestion("11", "Lähiosoite", "Lähiosoite"))
                .addChild(new TextQuestion("12", "Postinumero", "Postinumero"))
                .addChild(new TextQuestion("13", "Kotikunta", "Kotikunta"))
                .addChild(new TextQuestion("14", "Kansalaisuus", "Kansalaisuus"))
                .addChild(äidinkieli);
    }

    @Override
    public FormModel find() {
        return formModel;
    }

    @Override
    public void insert(FormModel formModel) {
        throw new RuntimeException("Insert not implemented");
    }

    private Element createRequiredTextQuestion(final String id, final String name) {
        TextQuestion textQuestion = new TextQuestion(id, name, name);
        textQuestion.addAttribute("required", "required");
        return textQuestion;
    }
}