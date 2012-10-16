package fi.vm.sade.oppija.haku.it;

import fi.vm.sade.oppija.haku.FormModelHelper;
import fi.vm.sade.oppija.haku.dao.impl.FormModelDummyMemoryDaoImpl;
import fi.vm.sade.oppija.haku.domain.FormModel;
import org.junit.Before;
import org.junit.Test;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

/**
 * @author Mikko Majapuro
 */
public class AdditionalQuestionsIT extends AbstractRemoteTest {

    private FormModelHelper formModelHelper;

    @Before
    public void init() {
        FormModelDummyMemoryDaoImpl dummyMem = new FormModelDummyMemoryDaoImpl();
        FormModel formModel = dummyMem.getModel();
        this.formModelHelper = initModel(formModel);
    }

    @Test
    public void testAdditionalQuestion() {
        beginAt("education/additionalquestion/test/yhteishaku/hakutoiveet/hakutoiveetGrp/2_2");
        assertElementPresent("2_2_additional_question_1");
        assertElementPresent("2_2_additional_question_2_2_2_q2_option_1");
        assertElementPresent("2_2_additional_question_2_2_2_q2_option_2");
    }

    @Test
    public void testNoAdditionalQuestion() {
        beginAt("education/additionalquestion/test/yhteishaku/hakutoiveet/hakutoiveetGrp/6_1");
        assertElementNotPresent("6_1_additional_question_1");
        assertElementNotPresent("6_1_additional_question_2_2_2_q2_option_1");
        assertElementNotPresent("6_1_additional_question_2_2_2_q2_option_2");
    }
}
