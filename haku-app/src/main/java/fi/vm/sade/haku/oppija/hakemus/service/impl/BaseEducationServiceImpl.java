package fi.vm.sade.haku.oppija.hakemus.service.impl;

import fi.vm.sade.haku.oppija.common.suoritusrekisteri.OpiskelijaDTO;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusDTO;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusrekisteriService;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.service.BaseEducationService;
import fi.vm.sade.haku.oppija.lomake.exception.IllegalValueException;
import fi.vm.sade.haku.oppija.lomake.exception.ResourceNotFoundException;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.apache.commons.lang.StringUtils.*;

@Service
public class BaseEducationServiceImpl implements BaseEducationService {

    public static final Logger LOGGER = LoggerFactory.getLogger(BaseEducationServiceImpl.class);

    private enum GradePrefix {PK_, LK_};

    @Value("${komo.oid.perusopetus}")
    private String perusopetusKomoOid;
    @Value("${komo.oid.lukio}")
    private String lukioKomoOid;
    @Value("${komo.oid.lisaopetus}")
    private String lisaopetusKomoOid;
    @Value("${komo.oid.ulkomainen}")
    private String ulkomainenKomoOid;
    @Value("${komo.oid.valmistava}")
    private String valmistavaKomoOid;
    @Value("${komo.oid.mamuValmistava}")
    private String mamuValmistavaKomoOid;
    @Value("${komo.oid.kuntouttava}")
    private String kuntouttavaKomoOid;
    @Value("${komo.oid.ylioppilastutkinto")
    private String ylioppilastutkintoKomoOid;

    private final SuoritusrekisteriService suoritusrekisteriService;

    @Autowired
    public BaseEducationServiceImpl(SuoritusrekisteriService suoritusrekisteriService) {
        this.suoritusrekisteriService = suoritusrekisteriService;
    }


    @Override
    public Application addSendingSchool(Application application) {
        String personOid = application.getPersonOid();
        if (isEmpty(personOid)) {
            return application;
        }

        List<OpiskelijaDTO> opiskelijat = suoritusrekisteriService.getOpiskelijat(personOid);

        if (opiskelijat != null && opiskelijat.size() >= 1) {
            OpiskelijaDTO opiskelija = null;
            boolean found = false;
            for (OpiskelijaDTO dto : opiskelijat) {
                if (dto.getLoppuPaiva() == null || dto.getLoppuPaiva().after(new Date())) {
                    if (found) {
                        throw new ResourceNotFoundException("Person " + personOid + " in enrolled in multiple schools");
                    }
                    opiskelija = dto;
                    found = true;
                }
            }
            if (opiskelija == null) {
                // Jos hakija ei ole missään koulussa, ei aseteta lähtökoulua
                return application;
            }

            Map<String, String> educationAnswers = new HashMap<String, String>(
                    application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION));

            educationAnswers = handleOpiskelija(educationAnswers, application, opiskelija);
            application.addVaiheenVastaukset(OppijaConstants.PHASE_EDUCATION, educationAnswers);
        }

        return application;
    }

    private Map<String, String> handleOpiskelija(Map<String, String> answers, Application application, OpiskelijaDTO opiskelija) {
        String sendingSchool = opiskelija.getOppilaitosOid();
        String sendingClass = opiskelija.getLuokka();
        String classLevel = opiskelija.getLuokkataso();
        if (isNotEmpty(sendingSchool)) {
            answers = addRegisterValue(application, answers, OppijaConstants.ELEMENT_ID_SENDING_SCHOOL, sendingSchool);
        }
        if (isNotEmpty(sendingClass)) {
            sendingClass = sendingClass.toUpperCase();
            answers = addRegisterValue(application, answers, OppijaConstants.ELEMENT_ID_SENDING_CLASS, sendingClass);
        }
        if (isNotEmpty(classLevel)) {
            classLevel = classLevel.toUpperCase();
            answers = addRegisterValue(application, answers, OppijaConstants.ELEMENT_ID_CLASS_LEVEL, classLevel);
        }
        return answers;
    }

    @Override
    public Application addBaseEducation(Application application) {
        String personOid = application.getPersonOid();
        if (isEmpty(personOid)) {
            return application;
        }
        Map<String, SuoritusDTO> suoritukset = suoritusrekisteriService.getSuoritukset(personOid);

        if (suoritukset.isEmpty()) {
            return application;
        }

        String pohjakoulutus = null;
        Date valmistuminen = null;
        String suorituskieli = null;

        SuoritusDTO lukioSuoritus = suoritukset.get(lukioKomoOid);
        SuoritusDTO ulkomainenSuoritus = suoritukset.get(ulkomainenKomoOid);
        SuoritusDTO kymppiSuoritus = suoritukset.get(lisaopetusKomoOid);
        SuoritusDTO ammattistarttiSuoritus = suoritukset.get(valmistavaKomoOid);
        SuoritusDTO kuntouttavaSuoritus = suoritukset.get(kuntouttavaKomoOid);
        SuoritusDTO mamuValmentavaSuoritus = suoritukset.get(mamuValmistavaKomoOid);
        SuoritusDTO peruskouluSuoritus = suoritukset.get(perusopetusKomoOid);

        Map<String, String> educationAnswers = new HashMap<String, String>(application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION));

        String ammattistarttiSuoritettuStr = educationAnswers.get(OppijaConstants.ELEMENT_ID_LISAKOULUTUS_AMMATTISTARTTI);
        String kuntouttavaSuoritettuStr = educationAnswers.get(OppijaConstants.ELEMENT_ID_LISAKOULUTUS_VAMMAISTEN);
        String mamuValmentavaSuoritettuStr = educationAnswers.get(OppijaConstants.ELEMENT_ID_LISAKOULUTUS_MAAHANMUUTTO);

        boolean ammattistarttiSuoritettu = isNotBlank(ammattistarttiSuoritettuStr) ? Boolean.valueOf(ammattistarttiSuoritettuStr) : false;
        boolean kuntouttavaSuoritettu = isNotBlank(kuntouttavaSuoritettuStr) ? Boolean.valueOf(kuntouttavaSuoritettuStr) : false;
        boolean mamuValmentavaSuoritettu = isNotBlank(mamuValmentavaSuoritettuStr) ? Boolean.valueOf(mamuValmentavaSuoritettuStr) : false;

        boolean gradesTransferredPk = false;
        boolean gradesTransferredLk = false;

        if (lukioSuoritus != null && isComplete(lukioSuoritus)) {
            pohjakoulutus = OppijaConstants.YLIOPPILAS;
            valmistuminen = lukioSuoritus.getValmistuminen();
            suorituskieli = lukioSuoritus.getSuorituskieli();
            gradesTransferredLk = true;
        } else if (ulkomainenSuoritus != null && isComplete(ulkomainenSuoritus)) {
            pohjakoulutus = OppijaConstants.ULKOMAINEN_TUTKINTO;
            clearGrades(application);
        } else {
            if (kymppiSuoritus != null) {
                gradesTransferredPk = true;
                if (isComplete(kymppiSuoritus)) {
                    valmistuminen = kymppiSuoritus.getValmistuminen();
                }
                if (peruskouluSuoritus != null) {
                    if (!isComplete(kymppiSuoritus)) {
                        valmistuminen = peruskouluSuoritus.getValmistuminen();
                    }
                    pohjakoulutus = getPohjakoulutus(peruskouluSuoritus);
                    suorituskieli = peruskouluSuoritus.getSuorituskieli();
                } else {
                    LOGGER.error("Missing pk-suoritus with kymppi-suoritus for application: {} of person: {}", application.getOid(), application.getPersonOid());
                    suorituskieli = kymppiSuoritus.getSuorituskieli();
                    pohjakoulutus = getPohjakoulutus(kymppiSuoritus);
                }
            } else if (peruskouluSuoritus != null && isComplete(peruskouluSuoritus)) {
                gradesTransferredPk = true;
                valmistuminen = peruskouluSuoritus.getValmistuminen();
                suorituskieli = peruskouluSuoritus.getSuorituskieli();
                pohjakoulutus = getPohjakoulutus(peruskouluSuoritus);
            }
        }

        ammattistarttiSuoritettu = ammattistarttiSuoritettu || ammattistarttiSuoritus != null && isComplete(ammattistarttiSuoritus);
        kuntouttavaSuoritettu = kuntouttavaSuoritettu || kuntouttavaSuoritus != null && isComplete(kuntouttavaSuoritus);
        mamuValmentavaSuoritettu = mamuValmentavaSuoritettu || mamuValmentavaSuoritus != null && isComplete(mamuValmentavaSuoritus);

        final boolean pohjakoulutusSuoritettu = pohjakoulutus != null;

        if (gradesTransferredLk) {
            application.addMeta("grades_transferred_lk", "true");
            application.addMeta("grades_transferred_pk", "false");
        } else if (gradesTransferredPk) {
            application.addMeta("grades_transferred_lk", "false");
            application.addMeta("grades_transferred_pk", "true");
        } else {
            application.addMeta("grades_transferred_lk", "false");
            application.addMeta("grades_transferred_pk", "false");
        }
        
        if (!(ammattistarttiSuoritettu || kuntouttavaSuoritettu || mamuValmentavaSuoritettu || pohjakoulutusSuoritettu)) {
            return application;
        }

        if (pohjakoulutusSuoritettu) {
            educationAnswers = addRegisterValue(application, educationAnswers,
                    OppijaConstants.ELEMENT_ID_BASE_EDUCATION, String.valueOf(pohjakoulutus));
        }

        educationAnswers = addRegisterValue(application, educationAnswers,
                OppijaConstants.ELEMENT_ID_LISAKOULUTUS_AMMATTISTARTTI, String.valueOf(ammattistarttiSuoritettu));
        educationAnswers = addRegisterValue(application, educationAnswers,
                OppijaConstants.ELEMENT_ID_LISAKOULUTUS_VAMMAISTEN, String.valueOf(kuntouttavaSuoritettu));
        educationAnswers = addRegisterValue(application, educationAnswers,
                OppijaConstants.ELEMENT_ID_LISAKOULUTUS_MAAHANMUUTTO, String.valueOf(mamuValmentavaSuoritettu));

        String todistusvuosiKey = OppijaConstants.YLIOPPILAS.equals(pohjakoulutus)
                ? OppijaConstants.LUKIO_PAATTOTODISTUS_VUOSI
                : OppijaConstants.PERUSOPETUS_PAATTOTODISTUSVUOSI;
        if (valmistuminen != null) {
            Calendar cal = GregorianCalendar.getInstance();
            cal.setTime(valmistuminen);
            String todistusvuosi = String.valueOf(cal.get(Calendar.YEAR));
            educationAnswers = addRegisterValue(application, educationAnswers, todistusvuosiKey, todistusvuosi);
        }
        String suorituskieliKey = OppijaConstants.YLIOPPILAS.equals(pohjakoulutus)
                ? OppijaConstants.LUKIO_KIELI
                : OppijaConstants.PERUSOPETUS_KIELI;
        if (suorituskieli != null) {
            educationAnswers = addRegisterValue(application, educationAnswers, suorituskieliKey, suorituskieli);
        }

        application.addVaiheenVastaukset(OppijaConstants.PHASE_EDUCATION, educationAnswers);

        return application;
    }

    private void clearGrades(final Application application) {
        LOGGER.info("Clearing grades for application {}", application.getOid());
        Map<String, String> originalGradeAnswers = application.getPhaseAnswers(OppijaConstants.PHASE_GRADES);
        Map<String, String> gradeAnswers = new HashMap<String, String>(originalGradeAnswers);
        for (String key : originalGradeAnswers.keySet()) {
            for (GradePrefix prefix : GradePrefix.values()) {
                if (!key.startsWith(prefix.name())) {
                    continue;
                }
                String value = gradeAnswers.remove(key);
                application.addOverriddenAnswer(key, value);
                LOGGER.debug("Removed grade key: {}, value {} from application: {}", key, value, application.getOid());
            }
        }
        application.addVaiheenVastaukset(OppijaConstants.PHASE_GRADES, gradeAnswers);
    }

    private String getPohjakoulutus(final SuoritusDTO suoritus) {
        final String yksilollistaminen = suoritus.getYksilollistaminen();
        if ("Ei".equals(yksilollistaminen)) {
            return OppijaConstants.PERUSKOULU;
        } else if ("Alueittain".equals(yksilollistaminen)) {
            return OppijaConstants.ALUEITTAIN_YKSILOLLISTETTY;
        } else if ("Kokonaan".equals(yksilollistaminen)) {
            return OppijaConstants.YKSILOLLISTETTY;
        } else if ("Osittain".equals(yksilollistaminen)) {
            return OppijaConstants.OSITTAIN_YKSILOLLISTETTY;
        } else {
            throw new IllegalValueException("Illegal value for yksilollistaminen: " + yksilollistaminen);
        }
    }

    private boolean isComplete(SuoritusDTO suoritus) {
        return !"KESKEYTYNYT".equals(suoritus.getTila());
    }


    private Map<String, String> addRegisterValue(Application application, Map<String, String> answers,
                                                 String key, String value) {
        String oldValue = answers.put(key, value);
        application.addOverriddenAnswer(key, oldValue);
        LOGGER.debug("Changing value key: {}, value: {} -> {}", key, oldValue, value);
        return answers;
    }
}
