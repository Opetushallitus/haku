package fi.vm.sade.oppija.haku.service.impl;

import fi.vm.sade.oppija.haku.domain.Hakemus;
import fi.vm.sade.oppija.haku.domain.HakemusId;
import fi.vm.sade.oppija.haku.domain.elements.Element;
import fi.vm.sade.oppija.haku.domain.elements.Form;
import fi.vm.sade.oppija.haku.domain.elements.Teema;
import fi.vm.sade.oppija.haku.domain.elements.Vaihe;
import fi.vm.sade.oppija.haku.domain.questions.Question;
import fi.vm.sade.oppija.haku.service.AdditionalQuestionService;
import fi.vm.sade.oppija.haku.service.FormService;
import fi.vm.sade.oppija.haku.service.HakemusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;

/**
 * @author Hannu Lyytikainen
 */
public class AdditionalQuestionServiceImpl implements AdditionalQuestionService {

    @Autowired
    @Qualifier("formServiceImpl")
    FormService formService;

    @Autowired
    @Qualifier("hakemusServiceImpl")
    HakemusService hakemusService;

    @Override
    public List<Question> findAdditionalQuestions(String teemaId, HakemusId hakemusId) {
        Map<String, String> hakemusValues = hakemusService.getHakemus(hakemusId).getValues();
        List<String> hakukohdeList = new ArrayList<String>();

        int prefNumber = 1;

        while (hakemusValues.containsKey("preference" + prefNumber + "-Koulutus-id")) {
            hakukohdeList.add(hakemusValues.get("preference" + prefNumber + "-Koulutus-id"));
            prefNumber++;
        }

        return findAdditionalQuestions(teemaId, hakukohdeList, hakemusId);
    }

    @Override
    public List<Question> findAdditionalQuestions(String teemaId , List<String> hakukohdeIds, HakemusId hakemusId) {
        Teema teema = null;
        Form form = formService.getActiveForm(hakemusId.getApplicationPeriodId(), hakemusId.getFormId());
        Vaihe vaihe = form.getCategory(hakemusId.getCategoryId());
        for (Element e : vaihe.getChildren()) {
            if (e.getId().equals(teemaId))  {
                teema = (Teema) e;
                break;
            }
        }
        if (teema == null) {
            return null;
        }

        List<Question> additionalQuestions = new ArrayList<Question>();

        for (String hakukohdeId : hakukohdeIds) {
            additionalQuestions.addAll(teema.getAdditionalQuestions().get(hakukohdeId));
        }

        return additionalQuestions;
    }
}
