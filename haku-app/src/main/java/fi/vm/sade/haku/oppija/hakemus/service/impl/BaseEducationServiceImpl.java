package fi.vm.sade.haku.oppija.hakemus.service.impl;

import fi.vm.sade.haku.oppija.common.suoritusrekisteri.ArvosanaDTO;
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
                // Jos opiskelija ei ole missään koulussa, ei aseteta lähtökoulua
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
            clearGradesTranferedFlags(application);
            return application;
        }
        Map<String, SuoritusDTO> suoritukset = suoritusrekisteriService.getSuoritukset(personOid);

        if (suoritukset.isEmpty()) {
            clearGradesTranferedFlags(application);
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

        boolean gradesTranferredPk = false;
        boolean gradesTranferredLk = false;

        if (lukioSuoritus != null && isComplete(lukioSuoritus)) {
            pohjakoulutus = OppijaConstants.YLIOPPILAS;
            addGrades(application, lukioSuoritus);
            gradesTranferredLk = true;
            valmistuminen = lukioSuoritus.getValmistuminen();
            suorituskieli = lukioSuoritus.getSuorituskieli();
        } else if (ulkomainenSuoritus != null && isComplete(ulkomainenSuoritus)) {
            pohjakoulutus = OppijaConstants.ULKOMAINEN_TUTKINTO;
            clearGrades(application);
        } else {
            if (kymppiSuoritus != null) {
                addGrades(application, kymppiSuoritus);
                gradesTranferredPk = true;
                if (isComplete(kymppiSuoritus)) {
                    valmistuminen = kymppiSuoritus.getValmistuminen();
                }
                if (peruskouluSuoritus != null) {
                    if (!isComplete(kymppiSuoritus)) {
                        valmistuminen = peruskouluSuoritus.getValmistuminen();
                    }
                    pohjakoulutus = getPohjakoulutus(peruskouluSuoritus);
                    suorituskieli = peruskouluSuoritus.getSuorituskieli();
                    addGrades(application, peruskouluSuoritus);
                } else {
                    LOGGER.error("Missing pk-suoritus with kymppi-suoritus for application: {} of person: {}", application.getOid(), application.getPersonOid());
                    suorituskieli = kymppiSuoritus.getSuorituskieli();
                    pohjakoulutus = getPohjakoulutus(kymppiSuoritus);
                }
            } else if (peruskouluSuoritus != null && isComplete(peruskouluSuoritus)) {
                valmistuminen = peruskouluSuoritus.getValmistuminen();
                suorituskieli = peruskouluSuoritus.getSuorituskieli();
                pohjakoulutus = getPohjakoulutus(peruskouluSuoritus);
                addGrades(application, peruskouluSuoritus);
                gradesTranferredPk = true;
            }
        }

        ammattistarttiSuoritettu = ammattistarttiSuoritettu || ammattistarttiSuoritus != null && isComplete(ammattistarttiSuoritus);
        kuntouttavaSuoritettu = kuntouttavaSuoritettu || kuntouttavaSuoritus != null && isComplete(kuntouttavaSuoritus);
        mamuValmentavaSuoritettu = mamuValmentavaSuoritettu || mamuValmentavaSuoritus != null && isComplete(mamuValmentavaSuoritus);

        final boolean pohjakoulutusSuoritettu = pohjakoulutus != null;

        if (gradesTranferredLk) {
            application.addMeta("grades_transferred_lk", "true");
            application.addMeta("grades_transferred_pk", "false");
        } else if (gradesTranferredPk) {
            application.addMeta("grades_transferred_lk", "false");
            application.addMeta("grades_transferred_pk", "true");
        } else {
            clearGradesTranferedFlags(application);
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

    private void clearGradesTranferedFlags(final Application application) {
        application.addMeta("grades_transferred_lk", "false");
        application.addMeta("grades_transferred_pk", "false");
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

    private void addGrades(Application application, SuoritusDTO suoritus) {
        String suoritusId = suoritus.getId();

        List<ArvosanaDTO> suoritusArvosanat = suoritusrekisteriService.getArvosanat(suoritusId);

        String prefix = getGradePrefix(suoritus);

        Map<String, String> proficiencyPhaseAnswers = new HashMap<String, String>(application.getPhaseAnswers(OppijaConstants.PHASE_GRADES));
        Set<String> receivedGrades = new HashSet<String>();
        // application.addMeta("osaaminen_locked", "true");

        Map<String, Integer> valinnaiset = new HashMap<String, Integer>();

        // Käy läpi rekisteristä tulleet arvosanat ja lisää answers-mappiin
        for (ArvosanaDTO suoritusArvosana : suoritusArvosanat) {
            String suffix = getGradeSuffix(suoritus, valinnaiset, suoritusArvosana);
            String key = prefix + suoritusArvosana.getAine() + suffix;
            if (!receivedGrades.add(key)) {
                throw new IllegalValueException("Doublegrade: " + key + " for person " + application.getPersonOid());
            }
            proficiencyPhaseAnswers = addRegisterValue(application, proficiencyPhaseAnswers, key, suoritusArvosana.getArvosana());
            // Lisätieto == kieli (AI, A1, B1 jne)
            if (isNotBlank(suoritusArvosana.getLisatieto())) {
                proficiencyPhaseAnswers = addRegisterValue(application, proficiencyPhaseAnswers,
                        prefix + suoritusArvosana.getAine() + "_OPPIAINE", suoritusArvosana.getLisatieto());
            }
        }

        // Lisää "Ei arvosanaa" puuttuviin kenttiin
        Map<String, String> toAdd = new HashMap<String, String>();
        boolean isKymppi = suoritus.getKomo().equals(lisaopetusKomoOid);
        for (String key : proficiencyPhaseAnswers.keySet()) {
            if (!key.startsWith(prefix)) {
                continue;
            } else if (isKymppi != key.endsWith("_10")) {
                continue;
            }
            if (!receivedGrades.contains(key) && !key.endsWith("OPPIAINE")) {
                application.addOverriddenAnswer(key, proficiencyPhaseAnswers.get(key));
                toAdd.put(key, "Ei arvosanaa");
            }
            if (suoritus.getKomo().equals(perusopetusKomoOid) && !key.endsWith("OPPIAINE")) {

                if (!key.endsWith("_VAL1") && !key.endsWith("VAL2")) {
                    if (!proficiencyPhaseAnswers.containsKey(key + "_VAL1")) {
                        toAdd.put(key + "_VAL1", "Ei arvosanaa");
                    }
                    if (!proficiencyPhaseAnswers.containsKey(key + "_VAL2")) {
                        toAdd.put(key + "_VAL2", "Ei arvosanaa");
                    }
                }
                String baseKey = key.substring(0, key.length() - "_VAL1".length());
                if (key.endsWith("_VAL1")) {
                    if (!proficiencyPhaseAnswers.containsKey(baseKey)) {
                        toAdd.put(baseKey, "Ei arvosanaa");
                    }
                    if (!proficiencyPhaseAnswers.containsKey(baseKey+"_VAL2")) {
                        toAdd.put(baseKey+"_VAL2", "Ei arvosanaa");
                    }
                }
            }
        }
        proficiencyPhaseAnswers.putAll(toAdd);
        application.addVaiheenVastaukset(OppijaConstants.PHASE_GRADES, proficiencyPhaseAnswers);
    }

    private String getGradePrefix(SuoritusDTO suoritus) {
        if (suoritus.getKomo().equals(lukioKomoOid)) {
            return GradePrefix.LK_.name();
        }
        return GradePrefix.PK_.name();
    }

    private String getGradeSuffix(SuoritusDTO suoritus, Map<String, Integer> valinnaiset, ArvosanaDTO arvosana) {
        String suffix = "";
        if (arvosana.isValinnainen()) {
            if (suoritus.getKomo().equals(lukioKomoOid)) {
                LOGGER.error("Lukio grades can not have optional subjects");
                throw new IllegalValueException("Lukio grades can not have optional subjects");
            }
            String aine = arvosana.getAine();
            Integer count = 1;
            if (valinnaiset.containsKey(aine)) {
                count = valinnaiset.get(aine) + 1;
            }
            if (count > 2) {
                LOGGER.error("Can not have more than two optional subjects");
                throw new IllegalValueException("Can not have more than two optional subjects");
            }
            valinnaiset.put(aine, count);
            suffix = "_VAL" + String.valueOf(count);
        }
        if (suoritus.getKomo().equals(lisaopetusKomoOid)) {
            suffix = suffix + "_10";
        }
        return suffix;
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
