package fi.vm.sade.oppija.haku.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import fi.vm.sade.oppija.haku.dao.impl.FormModelDummyMemoryDaoImpl;
import fi.vm.sade.oppija.haku.domain.ApplicationPeriod;
import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.domain.elements.Vaihe;
import fi.vm.sade.oppija.haku.domain.elements.Element;
import fi.vm.sade.oppija.haku.domain.elements.Form;
import fi.vm.sade.oppija.haku.tools.FileHandling;
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
import static junit.framework.Assert.assertNotNull;

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
            dbFactory.getObject().getCollection(getCollectionName()).insert(applicationPeriodTestDataObject);
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
        final FormModel formModel = formModelDAO.find();
        final String id = getPeriodId(form);
        final String id1 = getPeriodId(formModel);
        assertEquals(id, id1);
    }

    @Test
    public void testFind() {
        FormModel formModel = formModelDAO.find();
        assertNotNull("Could not retrieve FormModel", formModel);
        assertNotNull("Found FormModel does not include an application period map", formModel.getApplicationPerioidMap());
        assertEquals("Found incorrect amount of forms", 1, formModel.getApplicationPerioidMap().get("test").getForms().size());

    }

    private String serialize(FormModel model) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
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
        final Vaihe cat1 = formById.getFirstCategory();
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
