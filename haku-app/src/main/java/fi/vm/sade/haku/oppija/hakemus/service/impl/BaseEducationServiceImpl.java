package fi.vm.sade.haku.oppija.hakemus.service.impl;

import fi.vm.sade.haku.oppija.common.suoritusrekisteri.OpiskelijaDTO;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusrekisteriService;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.service.BaseEducationService;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.exception.ResourceNotFoundException;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.HAKUKAUSI_KEVAT;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.PHASE_EDUCATION;
import static java.util.Calendar.*;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

@Service
public class BaseEducationServiceImpl implements BaseEducationService {

    public static final Logger LOGGER = LoggerFactory.getLogger(BaseEducationServiceImpl.class);

    private final SuoritusrekisteriService suoritusrekisteriService;
    private final ApplicationSystemService applicationSystemService;

    @Autowired
    public BaseEducationServiceImpl(SuoritusrekisteriService suoritusrekisteriService,
                                    ApplicationSystemService applicationSystemService) {
        this.suoritusrekisteriService = suoritusrekisteriService;
        this.applicationSystemService = applicationSystemService;
    }

    private static boolean hasSendingSchoolQuestion(Element elem, Application application) {
        if(OppijaConstants.ELEMENT_ID_SENDING_SCHOOL.equals(elem.getId())) {
            return true;
        }
        for (Element child : elem.getChildren(application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION))) {
            if(hasSendingSchoolQuestion(child, application)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasSendingSchoolQuestionOnForm(ApplicationSystem as, Application application) {
        return hasSendingSchoolQuestion(as.getForm().getChildById(OppijaConstants.PHASE_EDUCATION), application);
    }

    @Override
    public Application addSendingSchool(Application application) {
        String personOid = application.getPersonOid();
        if (isEmpty(personOid)) {
            return application;
        }

        ApplicationSystem as = applicationSystemService.getApplicationSystem(application.getApplicationSystemId());
        if(hasSendingSchoolQuestionOnForm(as, application)) {
            return application;
        }

        List<OpiskelijaDTO> opiskelijatiedot = suoritusrekisteriService.getOpiskelijatiedot(personOid);

        if (!opiskelijatiedot.isEmpty()) {
            Date hakukausiStart = resolveHakukausiStart(as);
            OpiskelijaDTO opiskelija = null;
            boolean found = false;
            for (OpiskelijaDTO dto : opiskelijatiedot) {
                if (dto.getLoppuPaiva() == null || dto.getLoppuPaiva().after(hakukausiStart)) {
                    if (found) {
                        throw new ResourceNotFoundException("Person " + personOid + " in enrolled in multiple schools");
                    }
                    opiskelija = dto;
                    found = true;
                }
            }
            Map<String, String> educationAnswers = new HashMap(application.getPhaseAnswers(PHASE_EDUCATION));

            educationAnswers = handleOpiskelija(educationAnswers, application, opiskelija);
            application.setVaiheenVastauksetAndSetPhaseId(PHASE_EDUCATION, educationAnswers);
        }

        return application;
    }

    private Date resolveHakukausiStart(ApplicationSystem as) {
        Calendar start = GregorianCalendar.getInstance();
        start.set(DAY_OF_MONTH, 1);
        start.set(MONTH, HAKUKAUSI_KEVAT.equals(as.getHakukausiUri()) ? JANUARY : AUGUST);
        start.set(YEAR, as.getHakukausiVuosi());
        return start.getTime();
    }

    private Map<String, String> handleOpiskelija(Map<String, String> answers, Application application, OpiskelijaDTO opiskelija) {
        String sendingSchool = opiskelija != null ? opiskelija.getOppilaitosOid() : null;
        String sendingClass = opiskelija != null ? opiskelija.getLuokka() : null;
        String classLevel = opiskelija != null ? opiskelija.getLuokkataso() : null;

        if (isNotEmpty(sendingClass)) {
            sendingClass = sendingClass.toUpperCase();
        }
        if (isNotEmpty(classLevel)) {
            classLevel = classLevel.toUpperCase();
        }

        answers = addRegisterValue(application, answers, OppijaConstants.ELEMENT_ID_SENDING_SCHOOL, sendingSchool);
        answers = addRegisterValue(application, answers, OppijaConstants.ELEMENT_ID_SENDING_CLASS, sendingClass);
        answers = addRegisterValue(application, answers, OppijaConstants.ELEMENT_ID_CLASS_LEVEL, classLevel);

        return answers;
    }

    private Map<String, String> addRegisterValue(Application application, Map<String, String> answers,
                                                 String key, String value) {
        String oldValue = answers.put(key, value);
        if (value == null) {
            answers.remove(key);
        }
        application.addOverriddenAnswer(key, oldValue);
        LOGGER.debug("Changing value key: {}, value: {} -> {}", key, oldValue, value);
        return answers;
    }

}
