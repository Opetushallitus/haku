package fi.vm.sade.haku.testfixtures;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationDAO;
import fi.vm.sade.haku.virkailija.authentication.Person;
import fi.vm.sade.haku.virkailija.authentication.PersonBuilder;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;

public class FixtureSSNFixer {
    public static void updateEmptySsnInApplications(String personOid, String ssn, ApplicationDAO dao) {
        Application queryApplication = new Application().setPersonOid(personOid);
        List<Application> applicationJavaObjects = dao.find(queryApplication);
        for (Application application: applicationJavaObjects) {
            Map<String, String> allAnswers = application.getVastauksetMerged();
            if (StringUtils.isEmpty(allAnswers.get(OppijaConstants.ELEMENT_ID_SOCIAL_SECURITY_NUMBER))) {
                setHetu(application, ssn, dao);
                //TODO RS add Version
                dao.update(new Application().setOid(application.getOid()), application);
            }
        }
    }

    private static void setHetu(Application application, String ssn, ApplicationDAO dao) {
        Map<String, String> allAnswers = application.getVastauksetMerged();
        PersonBuilder personBuilder = PersonBuilder.start()
            .setFirstNames(allAnswers.get(OppijaConstants.ELEMENT_ID_FIRST_NAMES))
            .setNickName(allAnswers.get(OppijaConstants.ELEMENT_ID_NICKNAME))
            .setLastName(allAnswers.get(OppijaConstants.ELEMENT_ID_LAST_NAME))
            .setSex(allAnswers.get(OppijaConstants.ELEMENT_ID_SEX))
            .setHomeCity(allAnswers.get(OppijaConstants.ELEMENT_ID_HOME_CITY))
            .setLanguage(allAnswers.get(OppijaConstants.ELEMENT_ID_LANGUAGE))
            .setNationality(allAnswers.get(OppijaConstants.ELEMENT_ID_NATIONALITY))
            .setContactLanguage(allAnswers.get(OppijaConstants.ELEMENT_ID_CONTACT_LANGUAGE))
            .setSocialSecurityNumber(ssn)
            .setPersonOid(application.getPersonOid()).setSecurityOrder(false);
        Person person = personBuilder.get();
        application.modifyPersonalData(person);
    }
}
