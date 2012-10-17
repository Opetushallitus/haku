package fi.vm.sade.oppija.haku.event;

import fi.vm.sade.oppija.haku.domain.Hakemus;
import fi.vm.sade.oppija.haku.domain.HakemusId;
import fi.vm.sade.oppija.haku.domain.elements.Vaihe;
import fi.vm.sade.oppija.haku.domain.elements.custom.SubjectRow;
import fi.vm.sade.oppija.haku.service.AdditionalQuestionService;
import fi.vm.sade.oppija.haku.service.FormService;
import fi.vm.sade.oppija.haku.service.HakukohdeService;
import fi.vm.sade.oppija.haku.validation.HakemusState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author Hannu Lyytikainen
 */
@Service
public class HakukohdeSelectedEvent extends AbstractEvent {

    private AdditionalQuestionService additionalQuestionService;

    @Autowired
    public HakukohdeSelectedEvent(EventHandler handler,
                                  @Qualifier("additionalQuestionService") AdditionalQuestionService additionalQuestionService) {
        handler.addPostValidateEvent(this);
        this.additionalQuestionService = additionalQuestionService;
    }

    @Override
    public void process(HakemusState hakemusState) {

        // check that the phase is changing from
        // hakutoiveet => arvosanat

        Vaihe vaihe = (Vaihe)hakemusState.getModelObjects().get("category");



        HakemusId hakemusId = ((Hakemus)hakemusState.getModelObjects().get("hakemus")).getHakemusId();






//
//
//
//        if ("hakutoiveet".equals(vaihe.getPrev().getId()) &&
//                "arvosanat".equals(vaihe.getId())) {
//
//            Hakemus hakemus = (Hakemus)hakemusState.getModelObjects().get("hakemus");
//            Map<String, String> hakemusValues = hakemus.getValues();
//            //preference1-Koulutus-id
//
//            int prefNumber = 1;
//            Set<SubjectRow> subjects = new HashSet<SubjectRow>();
//            while (hakemusValues.containsKey("preference" + prefNumber + "-Koulutus-id")) {
//                String hakukohdeId = hakemusValues.get("preference" + prefNumber + "-Koulutus-id");
//
//                subjects.addAll(this.hakukohdeService.getHakukohdeSpecificSubjects(hakukohdeId, null));
//
//                prefNumber++;
//            }
//            Form form = formService.getActiveForm("test", "yhteishaku");
//            for (Element child : form.getCategory("arvosanat").getChildren()) {
//                if (child.getType().equals("QuestionGroup")) {
//                    for (Element innerChild : child.getChildren()) {
//                        if (innerChild.getType().equals("GradeGrid")) {
//                            GradeGrid gradeGrid = (GradeGrid)innerChild;
//                            gradeGrid.setEducationSpecificSubjects(subjects);
//                        }
//                    }
//                }
//            }






    }
}
