/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */

package fi.vm.sade.haku.oppija.hakemus.service.impl;

import com.google.common.collect.Lists;
import fi.vm.sade.haku.oppija.common.organisaatio.OrganizationService;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.ArvosanaDTO;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.OpiskelijaDTO;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusDTO;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusrekisteriService;
import fi.vm.sade.haku.oppija.hakemus.converter.ApplicationToAdditionalDataDTO;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationNote;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationAdditionalDataDTO;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationSearchResultDTO;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationDAO;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationQueryParameters;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationOidService;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.oppija.hakemus.service.HakuPermissionService;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationState;
import fi.vm.sade.haku.oppija.lomake.domain.User;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.oppija.lomake.exception.IllegalValueException;
import fi.vm.sade.haku.oppija.lomake.exception.ResourceNotFoundException;
import fi.vm.sade.haku.oppija.lomake.service.FormService;
import fi.vm.sade.haku.oppija.lomake.service.UserSession;
import fi.vm.sade.haku.oppija.lomake.util.ElementTree;
import fi.vm.sade.haku.oppija.lomake.validation.ElementTreeValidator;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationInput;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationResult;
import fi.vm.sade.haku.virkailija.authentication.AuthenticationService;
import fi.vm.sade.haku.virkailija.authentication.Person;
import fi.vm.sade.haku.virkailija.authentication.PersonBuilder;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.apache.commons.lang.StringUtils.*;

@Service
public class ApplicationServiceImpl implements ApplicationService {

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

    private static final String OPH_ORGANIZATION = "1.2.246.562.10.00000000001";
    private final ApplicationDAO applicationDAO;
    private final ApplicationOidService applicationOidService;
    private final UserSession userSession;
    private final FormService formService;
    private final AuthenticationService authenticationService;
    private final OrganizationService organizationService;
    private final HakuPermissionService hakuPermissionService;
    private final SuoritusrekisteriService suoritusrekisteriService;
    private final ElementTreeValidator elementTreeValidator;

    @Autowired
    public ApplicationServiceImpl(@Qualifier("applicationDAOMongoImpl") ApplicationDAO applicationDAO,
                                  final UserSession userSession,
                                  @Qualifier("formServiceImpl") final FormService formService,
                                  @Qualifier("applicationOidServiceImpl") ApplicationOidService applicationOidService,
                                  AuthenticationService authenticationService,
                                  OrganizationService organizationService,
                                  HakuPermissionService hakuPermissionService,
                                  SuoritusrekisteriService suoritusrekisteriService, ElementTreeValidator elementTreeValidator) {

        this.applicationDAO = applicationDAO;
        this.userSession = userSession;
        this.formService = formService;
        this.applicationOidService = applicationOidService;
        this.authenticationService = authenticationService;
        this.organizationService = organizationService;
        this.hakuPermissionService = hakuPermissionService;
        this.suoritusrekisteriService = suoritusrekisteriService;
        this.elementTreeValidator = elementTreeValidator;
    }


    @Override
    public ApplicationState saveApplicationPhase(ApplicationPhase applicationPhase) {
        final Application application = new Application(this.userSession.getUser(), applicationPhase);
        return saveApplicationPhase(applicationPhase, application);
    }

    private ApplicationState saveApplicationPhase(final ApplicationPhase applicationPhase,
                                                  final Application application) {
        //
        final String applicationSystemId = application.getApplicationSystemId();
        final Form activeForm = formService.getActiveForm(applicationSystemId);
        ElementTree elementTree = new ElementTree(activeForm);
        final Element phase = elementTree.getChildById(applicationPhase.getPhaseId());
        final Map<String, String> answers = applicationPhase.getAnswers();

        Map<String, String> allAnswers = new HashMap<String, String>();
        // if the current phase has previous phase, get all the answers for
        // validating rules
        Application current = userSession.getApplication(applicationSystemId);

        elementTree.isStateValid(current.getPhaseId(), applicationPhase.getPhaseId());

        allAnswers.putAll(current.getVastauksetMergedIgnoringPhase(applicationPhase.getPhaseId()));
        allAnswers.putAll(answers);

        final ApplicationState applicationState = new ApplicationState(application, applicationPhase.getPhaseId(), allAnswers);

        if (elementTree.isValidationNeeded(applicationPhase.getPhaseId(), application.getPhaseId())) {
            ValidationResult validationResult = elementTreeValidator.validate(new ValidationInput(phase, allAnswers,
                    application.getOid(), applicationSystemId));
            applicationState.addError(validationResult.getErrorMessages());
        }

        if (applicationState.isValid()) {
            this.userSession.savePhaseAnswers(applicationPhase);
        }
        return applicationState;
    }

    @Override
    public Application submitApplication(final String applicationSystemId) {
        final User user = userSession.getUser();
        Application application = userSession.getApplication(applicationSystemId);
        Form form = formService.getForm(applicationSystemId);
        Map<String, String> allAnswers = application.getVastauksetMerged();
        ValidationResult validationResult = elementTreeValidator.validate(new ValidationInput(form, allAnswers,
                application.getOid(), applicationSystemId));

        if (!validationResult.hasErrors()) {

            application.setOid(applicationOidService.generateNewOid());
            if (!user.isKnown()) {
                application.removeUser();
            }
            application.resetUser();
            application.setReceived(new Date());
            application.addNote(new ApplicationNote("Hakemus vastaanotettu", new Date(), userSession.getUser().getUserName()));
            application.setLastAutomatedProcessingTime(System.currentTimeMillis());
            application.submitted();
            application.flagStudentIdentificationRequired();
            this.applicationDAO.save(application);
            this.userSession.removeApplication(application);
            return application;
        } else {
            throw new IllegalStateException("Could not send the application ");
        }
    }

    @Override
    public Application addPersonOid(Application application) {
        Map<String, String> allAnswers = application.getVastauksetMerged();

        LOGGER.debug("start addPersonAndAuthenticate, {}", System.currentTimeMillis() / 1000L);

        PersonBuilder personBuilder = PersonBuilder.start()
                .setFirstNames(allAnswers.get(OppijaConstants.ELEMENT_ID_FIRST_NAMES))
                .setNickName(allAnswers.get(OppijaConstants.ELEMENT_ID_NICKNAME))
                .setLastName(allAnswers.get(OppijaConstants.ELEMENT_ID_LAST_NAME))
                .setSex(allAnswers.get(OppijaConstants.ELEMENT_ID_SEX))
                .setHomeCity(allAnswers.get(OppijaConstants.ELEMENT_ID_HOME_CITY))
                .setLanguage(allAnswers.get(OppijaConstants.ELEMENT_ID_LANGUAGE))
                .setNationality(allAnswers.get(OppijaConstants.ELEMENT_ID_NATIONALITY))
                .setContactLanguage(allAnswers.get(OppijaConstants.ELEMENT_ID_CONTACT_LANGUAGE))
                .setSocialSecurityNumber(allAnswers.get(OppijaConstants.ELEMENT_ID_SOCIAL_SECURITY_NUMBER))
                .setPersonOid(allAnswers.get(application.getPersonOid()))
                .setSecurityOrder(false);

        application.setLastAutomatedProcessingTime(System.currentTimeMillis());
        Person personBefore = personBuilder.get();
        LOGGER.debug("Calling addPerson");
        try {
            Person personAfter = authenticationService.addPerson(personBefore);
            LOGGER.debug("Called addPerson");
            LOGGER.debug("Calling modifyPersonalData");
            application = application.modifyPersonalData(personAfter);
            LOGGER.debug("Called modifyPersonalData");
        } catch (Throwable t) {
            LOGGER.debug("Unexpected happened: ", t);
        }
        return application;
    }

    @Override
    public Application checkStudentOid(Application application) {
        String personOid = application.getPersonOid();

        if (isEmpty(personOid)) {
            application = addPersonOid(application);
            personOid = application.getPersonOid();
        }

        String studentOid = application.getStudentOid();

        if (isNotEmpty(personOid) && isEmpty(studentOid)) {
            Person person = authenticationService.checkStudentOid(application.getPersonOid());
            application.modifyPersonalData(person);
            application.studentIdentificationDone();
        }

        application.setLastAutomatedProcessingTime(System.currentTimeMillis());
        applicationDAO.save(application);
        return application;
    }

    @Override
    public Application getSubmittedApplication(final String applicationSystemId, final String oid) {
        Application submittedApplication = userSession.getSubmittedApplication();
        if (submittedApplication != null &&
                submittedApplication.getApplicationSystemId().equals(applicationSystemId) &&
                submittedApplication.getOid().equals(oid)) {
            return submittedApplication;
        }
        throw new ResourceNotFoundException("Could not found submitted application");
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
            LOGGER.debug("Sending school found ({}), adding parents", sendingSchool);
            List<String> parentOids = organizationService.findParentOids(sendingSchool);
            parentOids.add(OPH_ORGANIZATION);
            parentOids.add(sendingSchool);
            String parentOidStr = join(parentOids, ",");
            LOGGER.debug("parentOids: {}", parentOidStr);
            answers.put(OppijaConstants.ELEMENT_ID_SENDING_SCHOOL_PARENTS, parentOidStr);
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

        boolean ammattistarttiSuoritettu = false;
        boolean kuntouttavaSuoritettu = false;
        boolean mamuValmentavaSuoritettu = false;
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

        if (pohjakoulutus == null) {
            return application;
        }

        Map<String, String> educationAnswers = new HashMap<String, String>(application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION));

        if (gradesTranferredLk) {
            application.addMeta("grades_transferred_lk", "true");
        } else if (gradesTranferredPk) {
            application.addMeta("grades_transferred_pk", "true");
        }

        educationAnswers = addRegisterValue(application, educationAnswers,
                OppijaConstants.ELEMENT_ID_BASE_EDUCATION, String.valueOf(pohjakoulutus));
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
            receivedGrades.add(key);
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

    @Override
    public Application getApplication(final String applicationSystemId) {
        User user = userSession.getUser();
        if (user.isKnown()) {
            Application application = new Application(applicationSystemId, userSession.getUser());
            List<Application> listOfApplications = applicationDAO.find(application);
            if (listOfApplications.isEmpty() || listOfApplications.size() > 1) {
                return application;
            }
            return listOfApplications.get(0);
        } else {
            return userSession.getApplication(applicationSystemId);

        }
    }

    @Override
    public Application getApplicationByOid(String oid) {
        return getApplication(new Application(oid));
    }

    @Override
    public ApplicationSearchResultDTO findApplications(final String term,
                                                       final ApplicationQueryParameters applicationQueryParameters) {
        return applicationDAO.findAllQueried(term, applicationQueryParameters);
    }

    @Override
    public List<Map<String, Object>> findFullApplications(final String query,
                                                  final ApplicationQueryParameters applicationQueryParameters) {
        return applicationDAO.findAllQueriedFull(query, applicationQueryParameters);
    }

    @Override
    public void saveApplicationAdditionalInfo(List<ApplicationAdditionalDataDTO> applicationAdditionalData) {
        if (applicationAdditionalData != null) {
            for (ApplicationAdditionalDataDTO data : applicationAdditionalData) {
                saveApplicationAdditionalInfo(data.getOid(), data.getAdditionalData());
            }
        }
    }

    @Override
    public void saveApplicationAdditionalInfo(String oid, Map<String, String> additionalInfo) {
        Application query = new Application(oid);
        Application current = getApplication(query);
        hakuPermissionService.userCanEditApplicationAdditionalData(current);
        current.setAdditionalInfo(additionalInfo);
        applicationDAO.update(query, current);
    }

    @Override
    public void update(final Application queryApplication, final Application application) {
        this.applicationDAO.update(queryApplication, application);
    }


    @Override
    public String getApplicationKeyValue(String applicationOid, String key) {
        Application application = getApplication(new Application(applicationOid));
        if (application.getAdditionalInfo().containsKey(key)) {
            return application.getAdditionalInfo().get(key);
        } else if (application.getVastauksetMerged().containsKey(key)) {
            return application.getVastauksetMerged().get(key);
        } else {
            throw new ResourceNotFoundException(String.format("Could not find application : %s value of key : %s",
                    applicationOid, key));
        }
    }

    @Override
    public void putApplicationAdditionalInfoKeyValue(String applicationOid, String key, String value) {
        //access application to verify permissions
        getApplication(new Application(applicationOid));
        if (value == null) {
            throw new IllegalArgumentException("Value can't be null");
        } else {
            applicationDAO.updateKeyValue(applicationOid, "additionalInfo." + key, value);
        }
    }

    @Override
    public List<ApplicationAdditionalDataDTO> findApplicationAdditionalData(final String applicationSystemId, final String aoId) {
        List<Application> applications = applicationDAO.findByApplicationSystemAndApplicationOption(applicationSystemId, aoId);
        return Lists.transform(applications, new ApplicationToAdditionalDataDTO());
    }

    @Override
    public Application fillLOPChain(final Application application, final boolean save) {
        String[] ids = new String[]{
                "preference1-Opetuspiste-id",
                "preference2-Opetuspiste-id",
                "preference3-Opetuspiste-id",
                "preference4-Opetuspiste-id",
                "preference5-Opetuspiste-id"};

        Map<String, String> hakutoiveet = application.getAnswers().get("hakutoiveet");
        if (hakutoiveet != null) {
            HashMap<String, String> answers = new HashMap<String, String>(hakutoiveet);
            for (String id : ids) {
                String opetuspiste = answers.get(id);
                if (isNotEmpty(opetuspiste)) {
                    List<String> parentOids = organizationService.findParentOids(opetuspiste);
                    // OPH-guys have access to all organizations
                    parentOids.add(OPH_ORGANIZATION);
                    // Also add organization itself
                    parentOids.add(opetuspiste);
                    answers.put(id + "-parents", join(parentOids, ","));
                }
            }
            application.addVaiheenVastaukset("hakutoiveet", answers);
            if (save) {
                this.applicationDAO.save(application);
            }
        }
        return application;
    }

    @Override
    public Application officerCreateNewApplication(String asId) {
        Application application = new Application();
        application.setApplicationSystemId(asId);
        application.setReceived(new Date());
        application.setState(Application.State.INCOMPLETE);
        application.addNote(new ApplicationNote("Hakemus vastaanotettu", new Date(), userSession.getUser().getUserName()));
        application.setOid(applicationOidService.generateNewOid());
        this.applicationDAO.save(application);
        return application;
    }

    public Application getApplication(final Application queryApplication) {

        LOGGER.debug("Entering ApplicationServiceImpl.getApplication()");
        List<Application> listOfApplications;
        try {
            listOfApplications = applicationDAO.find(queryApplication);
            LOGGER.debug("Got "+listOfApplications.size()+" applications");
        } catch (IllegalArgumentException iae) {
            LOGGER.error("Error getting application: ", iae);
            throw new ResourceNotFoundException("Error getting application", iae);
        } catch(RuntimeException t) {
            LOGGER.error("Getting application failed: "+t);
            throw t;
        }
        if (listOfApplications == null || listOfApplications.isEmpty()) {
            throw new ResourceNotFoundException("Could not find application " + queryApplication.getOid());
        }
        if (listOfApplications.size() > 1) {
            throw new ResourceNotFoundException("Found multiple applications with oid " + queryApplication.getOid());
        }

        Application application = listOfApplications.get(0);

        if (!hakuPermissionService.userCanReadApplication(application)) {
            throw new ResourceNotFoundException("User "+  authenticationService.getCurrentHenkilo().getPersonOid()  +" is not allowed to read application " + application.getOid());
        }
        return application;
    }
}
