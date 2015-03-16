package fi.vm.sade.haku.oppija.hakemus.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.jersey.core.impl.provider.entity.XMLJAXBElementProvider;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.SyntheticApplication;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationDAO;
import fi.vm.sade.haku.virkailija.authentication.Person;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
public class SyntheticApplicationService {
    private final ApplicationDAO applicationDAO;
    private final ApplicationOidService applicationOidService;

    @Autowired
    public SyntheticApplicationService(final ApplicationDAO applicationDAO, final ApplicationOidService applicationOidService) {
        this.applicationDAO = applicationDAO;
        this.applicationOidService = applicationOidService;
    }

    public List<Application> createApplications(SyntheticApplication applicationStub) {
        List<Application> returns = new ArrayList<Application>();
        for (SyntheticApplication.Hakemus hakemus : applicationStub.hakemukset) {
            Application app = applicationForStub(hakemus, applicationStub);
            Application dbApp = applicationDAO.getApplication(app.getOid(), "oid", "version");
            if (null == dbApp){
                applicationDAO.save(app);
            }
            else {
                applicationDAO.update(new Application(dbApp.getOid(), dbApp.getVersion()), app);
            }
            returns.add(app);
        }
        return returns;
    }

    private Application applicationForStub(SyntheticApplication.Hakemus hakemus, SyntheticApplication stub) {

        Application query = new Application();
        query.setPersonOid(hakemus.hakijaOid);
        query.setApplicationSystemId(stub.hakuOid);
        List<Application> applications = applicationDAO.find(query);

        if(applications.isEmpty()) {
            return newApplication(stub, hakemus);
        } else {
            Application current = Iterables.getFirst(applications, query);
            return updateApplication(stub, hakemus, current);
        }
    }

    private Application newApplication(SyntheticApplication stub, SyntheticApplication.Hakemus hakemus) {
        Application app = new Application();
        app.setOid(applicationOidService.generateNewOid());
        app.setApplicationSystemId(stub.hakuOid);
        app.setRedoPostProcess(Application.PostProcessingState.DONE);
        app.setState(Application.State.ACTIVE);
        app.setPersonOid(hakemus.hakijaOid);

        Person person = new Person(hakemus.etunimi, hakemus.sukunimi, hakemus.henkilotunnus, hakemus.sahkoposti, hakemus.hakijaOid, hakemus.syntymaAika);
        Map<String, String> henkilotiedot = updateHenkiloTiedot(person, new HashMap<String, String>());
        app.addVaiheenVastaukset(OppijaConstants.PHASE_PERSONAL, henkilotiedot);

        HashMap<String, String> hakutoiveet = new HashMap<String, String>();
        hakutoiveet.put("preference1-Koulutus-id", stub.hakukohdeOid);
        hakutoiveet.put("preference1-Opetuspiste-id", stub.tarjoajaOid);
        app.addVaiheenVastaukset(OppijaConstants.PHASE_APPLICATION_OPTIONS, hakutoiveet);
        return app;
    }

    private Application updateApplication(SyntheticApplication stub, SyntheticApplication.Hakemus hakemus, Application current) {
        Person person = new Person(hakemus.etunimi, hakemus.sukunimi, hakemus.henkilotunnus, hakemus.sahkoposti, hakemus.hakijaOid, hakemus.syntymaAika);
        Map<String, String> henkilotiedot = updateHenkiloTiedot(person, current.getAnswers().get(OppijaConstants.PHASE_PERSONAL));
        current.updateNameMetadata();
        current.addVaiheenVastaukset(OppijaConstants.PHASE_PERSONAL, henkilotiedot);

        addHakutoive(current, stub.hakukohdeOid, stub.tarjoajaOid);
        return current;
    }

    private Map<String, String> updateHenkiloTiedot(Person person, Map<String, String> henkilotiedot) {
        if (isNotBlank(person.getFirstNames())) {
            henkilotiedot.put(OppijaConstants.ELEMENT_ID_FIRST_NAMES, person.getFirstNames());
            henkilotiedot.put(OppijaConstants.ELEMENT_ID_NICKNAME, person.getFirstNames());
        }
        if (isNotBlank(person.getLastName())) {
            henkilotiedot.put(OppijaConstants.ELEMENT_ID_LAST_NAME, person.getLastName());
        }
        if (isNotBlank(person.getSocialSecurityNumber())) {
            henkilotiedot.put(OppijaConstants.ELEMENT_ID_SOCIAL_SECURITY_NUMBER, person.getSocialSecurityNumber());
        }
        if (isNotBlank(person.getSex())) {
            henkilotiedot.put(OppijaConstants.ELEMENT_ID_SEX, person.getSex());
        }
        if (isNotBlank(person.getDateOfBirth())) {
            henkilotiedot.put(OppijaConstants.ELEMENT_ID_DATE_OF_BIRTH, person.getDateOfBirth());
        }
        Boolean eiSuomalaistaHetua = person.isNoSocialSecurityNumber();
        if (eiSuomalaistaHetua != null) {
            henkilotiedot.put(OppijaConstants.ELEMENT_ID_HAS_SOCIAL_SECURITY_NUMBER, String.valueOf(!eiSuomalaistaHetua));
        }
        Boolean securityOrder = person.isSecurityOrder();
        if (securityOrder != null) {
            henkilotiedot.put(OppijaConstants.ELEMENT_ID_SECURITY_ORDER, String.valueOf(securityOrder));
        }
        String personOid = person.getPersonOid();
        if (isNotBlank(personOid)) {
            henkilotiedot.put(OppijaConstants.ELEMENT_ID_PERSON_OID, person.getPersonOid());
        }
        String studentOid = person.getStudentOid();
        if (isNotBlank(studentOid)) {
            henkilotiedot.put(OppijaConstants.ELEMENT_ID_STUDENT_OID, person.getStudentOid());
        }
        if (isNotBlank(person.getEmail())) {
            henkilotiedot.put(OppijaConstants.ELEMENT_ID_EMAIL, person.getEmail());
        }
        return henkilotiedot;
    }

    private void addHakutoive(Application application, final String hakukohdeOid, String tarjoajaOid) {

        Map<String, String> existing = existingPreferences(application);
        if(!existing.values().contains(hakukohdeOid)) {
            Map<String, String> hakutoiveet = application.getAnswers().get(OppijaConstants.PHASE_APPLICATION_OPTIONS);
            String suffix = getNextHakutoiveSuffix(existing);
            hakutoiveet.put("preference" + suffix + "-Koulutus-id", hakukohdeOid);
            hakutoiveet.put("preference" + suffix + "-Opetuspiste-id", tarjoajaOid);
        }
    }

    private String getNextHakutoiveSuffix(Map<String, String> existingPreferences) {

        TreeSet<String> usedKeys = Sets.newTreeSet(existingPreferences.keySet());
        if(usedKeys.isEmpty()) {
            return "1";
        }

        Matcher matcher = Pattern.compile("preference(\\d+)").matcher(usedKeys.last());
        if(matcher.find()) {
            String latestUsed = matcher.group(1);
            int next = Integer.parseInt(latestUsed) + 1;
            return Integer.toString(next);
        } else {
            return "1";
        }
    }

    private Map<String, String> existingPreferences(Application application) {

        return Maps.filterEntries(application.getPhaseAnswers((OppijaConstants.PHASE_APPLICATION_OPTIONS)), new Predicate<Map.Entry<String, String>>() {
            @Override
            public boolean apply(Map.Entry<String, String> input) {
                return input.getKey().matches("preference\\d+-Koulutus-id") && !input.getValue().isEmpty();
            }
        });
    }
}
