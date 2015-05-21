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
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusrekisteriService.*;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.*;
import static java.util.Calendar.*;
import static org.apache.commons.lang.StringUtils.*;

@Service
public class BaseEducationServiceImpl implements BaseEducationService {

    public static final Logger LOGGER = LoggerFactory.getLogger(BaseEducationServiceImpl.class);

    private enum GradePrefix {PK_, LK_}

    private final SuoritusrekisteriService suoritusrekisteriService;
    private final ApplicationSystemService applicationSystemService;

    @Autowired
    public BaseEducationServiceImpl(SuoritusrekisteriService suoritusrekisteriService,
                                    ApplicationSystemService applicationSystemService) {
        this.suoritusrekisteriService = suoritusrekisteriService;
        this.applicationSystemService = applicationSystemService;
    }

    @Override
    public Application addSendingSchool(Application application) {
        String personOid = application.getPersonOid();
        if (isEmpty(personOid)) {
            return application;
        }

        List<OpiskelijaDTO> opiskelijatiedot = suoritusrekisteriService.getOpiskelijatiedot(personOid);

        if (!opiskelijatiedot.isEmpty()) {
            ApplicationSystem as = applicationSystemService.getApplicationSystem(application.getApplicationSystemId());
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
            Map<String, String> educationAnswers = new HashMap<String, String>(
                    application.getPhaseAnswers(PHASE_EDUCATION));

            educationAnswers = handleOpiskelija(educationAnswers, application, opiskelija);
            application.addVaiheenVastaukset(PHASE_EDUCATION, educationAnswers);
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

    @Override
    public Application addBaseEducation(Application application) {
        String personOid = application.getPersonOid();
        if (isEmpty(personOid)) {
            return application;
        }
        Map<String, List<SuoritusDTO>> suoritukset = suoritusrekisteriService.getSuoritukset(personOid);

        String pohjakoulutus = null;
        Date valmistuminen = null;
        String suorituskieli = null;

        SuoritusDTO lukioSuoritus = resolveSuoritus(suoritukset, LUKIO_KOMO);
        SuoritusDTO ulkomainenSuoritus = resolveSuoritus(suoritukset, ULKOMAINEN_KOMO);
        SuoritusDTO kymppiSuoritus = resolveSuoritus(suoritukset, LISAOPETUS_KOMO);
        SuoritusDTO ammattistarttiSuoritus = resolveSuoritus(suoritukset, AMMATTISTARTTI_KOMO);
        SuoritusDTO kuntouttavaSuoritus = resolveSuoritus(suoritukset, KUNTOUTTAVA_KOMO);
        SuoritusDTO mamuValmentavaSuoritus = resolveSuoritus(suoritukset, MAMU_VALMENTAVA_KOMO);
        SuoritusDTO peruskouluSuoritus = resolveSuoritus(suoritukset, PERUSOPETUS_KOMO);

        Map<String, String> educationAnswers = new HashMap<>(application.getPhaseAnswers(PHASE_EDUCATION));

        String ammattistarttiSuoritettuStr = educationAnswers.get(ELEMENT_ID_LISAKOULUTUS_AMMATTISTARTTI);
        String kuntouttavaSuoritettuStr = educationAnswers.get(ELEMENT_ID_LISAKOULUTUS_VAMMAISTEN);
        String mamuValmentavaSuoritettuStr = educationAnswers.get(ELEMENT_ID_LISAKOULUTUS_MAAHANMUUTTO);

        boolean ammattistarttiSuoritettu = isNotBlank(ammattistarttiSuoritettuStr) ? Boolean.valueOf(ammattistarttiSuoritettuStr) : false;
        boolean kuntouttavaSuoritettu = isNotBlank(kuntouttavaSuoritettuStr) ? Boolean.valueOf(kuntouttavaSuoritettuStr) : false;
        boolean mamuValmentavaSuoritettu = isNotBlank(mamuValmentavaSuoritettuStr) ? Boolean.valueOf(mamuValmentavaSuoritettuStr) : false;
        boolean kymppiSuoritettu = false;

        if (lukioSuoritus != null && isComplete(lukioSuoritus)) {
            pohjakoulutus = YLIOPPILAS;
            valmistuminen = lukioSuoritus.getValmistuminen();
            suorituskieli = lukioSuoritus.getSuoritusKieli();
        } else if (ulkomainenSuoritus != null && isComplete(ulkomainenSuoritus)) {
            pohjakoulutus = ULKOMAINEN_TUTKINTO;
        } else if (kymppiSuoritus != null && isComplete(kymppiSuoritus)) {
            if (peruskouluSuoritus == null || !isComplete(peruskouluSuoritus)) {
                LOGGER.error("Missing pk-suoritus with kymppi-suoritus for application: {} of person: {}", application.getOid(), application.getPersonOid());
                throw new ResourceNotFoundException(String.format("Missing pk-suoritus with kymppi-suoritus for application: %s of person: %s",
                        application.getOid(), application.getPersonOid()));
            }
            pohjakoulutus = getPohjakoulutus(peruskouluSuoritus);
            valmistuminen = kymppiSuoritus.getValmistuminen();
            suorituskieli = peruskouluSuoritus.getSuoritusKieli();
            kymppiSuoritettu = true;
        } else if (peruskouluSuoritus != null && isComplete(peruskouluSuoritus)) {
            pohjakoulutus = getPohjakoulutus(peruskouluSuoritus);
            valmistuminen = peruskouluSuoritus.getValmistuminen();
            suorituskieli = peruskouluSuoritus.getSuoritusKieli();
        } else {
            pohjakoulutus = KESKEYTYNYT;
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
                ELEMENT_ID_LISAKOULUTUS_KYMPPI, String.valueOf(kymppiSuoritettu));
        educationAnswers = addRegisterValue(application, educationAnswers,
                ELEMENT_ID_LISAKOULUTUS_AMMATTISTARTTI, String.valueOf(ammattistarttiSuoritettu));
        educationAnswers = addRegisterValue(application, educationAnswers,
                ELEMENT_ID_LISAKOULUTUS_VAMMAISTEN, String.valueOf(kuntouttavaSuoritettu));
        educationAnswers = addRegisterValue(application, educationAnswers,
                ELEMENT_ID_LISAKOULUTUS_MAAHANMUUTTO, String.valueOf(mamuValmentavaSuoritettu));

        if (valmistuminen != null) {
            Calendar cal = GregorianCalendar.getInstance();
            cal.setTime(valmistuminen);
            String todistusvuosi = String.valueOf(cal.get(YEAR));
            educationAnswers = addRegisterValue(application, educationAnswers, YLIOPPILAS.equals(pohjakoulutus)
                    ? LUKIO_PAATTOTODISTUS_VUOSI
                    : PERUSOPETUS_PAATTOTODISTUSVUOSI, todistusvuosi);
            educationAnswers = addRegisterValue(application, educationAnswers, YLIOPPILAS.equals(pohjakoulutus)
                    ? PERUSOPETUS_PAATTOTODISTUSVUOSI
                    : LUKIO_PAATTOTODISTUS_VUOSI,
                    null);
        } else {
            educationAnswers = addRegisterValue(application, educationAnswers, LUKIO_PAATTOTODISTUS_VUOSI, null);
            educationAnswers = addRegisterValue(application, educationAnswers, PERUSOPETUS_PAATTOTODISTUSVUOSI, null);
        }

        if (suorituskieli != null) {
            educationAnswers = addRegisterValue(application, educationAnswers, YLIOPPILAS.equals(pohjakoulutus)
                    ? LUKIO_KIELI
                    : PERUSOPETUS_KIELI, suorituskieli);
            educationAnswers = addRegisterValue(application, educationAnswers, YLIOPPILAS.equals(pohjakoulutus)
                    ? PERUSOPETUS_KIELI
                    : LUKIO_KIELI, null);
        } else {
            educationAnswers = addRegisterValue(application, educationAnswers, LUKIO_KIELI, null);
            educationAnswers = addRegisterValue(application, educationAnswers, PERUSOPETUS_KIELI, null);
        }

        application.addVaiheenVastaukset(PHASE_EDUCATION, educationAnswers);

        return application;
    }

    private SuoritusDTO resolveSuoritus(Map<String, List<SuoritusDTO>> suoritukset, String komo) {
        List<SuoritusDTO> suoritusList = suoritukset.get(komo);
        if (suoritusList == null || suoritusList.isEmpty()) {
            return null;
        }
        if (suoritusList.size() == 1) {
            return suoritusList.get(0);
        }
        List<SuoritusDTO> vahvistettu = new ArrayList<>();
        for (SuoritusDTO suoritus : suoritusList) {
            if (suoritus.getVahvistettu() != null && suoritus.getVahvistettu().booleanValue()) {
                vahvistettu.add(suoritus);
            }
        }
        if (vahvistettu.size() == 1) {
            return vahvistettu.get(0);
        } else if (vahvistettu.size() > 1) {
            return getNewestWithGrades(vahvistettu);
        }
        return getNewestWithGrades(suoritusList);
    }

    private SuoritusDTO getNewestWithGrades(List<SuoritusDTO> suoritusList) {
        SuoritusDTO latest = null;
        Map<String, Boolean> hasGrades = new HashMap<>(suoritusList.size());
        for (SuoritusDTO suoritus : suoritusList) {
            if (latest == null) {
                latest = suoritus;
            }
            hasGrades.put(suoritus.getId(), !suoritusrekisteriService.getArvosanat(suoritus.getId()).isEmpty());
            if (!hasGrades.get(latest.getId()) && hasGrades.get(suoritus.getId())) {
                latest = suoritus;
            } else if (hasGrades.get(latest.getId()) == hasGrades.get(suoritus.getId())) {
                Date latestDate = latest.getValmistuminen();
                Date currDate = suoritus.getValmistuminen();
                if (latestDate == null) {
                    latestDate = new Date(Long.MAX_VALUE);
                }
                if (currDate == null) {
                    currDate = new Date((Long.MAX_VALUE));
                }
                if (currDate.after(latestDate)) {
                    latest = suoritus;
                }
            }
        }
        return latest;
    }

    @Override
    public Map<String, String> getArvosanat(String personOid, String baseEducation, ApplicationSystem as) {
        Map<String, String> arvosanaMap = new HashMap<>();
        if (isBlank(personOid)) {
            return arvosanaMap;
        }
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
            SuoritusDTO lukioSuoritus =
                    resolveSuoritus(suoritusrekisteriService.getSuoritukset(personOid, LUKIO_KOMO), LUKIO_KOMO);
            if (lukioSuoritus != null && !SuoritusDTO.TILA_KESKEYTYNYT.equals(lukioSuoritus.getTila())) {
                arvosanaMap.putAll(suorituksenArvosanat("LK_", lukioSuoritus.getId()));
            }
        } else if (PERUSKOULU.equals(baseEducation) || YKSILOLLISTETTY.equals(baseEducation)
                || ALUEITTAIN_YKSILOLLISTETTY.equals(baseEducation) || OSITTAIN_YKSILOLLISTETTY.equals(baseEducation)) {

            Map<String, List<SuoritusDTO>> suoritukset = suoritusrekisteriService.getSuoritukset(personOid);
            SuoritusDTO pkSuoritus = resolveSuoritus(suoritukset, PERUSOPETUS_KOMO);
            if (pkSuoritus == null) {
                return arvosanaMap;
            }
            arvosanaMap.putAll(suorituksenArvosanat("PK_", pkSuoritus.getId()));
            List<SuoritusDTO> suoritusList = new ArrayList<>();
            for (String komo : new String[] { LISAOPETUS_KOMO, AMMATTISTARTTI_KOMO, KUNTOUTTAVA_KOMO,
                    MAMU_VALMENTAVA_KOMO,LUKIOON_VALMISTAVA_KOMO}) {
                SuoritusDTO suoritus = resolveSuoritus(suoritukset, komo);
                if (suoritus != null) {
                    suoritusList.add(suoritus);
                }
            }
            Collections.sort(suoritusList, new Comparator<SuoritusDTO>() {
                @Override
                public int compare(SuoritusDTO suoritus, SuoritusDTO other) {
                    return suoritus.getValmistuminen().compareTo(other.getValmistuminen());
                }
            });
            for (SuoritusDTO suoritus : suoritusList) {
                arvosanaMap = getMaxGrades(arvosanaMap, suoritus);
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

    private Map<String, String> getMaxGrades(Map<String, String> arvosanaMap, SuoritusDTO suoritus) {
        if (suoritus != null) {
            for (Map.Entry<String, String> entry : suorituksenArvosanat("PK_", suoritus.getId()).entrySet()) {
                String key = entry.getKey();
                if (key.endsWith("_OPPIAINE")) {
                    continue;
                }
                String prev = arvosanaMap.get(key);
                String curr = entry.getValue();
                arvosanaMap.put(key, maxGrade(prev, curr));
            }
        }
        return arvosanaMap;
    }

    private String maxGrade(String prev, String curr) {
        if (isEmpty(prev) || "Ei arvosanaa".equals(prev)) {
            return curr;
        }
        if (isEmpty(curr) || "Ei arvosanaa".equals(curr) || "S".equals(curr)) {
            return prev;
        }
        if ("S".equals(prev)) {
            return curr;
        }
        Integer prevInt = 0;
        Integer currInt = 0;
        try {
            prevInt = Integer.parseInt(prev);
        } catch (NumberFormatException nfe) {
            // NOP
        }
        try {
            currInt = Integer.parseInt(curr);
        } catch (NumberFormatException nfe) {
            // NOP
        }
        return String.valueOf(Math.max(prevInt, currInt));
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
            String arvio = arvosana.getArvio().getArvosana();
            if (!arvosana.isValinnainen()) {
                suorituksenArvosanat.put(aine, arvio);
            } else if (!suorituksenArvosanat.containsKey(aine + "_VAL1")) {
                suorituksenArvosanat.put(aine + "_VAL1", arvio);
            } else if (!suorituksenArvosanat.containsKey(aine + "_VAL2")) {
                suorituksenArvosanat.put(aine + "_VAL2", arvio);
            } else if (!suorituksenArvosanat.containsKey(aine + "_VAL3")) {
                suorituksenArvosanat.put(aine + "_VAL3", arvio);
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
        return !SuoritusDTO.TILA_KESKEYTYNYT.equals(suoritus.getTila());
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
