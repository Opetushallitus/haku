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

import static java.lang.ClassLoader.getSystemResourceAsStream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static net.sourceforge.jwebunit.junit.JWebUnit.beginAt;
import static net.sourceforge.jwebunit.junit.JWebUnit.getPageSource;

import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import fi.vm.sade.oppija.common.it.AbstractRemoteTest;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.domain.dto.ApplicationDTO;
import fi.vm.sade.oppija.lomake.dao.TestDBFactoryBean;

/**
 * @author Hannu Lyytikainen
 */
public class ApplicationIT extends AbstractRemoteTest {

    @Autowired
    TestDBFactoryBean dbFactory;

    protected static List<DBObject> applicationTestDataObject;

    @SuppressWarnings("unchecked")
    @BeforeClass
    public static void readTestData() throws IOException {
        String content = IOUtils.toString(getSystemResourceAsStream("application-test-data.json"), "UTF-8");
        applicationTestDataObject = (List<DBObject>) JSON.parse(content);
    }

    @Before
    public void setUp() throws Exception {
        super.initTestEngine();
        try {
            dbFactory.getObject().getCollection("application").drop();
            dbFactory.getObject().getCollection("application").insert(applicationTestDataObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFindAllApplications() throws IOException {
        beginAt("applications");
        String response = getPageSource();

        ObjectMapper mapper = new ObjectMapper();
        List<Application> applications = mapper.readValue(response, new TypeReference<List<Application>>() {
        });
        assertEquals(3, applications.size());
    }

    @Test
    public void testFindApplications() throws IOException {
        beginAt("applications?q=1.2.3.4.5.00000010003");
        String response = getPageSource();

        ObjectMapper mapper = new ObjectMapper();
        List<Application> applications = mapper.readValue(response, new TypeReference<List<Application>>() {
        });
        assertEquals(1, applications.size());
    }

    @Test
    public void testFindApplicationsNoMatch() throws IOException {
        beginAt("applications?q=nomatch");
        String response = getPageSource();

        ObjectMapper mapper = new ObjectMapper();
        List<Application> applications = mapper.readValue(response, new TypeReference<List<Application>>() {
        });
        assertEquals(0, applications.size());
    }

    @Test
    public void testGetApplication() throws IOException {
        beginAt("applications/1.2.3.4.5.00000010003/");
        String response = getPageSource();

        ObjectMapper mapper = new ObjectMapper();
        Application application = mapper.readValue(response, new TypeReference<Application>() {
        });
        assertNotNull(application);
        assertEquals("1.2.3.4.5.00000010003", application.getOid());
    }
}
