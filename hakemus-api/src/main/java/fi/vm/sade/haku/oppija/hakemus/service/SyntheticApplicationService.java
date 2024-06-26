package fi.vm.sade.haku.oppija.hakemus.service;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.PreferenceEligibility;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.SyntheticApplication;
import fi.vm.sade.haku.oppija.hakemus.domain.util.ApplicationUtil;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationDAO;
import fi.vm.sade.haku.virkailija.authentication.Person;
import fi.vm.sade.haku.virkailija.authentication.PersonBuilder;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;

@Service
public class SyntheticApplicationService {
    private final ApplicationDAO applicationDAO;
    private final ApplicationOidService applicationOidService;
    private final ApplicationService applicationService;

    @Autowired
    public SyntheticApplicationService(final ApplicationDAO applicationDAO, final ApplicationOidService applicationOidService, final ApplicationService applicationService) {
        this.applicationDAO = applicationDAO;
        this.applicationOidService = applicationOidService;
        this.applicationService = applicationService;
    }

    public List<Application> createApplications(SyntheticApplication applicationStub) throws IOException {
        List<Application> returns = new ArrayList<>();
        for (SyntheticApplication.Hakemus hakemus : applicationStub.hakemukset) {
            Application app = applicationForStub(hakemus, applicationStub);
            Application dbApp = applicationDAO.getApplication(app.getOid(), "oid", "version");
            applicationService.updateAuthorizationMeta(app);
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

    private Application applicationForStub(SyntheticApplication.Hakemus hakemus, final SyntheticApplication stub) {

        Application query = new Application();
        query.setPersonOid(hakemus.hakijaOid);
        query.setApplicationSystemId(stub.hakuOid);

        Iterator<Application> applications = Iterables.filter(applicationDAO.find(query), new Predicate<Application>() {
            @Override
            public boolean apply(Application application) {
                if(Application.State.ACTIVE.equals(application.getState())
                        || Application.State.INCOMPLETE.equals(application.getState())) {
                    Map<String, String> existing = existingPreferences(application);
                    // duplicate hakemus when not same hakutoive
                    return existing.containsValue(stub.hakukohdeOid);
                } else {
                    return false;
                }
            }
        }).iterator();

        if (!applications.hasNext()) {
            return newApplication(stub, hakemus);
        } else {
            return updateApplication(stub, hakemus, applications.next());
        }
    }
    private Person hakemusToPerson(SyntheticApplication.Hakemus hakemus) {
        PersonBuilder builder = PersonBuilder.start()
                .setFirstNames(hakemus.etunimi)
                .setLastName(hakemus.sukunimi)
                .setEmail(hakemus.sahkoposti)
                .setPersonOid(hakemus.hakijaOid)
                .setDateOfBirth(hakemus.syntymaAika);

        if(trimToNull(hakemus.osoite) != null) {
            builder.setAddress(hakemus.osoite);
        }
        if(trimToNull(hakemus.asuinmaa) != null) {
            builder.setCountryOfResidence(hakemus.asuinmaa);
        }
        String asiointikieli = asiointikieli(hakemus.asiointikieli);
        if(trimToNull(asiointikieli) != null) {
            builder.setContactLanguage(asiointikieli);
        }
        if(trimToNull(hakemus.kansalaisuus) != null) {
            builder.setNationality(hakemus.kansalaisuus);
        }
        if(trimToNull(hakemus.puhelinnumero) != null) {
            builder.setPhone(hakemus.puhelinnumero);
        }
        if(trimToNull(hakemus.postinumero) != null) {
            builder.setPostalCode(hakemus.postinumero);
        }
        if(trimToNull(hakemus.postitoimipaikka) != null) {
            builder.setPostalCity(hakemus.postitoimipaikka);
        }
        if(trimToNull(hakemus.sukupuoli) != null) {
            builder.setSex(hakemus.sukupuoli);
        }
        if(trimToNull(hakemus.aidinkieli) != null) {
            builder.setLanguage(hakemus.aidinkieli.toUpperCase());
        }
        if(trimToNull(hakemus.henkilotunnus) == null) {
            builder.setNoSocialSecurityNumber(true);
        } else {
            builder.setSocialSecurityNumber(hakemus.henkilotunnus);
        }
        if(trimToNull(hakemus.kotikunta) != null) {
            builder.setHomeCity(hakemus.kotikunta);
        }
        return builder.get();
    }

    private String asiointikieli(String asiointikieli) {
        if(null != trimToNull(asiointikieli)) {
            if("FI".equalsIgnoreCase(asiointikieli)) {
                return "suomi";
            }
            if("EN".equalsIgnoreCase(asiointikieli)) {
                return "englanti";
            }
            if("SV".equalsIgnoreCase(asiointikieli)) {
                return "ruotsi";
            }
        }
        return null;
    }

    private Application newApplication(SyntheticApplication stub, SyntheticApplication.Hakemus hakemus) {

        Application app = new Application();
        app.setOid(applicationOidService.generateNewOid());
        app.setApplicationSystemId(stub.hakuOid);
        app.setRedoPostProcess(Application.PostProcessingState.DONE);
        app.setState(Application.State.ACTIVE);
        app.setPersonOid(hakemus.hakijaOid);

        Person person = hakemusToPerson(hakemus);
        Map<String, String> henkilotiedot = updateHenkiloTiedot(person, new HashMap<String, String>());
        app.setVaiheenVastauksetAndSetPhaseId(OppijaConstants.PHASE_PERSONAL, henkilotiedot);

        Map<String, String> lisatiedot = updateLisatiedot(person, new HashMap<String, String>());
        app.setVaiheenVastauksetAndSetPhaseId(OppijaConstants.PHASE_MISC, lisatiedot);

        Map<String, String> koulutustausta = updateKoulutustausta(person, hakemus, new HashMap<String, String>());
        app.setVaiheenVastauksetAndSetPhaseId(OppijaConstants.PHASE_EDUCATION, koulutustausta);


        HashMap<String, String> hakutoiveet = new HashMap<>();
        hakutoiveet.put("preference1-Koulutus-id", stub.hakukohdeOid);
        hakutoiveet.put("preference1-Opetuspiste-id", stub.tarjoajaOid);
        app.setVaiheenVastauksetAndSetPhaseId(OppijaConstants.PHASE_APPLICATION_OPTIONS, hakutoiveet);
        app.setPreferenceEligibilities(ApplicationUtil.checkAndCreatePreferenceEligibilities(
                app.getPreferenceEligibilities(), asList(stub.hakukohdeOid)));
        app.setPreferencesChecked(ApplicationUtil.checkAndCreatePreferenceCheckedData(
                app.getPreferencesChecked(), asList(stub.hakukohdeOid)));

        app.getPreferenceEligibilities().forEach(e -> {
            if(stub.hakukohdeOid.equals(e.getAoId())) {
                e.setMaksuvelvollisuus(getMaksuvelvollisuusFromHakemus(hakemus));
            }
        });
        return app;
    }
    private PreferenceEligibility.Maksuvelvollisuus getMaksuvelvollisuusFromHakemus(SyntheticApplication.Hakemus hakemus) {
        return StringUtils.isNotEmpty(hakemus.maksuvelvollisuus) ? PreferenceEligibility.Maksuvelvollisuus.valueOf(hakemus.maksuvelvollisuus) : PreferenceEligibility.Maksuvelvollisuus.NOT_CHECKED;
    }

    private Application updateApplication(SyntheticApplication stub, SyntheticApplication.Hakemus hakemus, Application current) {
        Person person = hakemusToPerson(hakemus);
        Map<String, String> henkilotiedot = updateHenkiloTiedot(person, current.getPhaseAnswersForModify(OppijaConstants.PHASE_PERSONAL));
        current.updateNameMetadata();
        current.setVaiheenVastauksetAndSetPhaseId(OppijaConstants.PHASE_PERSONAL, henkilotiedot);

        Map<String, String> lisatiedot = updateLisatiedot(person, current.getPhaseAnswersForModify(OppijaConstants.PHASE_MISC));
        current.setVaiheenVastauksetAndSetPhaseId(OppijaConstants.PHASE_MISC, lisatiedot);

        Map<String, String> koulutustausta = updateKoulutustausta(person, hakemus, current.getPhaseAnswersForModify(OppijaConstants.PHASE_EDUCATION));
        current.setVaiheenVastauksetAndSetPhaseId(OppijaConstants.PHASE_EDUCATION, koulutustausta);

        addHakutoive(current, stub.hakukohdeOid, stub.tarjoajaOid);
        List<String> preferenceAoIds = ApplicationUtil.getPreferenceAoIds(current);
        current.setPreferenceEligibilities(ApplicationUtil.checkAndCreatePreferenceEligibilities(
                current.getPreferenceEligibilities(), preferenceAoIds));
        current.setPreferencesChecked(ApplicationUtil.checkAndCreatePreferenceCheckedData(
                current.getPreferencesChecked(), preferenceAoIds));
        current.getPreferenceEligibilities().forEach(e -> {
            if(stub.hakukohdeOid.equals(e.getAoId())) {
                e.setMaksuvelvollisuus(getMaksuvelvollisuusFromHakemus(hakemus));
            }
        });
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
        if (isNotBlank(person.getSex())) {
            henkilotiedot.put(OppijaConstants.ELEMENT_ID_SEX, person.getSex());
        }
        if (isNotBlank(person.getLanguage())) {
            henkilotiedot.put(OppijaConstants.ELEMENT_ID_LANGUAGE, person.getLanguage());
        }
        if (isNotBlank(person.getDateOfBirth())) {
            henkilotiedot.put(OppijaConstants.ELEMENT_ID_DATE_OF_BIRTH, person.getDateOfBirth());
        }

        setOrRemoveOsioValue(OppijaConstants.ELEMENT_ID_SOCIAL_SECURITY_NUMBER, person.getSocialSecurityNumber(), henkilotiedot);
        boolean eiSuomalaistaHetua = BooleanUtils.isTrue(person.isNoSocialSecurityNumber());
        setOrRemoveOsioValue(OppijaConstants.ELEMENT_ID_HAS_SOCIAL_SECURITY_NUMBER, !eiSuomalaistaHetua, henkilotiedot);

        if (person.isSecurityOrder() != null) {
            henkilotiedot.put(OppijaConstants.ELEMENT_ID_SECURITY_ORDER, Boolean.toString(person.isSecurityOrder()));
        }
        if (isNotBlank(person.getPersonOid())) {
            henkilotiedot.put(OppijaConstants.ELEMENT_ID_PERSON_OID, person.getPersonOid());
        }
        setOrRemoveOsioValue(OppijaConstants.ELEMENT_ID_EMAIL, person.getEmail(), henkilotiedot);
        setOrRemoveOsioValue(OppijaConstants.ELEMENT_ID_PREFIX_PHONENUMBER + "1", person.getPhone(), henkilotiedot);

        String countryOfResidence = person.getCountryOfResidence();
        setOrRemoveOsioValue(OppijaConstants.ELEMENT_ID_COUNTRY_OF_RESIDENCY, countryOfResidence, henkilotiedot);

        boolean finnishResidence = null == trimToNull(countryOfResidence) ||
                OppijaConstants.ELEMENT_VALUE_COUNTRY_OF_RESIDENCY_FIN.equalsIgnoreCase(countryOfResidence);

        setOrRemoveOsioValue(OppijaConstants.ELEMENT_ID_FIN_ADDRESS, finnishResidence ? person.getAddress() : null, henkilotiedot);
        setOrRemoveOsioValue(OppijaConstants.ELEMENT_ID_ADDRESS_ABROAD, finnishResidence ? null : person.getAddress(), henkilotiedot);

        setOrRemoveOsioValue(OppijaConstants.ELEMENT_ID_FIN_POSTAL_NUMBER, finnishResidence ? person.getPostalCode() : null, henkilotiedot);
        setOrRemoveOsioValue(OppijaConstants.ELEMENT_ID_POSTAL_NUMBER_ABROAD, finnishResidence ? null : person.getPostalCode(), henkilotiedot);

        setOrRemoveOsioValue(OppijaConstants.ELEMENT_ID_CITY_ABROAD, finnishResidence ? null : person.getPostalCity(), henkilotiedot);

        setOrRemoveOsioValue(OppijaConstants.ELEMENT_ID_HOME_CITY, person.getHomeCity(), henkilotiedot);

        setOrRemoveOsioValue(OppijaConstants.ELEMENT_ID_NATIONALITY, person.getNationality(), henkilotiedot);

        return henkilotiedot;
    }

    private Map<String, String> updateLisatiedot(Person person, Map<String, String> lisatiedot) {
        setOrRemoveOsioValue(OppijaConstants.ELEMENT_ID_CONTACT_LANGUAGE, person.getContactLanguage(), lisatiedot);
        return lisatiedot;
    }

    private Map<String, String> updateKoulutustausta(Person person, SyntheticApplication.Hakemus hakemus, Map<String, String> koulutustiedot) {
        setOrRemoveOsioValue(OppijaConstants.TOISEN_ASTEEN_SUORITUS, hakemus.toisenAsteenSuoritus, koulutustiedot);
        setOrRemoveOsioValue(OppijaConstants.TOISEN_ASTEEN_SUORITUSMAA, hakemus.toisenAsteenSuoritusmaa, koulutustiedot);
        return koulutustiedot;
    }

    private void setOrRemoveOsioValue(String key, Boolean value, Map<String, String> osio) {
        if (value != null) {
            osio.put(key, Boolean.toString(value.booleanValue()));
        }
        else if (osio.containsKey(key)) {
            osio.remove(key);
        }
    }

    private void setOrRemoveOsioValue(String key, String value, Map<String, String> osio) {
        if (isNotBlank(value)) {
            osio.put(key, value);
        }
        else if (osio.containsKey(key)) {
            osio.remove(key);
        }
    }

    private void addHakutoive(Application application, final String hakukohdeOid, String tarjoajaOid) {

        Map<String, String> existing = existingPreferences(application);
        if(!existing.containsValue(hakukohdeOid)) {
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
