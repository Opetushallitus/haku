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
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author Hannu Lyytikainen
 */
@Service("additionalQuestionService")
public class AdditionalQuestionServiceImpl implements AdditionalQuestionService {

    FormService formService;
    HakemusService hakemusService;

    @Autowired
    public AdditionalQuestionServiceImpl(@Qualifier("formServiceImpl") FormService formService,
                                         @Qualifier("hakemusServiceImpl") HakemusService hakemusService) {
        this.formService = formService;
        this.hakemusService = hakemusService;
    }

    @Override
    public Set<Question> findAdditionalQuestions(String teemaId, HakemusId hakemusId) {
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
    public Set<Question> findAdditionalQuestions(String teemaId , List<String> hakukohdeIds, HakemusId hakemusId) {
        Teema teema = null;
        Form form = formService.getActiveForm(hakemusId.getApplicationPeriodId(), hakemusId.getFormId());
        Vaihe vaihe = form.getCategory(hakemusId.getCategoryId());
        for (Element e : vaihe.getChildren()) {
            if (e.getId().equals(teemaId))  {
                teema = (Teema) e;
                break;
            }
        }

        Set<Question> additionalQuestions = new LinkedHashSet<Question>();

        if (teema == null || teema.getAdditionalQuestions() == null) {
            return additionalQuestions;
        }

        for (String hakukohdeId : hakukohdeIds) {
            List<Question> questions = teema.getAdditionalQuestions().get(hakukohdeId);
            if (questions != null && !questions.isEmpty()) {
                additionalQuestions.addAll(questions);
            }
        }

        return additionalQuestions;
    }

    @Override
    public Map<String, Set<Question>> findAdditionalQuestionsInCategory(HakemusId hakemusId) {
        Form form = formService.getActiveForm(hakemusId.getApplicationPeriodId(), hakemusId.getFormId());
        Vaihe vaihe = form.getCategory(hakemusId.getCategoryId());
        Map<String, Set<Question>> questionMap = new HashMap<String, Set<Question>>();

        for (Element e : vaihe.getChildren()) {
            questionMap.put(e.getId(), findAdditionalQuestions(e.getId(), hakemusId));
        }
        return questionMap;
    }
}
