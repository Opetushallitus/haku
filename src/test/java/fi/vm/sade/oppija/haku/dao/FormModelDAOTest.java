package fi.vm.sade.oppija.haku.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import fi.vm.sade.oppija.haku.dao.impl.FormModelDummyMemoryDaoImpl;
import fi.vm.sade.oppija.haku.domain.*;
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

import static junit.framework.Assert.assertEquals;

/**
 * @author jukka
 * @version 9/7/121:02 PM}
 * @since 1.1
 */


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
public class FormModelDAOTest {

    @Autowired
    @Qualifier("formModelDAOMongoImpl")
    FormModelDAO formModelDAO;

    @Test
    public void test() throws Exception {
        final FormModel model = createForm();
        System.out.println(serialize(model));
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


    @Test
    public void testDeserialize() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        final FormModel form1 = createForm();
        final String id = form1.getApplicationPerioidMap().keySet().iterator().next();
        final FormModel formModel = mapper.readValue(serialize(form1), FormModel.class);
        final List<Element> children = getCategoryChilds(id, formModel);
        assertEquals(children.get(0).getId(), getCategoryChilds(id, form1).get(0).getId());
    }

    private List<Element> getCategoryChilds(String id, FormModel formModel) {
        final ApplicationPeriod activePeriodById = formModel.getApplicationPeriodById(id);

        final Form formById = activePeriodById.getForms().entrySet().iterator().next().getValue();
        formById.init();
        final Category cat1 = formById.getFirstCategory();
        return cat1.getChildren();
    }

    @Test
    public void testInsert() {
        final FormModel form = createForm();

        formModelDAO.insert(form);
        final FormModel formModel = formModelDAO.find();
        final String id = getPeriodId(form);
        final String id1 = getPeriodId(formModel);
        assertEquals(id, id1);
        formModelDAO.delete(formModel);
    }

    private String getPeriodId(FormModel form) {
        final Map<String, ApplicationPeriod> applicationPerioidMap = form.getApplicationPerioidMap();
        final ApplicationPeriod applicationPeriod = applicationPerioidMap.entrySet().iterator().next().getValue();
        return applicationPeriod.getId();
    }

}
