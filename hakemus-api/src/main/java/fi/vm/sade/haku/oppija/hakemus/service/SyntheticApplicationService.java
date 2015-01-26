package fi.vm.sade.haku.oppija.hakemus.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fi.vm.sade.haku.oppija.lomake.util.StringUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.apache.commons.lang3.StringUtils;
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
            applicationDAO.save(app);
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
            updateEmail(current, hakemus.sahkoposti);
            addHakutoive(current, stub.hakukohdeOid, stub.tarjoajaOid);
            return current;
        }
    }

    private Application newApplication(SyntheticApplication stub, SyntheticApplication.Hakemus hakemus) {

        Application app = new Application();
        app.setOid(applicationOidService.generateNewOid());
        app.setApplicationSystemId(stub.hakuOid);
        app.setRedoPostProcess(Application.PostProcessingState.DONE);
        app.setState(Application.State.ACTIVE);

        updateEmail(app, hakemus.sahkoposti);

        HashMap<String, String> hakutoiveet = new HashMap<String, String>();
        hakutoiveet.put("preference1-Koulutus-id", stub.hakukohdeOid);
        hakutoiveet.put("preference1-Opetuspiste-id", stub.tarjoajaOid);
        app.getAnswers().put(OppijaConstants.PHASE_APPLICATION_OPTIONS, hakutoiveet);

        return app;
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

    private void updateEmail(Application app, final String email) {
        if (StringUtils.isNotBlank(email)) {
            app.getAnswers().get(OppijaConstants.PHASE_PERSONAL).put(OppijaConstants.ELEMENT_ID_EMAIL, email);
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

        return Maps.filterEntries(application.getPhaseAnswers(("hakutoiveet")), new Predicate<Map.Entry<String, String>>() {
            @Override
            public boolean apply(Map.Entry<String, String> input) {
                return input.getKey().matches("preference\\d+-Koulutus-id") && !input.getValue().isEmpty();
            }
        });
    }
}
