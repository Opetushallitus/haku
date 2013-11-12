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

package fi.vm.sade.oppija.hakemus.it;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import fi.vm.sade.oppija.common.selenium.DummyModelBaseItTest;
import fi.vm.sade.oppija.common.selenium.LoginPage;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.domain.dto.ApplicationSearchResultDTO;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static java.lang.ClassLoader.getSystemResourceAsStream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ApplicationIT extends DummyModelBaseItTest {

    protected static List<DBObject> applicationTestDataObject;

    @SuppressWarnings("unchecked")
    @BeforeClass
    public static void readTestData() throws IOException {
        String content = IOUtils.toString(getSystemResourceAsStream("application-test-data.json"), "UTF-8");
        applicationTestDataObject = (List<DBObject>) JSON.parse(content);
    }

    @Before
    public void beforeApplicationIT() throws Exception {
        mongoTemplate.getCollection("application").insert(applicationTestDataObject);
        final LoginPage loginPage = new LoginPage(seleniumContainer.getSelenium());
        navigateToPath("user", "login");
        loginPage.login("officer");
    }

//    @Test
    public void testFindAllApplications() throws IOException {
        navigateToPath("applications");
        screenshot("findAllApplications");
        ApplicationSearchResultDTO applications = responseToSearchResult();
        assertEquals(3, applications.getResults().size());
        assertEquals(3, applications.getTotalCount());
    }

    @Test
    public void testFindApplications() throws IOException {
        navigateToPath("applications?q=1.2.3.4.5.00000010003");
        screenshot("findApplications");
        ApplicationSearchResultDTO applications = responseToSearchResult();
        assertEquals(1, applications.getResults().size());
        assertEquals(1, applications.getTotalCount());
    }

//    @Test
    public void testFindApplicationsNoMatch() throws IOException {
        navigateToPath("applications?q=nomatch");
        ApplicationSearchResultDTO applications = responseToSearchResult();
        assertEquals(0, applications.getTotalCount());
    }

//    @Test
    public void testGetApplication() throws IOException {
        navigateToPath("applications/1.2.3.4.5.00000010003/");
        screenshot("getApplication");
        String response = selenium.getBodyText();
        ObjectMapper mapper = new ObjectMapper();
        Application application = mapper.readValue(response, new TypeReference<Application>() {
        });
        assertNotNull(application);
        assertEquals("1.2.3.4.5.00000010003", application.getOid());
    }

    private ApplicationSearchResultDTO responseToSearchResult() throws IOException {
        String response = selenium.getBodyText();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(response, new TypeReference<ApplicationSearchResultDTO>() {
        });
    }

}
