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

package fi.vm.sade.oppija.lomake.dao;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import fi.vm.sade.oppija.common.dao.AbstractDAOTest;
import fi.vm.sade.oppija.lomake.dao.impl.FormModelDummyMemoryDaoImpl;
import fi.vm.sade.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.oppija.lomake.domain.FormModel;
import fi.vm.sade.oppija.lomake.domain.elements.Element;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.oppija.lomake.tools.FileHandling;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import static java.lang.ClassLoader.getSystemResourceAsStream;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * @author jukka
 * @version 9/7/121:02 PM}
 * @since 1.1
 */


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
public class FormModelDAOTest extends AbstractDAOTest {

    @Autowired
    @Qualifier("formModelDAOMongoImpl")
    FormModelDAO formModelDAO;

    protected static DBObject applicationPeriodTestDataObject;

    @BeforeClass
    public static void readTestData() {

        String content = new FileHandling().readFile(getSystemResourceAsStream("test-data.json"));
        applicationPeriodTestDataObject = (DBObject) JSON.parse(content);

    }

    @Before
    public void insertTestData() {

        try {
            getDbFactory().getObject().getCollection(getCollectionName()).insert(applicationPeriodTestDataObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test() throws Exception {
        final FormModel model = createForm();
        System.out.println(serialize(model));
    }

    @Test
    public void testDeserialize() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        final FormModel form1 = createForm();
        final String id = form1.getApplicationPerioidMap().keySet().iterator().next();
        final FormModel formModel = mapper.readValue(serialize(form1), FormModel.class);
        final List<Element> children = getCategoryChilds(id, formModel);
        assertEquals(children.get(0).getId(), getCategoryChilds(id, form1).get(0).getId());
    }

    @Test
    public void testInsert() {
        final FormModel form = createForm();
        formModelDAO.insert(form);
        List<FormModel> formModels = formModelDAO.find(form);
        assertFalse(formModels.isEmpty());
    }

    @Test
    public void testFind() {
        final FormModel form = createForm();
        List<FormModel> formModels = formModelDAO.find(form);
        assertTrue(formModels.isEmpty());
    }

    private String serialize(FormModel model) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationConfig.Feature.INDENT_OUTPUT);
        final StringWriter w = new StringWriter();
        mapper.writeValue(w, model);
        return w.toString();
    }

    private FormModel createForm() {
        return new FormModelDummyMemoryDaoImpl().getModel();
    }


    private List<Element> getCategoryChilds(String id, FormModel formModel) {
        final ApplicationPeriod activePeriodById = formModel.getApplicationPeriodById(id);

        final Form formById = activePeriodById.getForms().entrySet().iterator().next().getValue();
        formById.init();
        final Phase cat1 = formById.getFirstPhase();
        return cat1.getChildren();
    }

    private String getPeriodId(FormModel form) {
        final Map<String, ApplicationPeriod> applicationPerioidMap = form.getApplicationPerioidMap();
        final ApplicationPeriod applicationPeriod = applicationPerioidMap.entrySet().iterator().next().getValue();
        return applicationPeriod.getId();
    }

    @Override
    protected String getCollectionName() {
        return "haku";
    }

}
