package fi.vm.sade.haku.oppija.hakemus.service.impl;

import fi.vm.sade.haku.oppija.hakemus.service.BaseEducationService;
import fi.vm.sade.haku.oppija.common.organisaatio.OrganizationService;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.ArvosanaDTO;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.OpiskelijaDTO;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusDTO;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusrekisteriService;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.lomake.exception.IllegalValueException;
import fi.vm.sade.haku.oppija.lomake.exception.ResourceNotFoundException;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.apache.commons.lang.StringUtils.join;

@Service
public class BaseEducationServiceImpl implements BaseEducationService{

    public static final Logger LOGGER = LoggerFactory.getLogger(ApplicationServiceImpl.class);

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

    private final OrganizationService organizationService;
    private final SuoritusrekisteriService suoritusrekisteriService;

    @Autowired
    public BaseEducationServiceImpl(OrganizationService organizationService,
      SuoritusrekisteriService suoritusrekisteriService) {
        this.organizationService = organizationService;
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
                        throw new ResourceNotFoundException("Person "+personOid+" in enrolled in multiple schools");
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

        Map<String, String> answers = new HashMap<String, String>(application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION));
        if (answers.containsKey(OppijaConstants.ELEMENT_ID_SENDING_SCHOOL)) {
            String sendingSchool = answers.get(OppijaConstants.ELEMENT_ID_SENDING_SCHOOL);
            List<String> parentOids = organizationService.findParentOids(sendingSchool);
            answers.put(OppijaConstants.ELEMENT_ID_SENDING_SCHOOL_PARENTS, join(parentOids, ","));
            application.addVaiheenVastaukset(OppijaConstants.PHASE_EDUCATION, answers);
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

        SuoritusDTO lukio = suoritukset.get(lukioKomoOid);
        SuoritusDTO ulkomainen = suoritukset.get(ulkomainenKomoOid);
        SuoritusDTO kymppi = suoritukset.get(lisaopetusKomoOid);
        SuoritusDTO ammattistartti = suoritukset.get(valmistavaKomoOid);
        SuoritusDTO kuntouttava = suoritukset.get(kuntouttavaKomoOid);
        SuoritusDTO mamuValmentava = suoritukset.get(mamuValmistavaKomoOid);
        SuoritusDTO pk = suoritukset.get(perusopetusKomoOid);

        Map<String, String> educationAnswers = new HashMap<String, String>(application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION));

        String ammattistarttiSuoritettuStr = educationAnswers.get(OppijaConstants.ELEMENT_ID_LISAKOULUTUS_AMMATTISTARTTI);
        String kuntouttavaSuoritettuStr = educationAnswers.get(OppijaConstants.ELEMENT_ID_LISAKOULUTUS_VAMMAISTEN);
        String mamuValmentavaSuoritettuStr = educationAnswers.get(OppijaConstants.ELEMENT_ID_LISAKOULUTUS_MAAHANMUUTTO);

        boolean ammattistarttiSuoritettu = isNotBlank(ammattistarttiSuoritettuStr) ? Boolean.valueOf(ammattistarttiSuoritettuStr) : false;
        boolean kuntouttavaSuoritettu = isNotBlank(kuntouttavaSuoritettuStr) ? Boolean.valueOf(kuntouttavaSuoritettuStr) : false;
        boolean mamuValmentavaSuoritettu = isNotBlank(mamuValmentavaSuoritettuStr) ? Boolean.valueOf(mamuValmentavaSuoritettuStr) : false;

        boolean gradesTranferredPk = false;
        boolean gradesTranferredLk = false;

        if (lukio != null) {
            pohjakoulutus = OppijaConstants.YLIOPPILAS;
            addGrades(application, lukio);
            gradesTranferredLk = true;
            valmistuminen = lukio.getValmistuminen();
            suorituskieli = lukio.getSuorituskieli();
        } else if (ulkomainen != null) {
            pohjakoulutus = OppijaConstants.ULKOMAINEN_TUTKINTO;
        } else if (kymppi != null) {
            addGrades(application, kymppi);
            gradesTranferredPk = true;
            valmistuminen = kymppi.getValmistuminen();
            suorituskieli = kymppi.getSuorituskieli();
            pohjakoulutus = getPohjakoulutus(kymppi);

            if (pk != null) {
                addGrades(application, pk);
            }
        } else if (ammattistartti != null) {
            ammattistarttiSuoritettu = true;

            if (pk != null) {
                valmistuminen = pk.getValmistuminen();
                suorituskieli = pk.getSuorituskieli();
                pohjakoulutus = getPohjakoulutus(pk);
                addGrades(application, pk);
                gradesTranferredPk = true;
            }
        } else if (kuntouttava != null) {
            kuntouttavaSuoritettu = true;

            if (pk != null) {
                valmistuminen = pk.getValmistuminen();
                suorituskieli = pk.getSuorituskieli();
                pohjakoulutus = getPohjakoulutus(pk);
                addGrades(application, pk);
                gradesTranferredPk = true;
            }
        } else if (mamuValmentava != null) {
            mamuValmentavaSuoritettu = true;

            if (pk != null) {
                valmistuminen = pk.getValmistuminen();
                suorituskieli = pk.getSuorituskieli();
                pohjakoulutus = getPohjakoulutus(pk);
                addGrades(application, pk);
                gradesTranferredPk = true;
            }
        } else if (pk != null) {
            valmistuminen = pk.getValmistuminen();
            suorituskieli = pk.getSuorituskieli();
            pohjakoulutus = getPohjakoulutus(pk);
            addGrades(application, pk);
            gradesTranferredPk = true;
        }

        boolean pohjakoulutusSuoritettu = pohjakoulutus != null;

        if (!(ammattistarttiSuoritettu || kuntouttavaSuoritettu || mamuValmentavaSuoritettu || pohjakoulutusSuoritettu)) {
            return application;
        }

        //        if (pohjakoulutus == null ) {
        //            return application;
        //        }

        if (gradesTranferredLk) {
            application.addMeta("grades_transferred_lk", "true");
        } else if (gradesTranferredPk) {
            application.addMeta("grades_transferred_pk", "true");
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
            educationAnswers= addRegisterValue(application, educationAnswers, todistusvuosiKey, todistusvuosi);
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

    private String getPohjakoulutus(SuoritusDTO suoritus) {
        String yksilollistaminen = suoritus.getYksilollistaminen();
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

    private Map<String, String> addGrades(Application application, SuoritusDTO suoritus) {
        String suoritusId = suoritus.getId();

        List<ArvosanaDTO> arvosanat = suoritusrekisteriService.getArvosanat(suoritusId);
        if (arvosanat.isEmpty()) {
            return new HashMap<String, String>();
        }
        String prefix = getGradePrefix(suoritus);

        Map<String, String> gradeAnswers = new HashMap<String, String>(application.getPhaseAnswers(OppijaConstants.PHASE_GRADES));
        Set<String> receivedGrades = new HashSet<String>();
        application.addMeta("osaaminen_locked", "true");

        Map<String, Integer> valinnaiset = new HashMap<String, Integer>();

        // Käy läpi rekisteristä tulleet arvosanat ja lisää answers-mappiin
        for (ArvosanaDTO arvosana : arvosanat) {
            String suffix = getGradeSuffix(suoritus, valinnaiset, arvosana);
            String key = prefix + arvosana.getAine() + suffix;
            if (!receivedGrades.add(key)) {
                throw new IllegalValueException("Doublegrade: "+key+" for person "+application.getPersonOid());
            }
            gradeAnswers = addRegisterValue(application, gradeAnswers, key, arvosana.getArvosana());
            // Lisätieto == kieli (AI, A1, B1 jne)
            if (isNotBlank(arvosana.getLisatieto())) {
                gradeAnswers = addRegisterValue(application, gradeAnswers,
                  prefix + arvosana.getAine() + "_OPPIAINE", arvosana.getLisatieto());
            }
        }

        // Lisää "Ei arvosanaa" puuttuviin kenttiin
        Map<String, String> toAdd = new HashMap<String, String>();
        boolean kymppi = suoritus.getKomo().equals(lisaopetusKomoOid);
        for (Map.Entry<String, String> entry : gradeAnswers.entrySet()) {
            String key = entry.getKey();
            if (!key.startsWith(prefix)) {
                continue;
            } else if (kymppi != key.endsWith("_10")) {
                continue;
            }
            if (!receivedGrades.contains(key) && !key.endsWith("OPPIAINE")) {
                application.addOverriddenAnswer(key, gradeAnswers.get(key));
                toAdd.put(key, "Ei arvosanaa");
            }
            if (suoritus.getKomo().equals(perusopetusKomoOid) && !key.endsWith("OPPIAINE") && !key.endsWith("_VAL1")
              && !key.endsWith("VAL2")) {
                if (!gradeAnswers.containsKey(key + "_VAL1")) {
                    toAdd.put(key + "_VAL1", "Ei arvosanaa");
                }
                if (!gradeAnswers.containsKey(key + "_VAL2")) {
                    toAdd.put(key + "_VAL2", "Ei arvosanaa");
                }
            }
        }
        gradeAnswers.putAll(toAdd);
        application.addVaiheenVastaukset(OppijaConstants.PHASE_GRADES, gradeAnswers);
        return gradeAnswers;
    }

    private String getGradePrefix(SuoritusDTO suoritus) {
        String prefix = "PK_";
        if (suoritus.getKomo().equals(lukioKomoOid)) {
            prefix = "LK_";
        }
        return prefix;
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

    private Map<String, String> addRegisterValue(Application application, Map<String, String> answers,
      String key, String value) {
        String oldValue = answers.put(key, value);
        application.addOverriddenAnswer(key, oldValue);
        LOGGER.info("Changing value key: {}, value: {} -> {}", key, oldValue, value);
        return answers;
    }
}
