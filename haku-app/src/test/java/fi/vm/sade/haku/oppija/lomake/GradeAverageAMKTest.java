package fi.vm.sade.haku.oppija.lomake;

import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.rules.RelatedQuestionRule;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.*;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.FormConfiguration;
import fi.vm.sade.hakutest.IntegrationTest;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.FormConfiguration.FeatureFlag.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class GradeAverageAMKTest extends IntegrationTest {
    public static final String AFFECTED_APPLICATION_SYSTEM_ID = "1.2.246.562.29.95390561488";

    @Before
    public void setup() {
        mongoServer.dropCollections();
    }

    @Test
    public void testNoGradeAverageQuestionsIfGradeAverageForAMKAreFalse() {
        Map<FormConfiguration.FeatureFlag, Boolean> flags = new HashMap<>();
        flags.put(gradeAverageLukio, false);
        flags.put(gradeAverageYoAmmatillinen, false);
        flags.put(gradeAverageAmmatillinen, false);
        formConfigurationDAO.save(new FormConfiguration(AFFECTED_APPLICATION_SYSTEM_ID, FormConfiguration.FormTemplateType.YHTEISHAKU_KEVAT_KORKEAKOULU, flags));
        applicationSystemService.save(formGenerator.generate(AFFECTED_APPLICATION_SYSTEM_ID));
        ApplicationSystem as = applicationSystemService.getApplicationSystem(AFFECTED_APPLICATION_SYSTEM_ID);
        assertEquals(0, getOsaaminenQuestions(as).size());
    }

    @Test
    public void testGradeAverageQuestionsIfGradeAverageForAMKAreTrue() {
        Map<FormConfiguration.FeatureFlag, Boolean> flags = new HashMap<>();
        flags.put(gradeAverageLukio, true);
        flags.put(gradeAverageYoAmmatillinen, true);
        flags.put(gradeAverageAmmatillinen, true);
        formConfigurationDAO.save(new FormConfiguration(AFFECTED_APPLICATION_SYSTEM_ID, FormConfiguration.FormTemplateType.YHTEISHAKU_KEVAT_KORKEAKOULU, flags));
        applicationSystemService.save(formGenerator.generate(AFFECTED_APPLICATION_SYSTEM_ID));
        ApplicationSystem as = applicationSystemService.getApplicationSystem(AFFECTED_APPLICATION_SYSTEM_ID);
        assertEquals(And.class, getLukioKeskiarvoRule(getOsaaminenQuestions(as)).getExpr().getClass());
        assertEquals(And.class, getYoAmmatillinenKeskiarvoRule(getOsaaminenQuestions(as)).getExpr().getClass());
        for (RelatedQuestionRule r : getAmmatillinenKeskiarvoRules(getOsaaminenQuestions(as))) {
            assertEquals(And.class, r.getExpr().getClass());
        }
    }

    private static RelatedQuestionRule getLukioKeskiarvoRule(List<Element> osaaminenQuestions) {
        return (RelatedQuestionRule) osaaminenQuestions.get(0);
    }

    private static RelatedQuestionRule getYoAmmatillinenKeskiarvoRule(List<Element> osaaminenQuestions) {
        return (RelatedQuestionRule) osaaminenQuestions.get(1);
    }

    private static List<RelatedQuestionRule> getAmmatillinenKeskiarvoRules(List<Element> osaaminenQuestions) {
        return (List<RelatedQuestionRule>)(List<?>)osaaminenQuestions.subList(2, 7);
    }

    private static List<Element> getOsaaminenQuestions(ApplicationSystem as) {
        return as.getForm().getChildren().get(3).getChildren().get(0).getChildren();
    }
}
