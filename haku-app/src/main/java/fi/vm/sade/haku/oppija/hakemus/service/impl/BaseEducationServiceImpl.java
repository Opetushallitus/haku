package fi.vm.sade.haku.oppija.hakemus.service.impl;

import fi.vm.sade.haku.oppija.common.suoritusrekisteri.ArvosanaDTO;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.OpiskelijaDTO;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusDTO;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusrekisteriService;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.service.BaseEducationService;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.exception.IllegalValueException;
import fi.vm.sade.haku.oppija.lomake.exception.ResourceNotFoundException;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusrekisteriService.*;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.*;
import static org.apache.commons.lang.StringUtils.*;

@Service
public class BaseEducationServiceImpl implements BaseEducationService {

    public static final Logger LOGGER = LoggerFactory.getLogger(BaseEducationServiceImpl.class);

    private enum GradePrefix {PK_, LK_};

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

            Map<String, String> educationAnswers = new HashMap<>(
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

        SuoritusDTO lukioSuoritus = suoritukset.get(LUKIO_KOMO);
        SuoritusDTO ulkomainenSuoritus = suoritukset.get(ULKOMAINEN_KOMO);
        SuoritusDTO kymppiSuoritus = suoritukset.get(LISAOPETUS_KOMO);
        SuoritusDTO ammattistarttiSuoritus = suoritukset.get(AMMATTISTARTTI_KOMO);
        SuoritusDTO kuntouttavaSuoritus = suoritukset.get(KUNTOUTTAVA_KOMO);
        SuoritusDTO mamuValmentavaSuoritus = suoritukset.get(MAMU_VALMENTAVA_KOMO);
        SuoritusDTO peruskouluSuoritus = suoritukset.get(PERUSOPETUS_KOMO);

        Map<String, String> educationAnswers = new HashMap<>(application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION));

        String ammattistarttiSuoritettuStr = educationAnswers.get(OppijaConstants.ELEMENT_ID_LISAKOULUTUS_AMMATTISTARTTI);
        String kuntouttavaSuoritettuStr = educationAnswers.get(OppijaConstants.ELEMENT_ID_LISAKOULUTUS_VAMMAISTEN);
        String mamuValmentavaSuoritettuStr = educationAnswers.get(OppijaConstants.ELEMENT_ID_LISAKOULUTUS_MAAHANMUUTTO);

        boolean ammattistarttiSuoritettu = isNotBlank(ammattistarttiSuoritettuStr) ? Boolean.valueOf(ammattistarttiSuoritettuStr) : false;
        boolean kuntouttavaSuoritettu = isNotBlank(kuntouttavaSuoritettuStr) ? Boolean.valueOf(kuntouttavaSuoritettuStr) : false;
        boolean mamuValmentavaSuoritettu = isNotBlank(mamuValmentavaSuoritettuStr) ? Boolean.valueOf(mamuValmentavaSuoritettuStr) : false;

        if (lukioSuoritus != null && isComplete(lukioSuoritus)) {
            pohjakoulutus = YLIOPPILAS;
            valmistuminen = lukioSuoritus.getValmistuminen();
            suorituskieli = lukioSuoritus.getSuorituskieli();
        } else if (ulkomainenSuoritus != null && isComplete(ulkomainenSuoritus)) {
            pohjakoulutus = ULKOMAINEN_TUTKINTO;
            clearGrades(application);
        } else {
            if (kymppiSuoritus != null) {
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
                valmistuminen = peruskouluSuoritus.getValmistuminen();
                suorituskieli = peruskouluSuoritus.getSuorituskieli();
                pohjakoulutus = getPohjakoulutus(peruskouluSuoritus);
            }
        }

        ammattistarttiSuoritettu = ammattistarttiSuoritettu || ammattistarttiSuoritus != null && isComplete(ammattistarttiSuoritus);
        kuntouttavaSuoritettu = kuntouttavaSuoritettu || kuntouttavaSuoritus != null && isComplete(kuntouttavaSuoritus);
        mamuValmentavaSuoritettu = mamuValmentavaSuoritettu || mamuValmentavaSuoritus != null && isComplete(mamuValmentavaSuoritus);

        final boolean pohjakoulutusSuoritettu = pohjakoulutus != null;

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

        String todistusvuosiKey = YLIOPPILAS.equals(pohjakoulutus)
                ? OppijaConstants.LUKIO_PAATTOTODISTUS_VUOSI
                : OppijaConstants.PERUSOPETUS_PAATTOTODISTUSVUOSI;
        if (valmistuminen != null) {
            Calendar cal = GregorianCalendar.getInstance();
            cal.setTime(valmistuminen);
            String todistusvuosi = String.valueOf(cal.get(Calendar.YEAR));
            educationAnswers = addRegisterValue(application, educationAnswers, todistusvuosiKey, todistusvuosi);
        }
        String suorituskieliKey = YLIOPPILAS.equals(pohjakoulutus)
                ? OppijaConstants.LUKIO_KIELI
                : OppijaConstants.PERUSOPETUS_KIELI;
        if (suorituskieli != null) {
            educationAnswers = addRegisterValue(application, educationAnswers, suorituskieliKey, suorituskieli);
        }

        application.addVaiheenVastaukset(OppijaConstants.PHASE_EDUCATION, educationAnswers);

        return application;
    }

    @Override
    public Map<String, String> getArvosanat(String personOid, String baseEducation, ApplicationSystem as) {
        Map<String, String> arvosanaMap = new HashMap<>();
        if (ULKOMAINEN_TUTKINTO.equals(baseEducation) || KESKEYTYNYT.equals(baseEducation) || isEmpty(baseEducation)) {
            return arvosanaMap;
        }
        if (OppijaConstants.KOHDEJOUKKO_KORKEAKOULU.equals(as.getKohdejoukkoUri())) {
            return arvosanaMap;
        }

        Date endDate = new Date();
        for (ApplicationPeriod ap : as.getApplicationPeriods()) {
            endDate = ap.getEnd().after(endDate) ? ap.getEnd() : endDate;
        }

        if (YLIOPPILAS.equals(baseEducation)) {
            Map<String, SuoritusDTO> suoritukset = suoritusrekisteriService.getSuoritukset(personOid);
            if (suoritukset.isEmpty() || suoritukset.get(LUKIO_KOMO) == null) {
                return arvosanaMap;
            }
            arvosanaMap.putAll(suorituksenArvosanat("LK_", suoritukset.get(LUKIO_KOMO).getId()));
        } else if (PERUSKOULU.equals(baseEducation) || YKSILOLLISTETTY.equals(baseEducation)
                || ALUEITTAIN_YKSILOLLISTETTY.equals(baseEducation) || OSITTAIN_YKSILOLLISTETTY.equals(baseEducation)) {
            Map<String, SuoritusDTO> suoritukset = suoritusrekisteriService.getSuoritukset(personOid);
            if (suoritukset.isEmpty() || suoritukset.get(PERUSOPETUS_KOMO) == null) {
                return arvosanaMap;
            }
            List<SuoritusDTO> suoritusList = new ArrayList<>(suoritukset.values());
            Collections.sort(suoritusList, new Comparator<SuoritusDTO>() {
                @Override
                public int compare(SuoritusDTO o1, SuoritusDTO o2) {
                    return o1.getValmistuminen().compareTo(o2.getValmistuminen());
                }
            });
            for (SuoritusDTO suoritus : suoritusList) {
                if ((PERUSOPETUS_KOMO.equals(suoritus.getKomo()) ||
                        LISAOPETUS_KOMO.equals(suoritus.getKomo()) ||
                        AMMATTISTARTTI_KOMO.equals(suoritus.getKomo()) ||
                        KUNTOUTTAVA_KOMO.equals(suoritus.getKomo()) ||
                        MAMU_VALMENTAVA_KOMO.equals(suoritus.getKomo()))) {

                    arvosanaMap.putAll(suorituksenArvosanat("PK_", suoritus.getId()));
                }
            }
        }
        Map<String, String> toAdd = new HashMap<>();
        for (Map.Entry<String, String> entry : arvosanaMap.entrySet()) {
            String prefix = entry.getKey().substring(0, 5);
            for (int i = 1; i <= 3; i++) {
                if (!arvosanaMap.containsKey(prefix + "_VAL" + i)) {
                    toAdd.put(prefix + "_VAL" + i, "Ei arvosanaa");
                }
            }
        }
        arvosanaMap.putAll(toAdd);
        return arvosanaMap;
    }

    private Map<String, String> suorituksenArvosanat(String prefix, String id) {
        Map<String, String> suorituksenArvosanat = new HashMap<>();
        List<ArvosanaDTO> arvosanaList = suoritusrekisteriService.getArvosanat(id);
        for (ArvosanaDTO arvosana : arvosanaList) {
            String aine = prefix + arvosana.getAine();
            String lisatieto = arvosana.getLisatieto();
            if (isNotBlank(lisatieto)) {
                suorituksenArvosanat.put(aine + "_OPPIAINE", lisatieto);
            }
            if (!arvosana.isValinnainen()) {
                suorituksenArvosanat.put(aine, arvosana.getArvosana());
            } else if (!suorituksenArvosanat.containsKey(aine + "_VAL1")) {
                suorituksenArvosanat.put(aine + "_VAL1", arvosana.getArvosana());
            } else if (!suorituksenArvosanat.containsKey(aine + "_VAL2")) {
                suorituksenArvosanat.put(aine + "_VAL2", arvosana.getArvosana());
            } else if (!suorituksenArvosanat.containsKey(aine + "_VAL3")) {
                suorituksenArvosanat.put(aine + "_VAL3", arvosana.getArvosana());
            }
        }
        return suorituksenArvosanat;
    }

    private void clearGrades(final Application application) {
        LOGGER.info("Clearing grades for application {}", application.getOid());
        Map<String, String> originalGradeAnswers = application.getPhaseAnswers(OppijaConstants.PHASE_GRADES);
        Map<String, String> gradeAnswers = new HashMap<>(originalGradeAnswers);
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
            return PERUSKOULU;
        } else if ("Alueittain".equals(yksilollistaminen)) {
            return ALUEITTAIN_YKSILOLLISTETTY;
        } else if ("Kokonaan".equals(yksilollistaminen)) {
            return YKSILOLLISTETTY;
        } else if ("Osittain".equals(yksilollistaminen)) {
            return OSITTAIN_YKSILOLLISTETTY;
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
