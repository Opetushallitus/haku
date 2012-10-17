package fi.vm.sade.oppija.haku.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import fi.vm.sade.oppija.haku.dao.FormModelDAO;
import fi.vm.sade.oppija.haku.dao.impl.FormModelDummyMemoryDaoImpl;
import fi.vm.sade.oppija.haku.domain.HakemusId;
import fi.vm.sade.oppija.haku.domain.User;
import fi.vm.sade.oppija.haku.domain.questions.Question;
import fi.vm.sade.oppija.haku.service.AbstractServiceTest;
import fi.vm.sade.oppija.haku.tools.FileHandling;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.*;

import static java.lang.ClassLoader.getSystemResourceAsStream;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Hannu Lyytikainen
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@Ignore
public class AdditionalQuestionsServiceTest extends AbstractServiceTest {

    private final String LOMAKE_COLLECTION = "haku";
    private final String HAKEMUS_COLLECTION = "hakemus";

    protected static DBObject lomakeTestDataObject;
    protected static List<DBObject> hakemusTestDataObjects = new ArrayList<DBObject>();

    @Autowired
    FormModelHolder formModelHolder;

    @Autowired
    private AdditionalQuestionService additionalQuestionService;

    FormModelDAO formModelDAO;

    public AdditionalQuestionsServiceTest() {
        this.formModelDAO = new FormModelDummyMemoryDaoImpl();
    }

    @BeforeClass
    public static void readTestData() {
        // read test data into objects

        //String lomakeContent = new FileHandling().readFile(getSystemResourceAsStream("test-data.json"));
        //lomakeTestDataObject = (DBObject) JSON.parse(lomakeContent);

        ObjectMapper mapper = new ObjectMapper();
        try {

            List hakemusTestObjects = mapper.readValue(getSystemResourceAsStream("hakemus-test-data.json"), List.class);
            for (Object testObject : hakemusTestObjects) {
                hakemusTestDataObjects.add(new BasicDBObject((Map)testObject));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Before
    public void insertTestData() {

        formModelHolder.updateModel(formModelDAO.find());

        try {
            dbFactory.getObject().getCollection(HAKEMUS_COLLECTION).insert(hakemusTestDataObjects);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testEducationSpecificQuestions() {

        String teemaId = "hakutoiveetGrp";
        HakemusId hakemusId = new HakemusId("test", "yhteishaku", "hakutoiveet");

        List<Question> additionalQuestions = additionalQuestionService.findAdditionalQuestions(teemaId, hakemusId);

        assertNotNull(additionalQuestions);
        assertEquals(2, additionalQuestions.size());
    }

    @Test
    public void testEducationSpecificSubjects() {
        String teemaId = "arvosanatGrp";
        HakemusId hakemusId = new HakemusId("test", "yhteishaku", "arvosanat");

        List<Question> additionalQuestions = additionalQuestionService.findAdditionalQuestions(teemaId, hakemusId);

        assertNotNull(additionalQuestions);
        assertEquals(4, additionalQuestions.size());

    }

    @Override
    protected List<String> getCollectionNames() {
        List<String> names = new ArrayList<String>();
        names.add(LOMAKE_COLLECTION);
        names.add(HAKEMUS_COLLECTION);
        return names;
    }
}
