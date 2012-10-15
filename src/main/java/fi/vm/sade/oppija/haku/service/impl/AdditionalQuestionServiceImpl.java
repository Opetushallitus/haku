package fi.vm.sade.oppija.haku.service.impl;

import fi.vm.sade.oppija.haku.domain.Hakemus;
import fi.vm.sade.oppija.haku.domain.HakemusId;
import fi.vm.sade.oppija.haku.domain.elements.Form;
import fi.vm.sade.oppija.haku.domain.elements.custom.SubjectRow;
import fi.vm.sade.oppija.haku.domain.questions.Question;
import fi.vm.sade.oppija.haku.service.AdditionalQuestionService;
import fi.vm.sade.oppija.haku.service.FormService;
import fi.vm.sade.oppija.haku.service.HakemusService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * @author Hannu Lyytikainen
 */
public class AdditionalQuestionServiceImpl implements AdditionalQuestionService {

    @Autowired
    FormService formService;

    @Autowired
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

        Form form = formService.getActiveForm(hakemusId.getApplicationPeriodId(), hakemusId.getFormId());
        form.getCategory(teemaId);


        return null;
    }

    @Override
    public List<Question> findAdditionalQuestions(String teemaId, List<String> hakukohdeIds) {
        return null;
    }
}
