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
package fi.vm.sade.oppija.yksilointi;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import fi.vm.sade.oppija.common.dao.AbstractDAOTest;
import fi.vm.sade.oppija.hakemus.dao.ApplicationDAO;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import static java.lang.ClassLoader.getSystemResourceAsStream;

public class YksilointiWorkerTest extends AbstractDAOTest {

    public static final Logger LOGGER = LoggerFactory.getLogger(YksilointiWorkerTest.class);

    //@Autowired
    //@Qualifier("applicationDAOMongoImpl")
    private ApplicationDAO applicationDAO;

    protected static List<DBObject> applicationTestDataObject;

    //@SuppressWarnings("unchecked")
    //@BeforeClass
    public static void readTestData() throws IOException {
        String content = IOUtils.toString(getSystemResourceAsStream("application-test-data.json"), "UTF-8");
        applicationTestDataObject = (List<DBObject>) JSON.parse(content);
    }

    //@Before
    public void setUp() {
        try {
            mongoWrapper.getCollection(getCollectionName()).insert(applicationTestDataObject);
        } catch (Exception e) {
            LOGGER.error("Error set up test", e);
        }
    }

    //@Test
    public void testProcessApplications() {
//        YksilointiWorker worker = new YksilointiWorker(applicationDAO);
//        worker.processApplications();

    }

    @Override
    protected String getCollectionName() {
        return "application";
    }
}
