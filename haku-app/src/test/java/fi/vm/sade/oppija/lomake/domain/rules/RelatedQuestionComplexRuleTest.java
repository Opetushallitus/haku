package fi.vm.sade.oppija.lomake.domain.rules;

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.oppija.lomake.domain.elements.Element;
import fi.vm.sade.oppija.lomake.domain.rules.expression.Value;
import fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RelatedQuestionComplexRuleTest {

    public static final String AIDINKIELI = "aidinkieli";
    public static final String OPETUSKIELI = "opetus";
    public static final String AIDINKIELI_OPPIAINE = "aidinkieli_oppiaine";
    public static final String VALINNAINEN_KIELI = "valinnainen_kieli";
    public static final String FI = "fi";
    public static final String VALINNAINEN_KIELI_ARVOSANA = "valinnainen_kieli_arvosana";
    public static final String SV = "sv";
    private RelatedQuestionComplexRule relatedQuestionComplexRule;

    @Before
    public void setUp() throws Exception {
        List<Map<String, String>> rules = new ArrayList<Map<String, String>>();
        rules.add(ImmutableMap.of(AIDINKIELI, FI));
        rules.add(ImmutableMap.of(OPETUSKIELI, FI));
        rules.add(ImmutableMap.of(AIDINKIELI_OPPIAINE, FI));
        rules.add(ImmutableMap.of(VALINNAINEN_KIELI, FI, VALINNAINEN_KIELI_ARVOSANA, "7"));
        rules.add(ImmutableMap.of(VALINNAINEN_KIELI, FI, VALINNAINEN_KIELI_ARVOSANA, "8"));
        rules.add(ImmutableMap.of(VALINNAINEN_KIELI, FI, VALINNAINEN_KIELI_ARVOSANA, "9"));
        rules.add(ImmutableMap.of(VALINNAINEN_KIELI, FI, VALINNAINEN_KIELI_ARVOSANA, "10"));
        relatedQuestionComplexRule = new RelatedQuestionComplexRule(ElementUtil.randomId(), new Value("10"));
        relatedQuestionComplexRule.addChild(ElementUtil.createHiddenGradeGridRowWithId(ElementUtil.randomId()));
    }

    @Test
    public void testGetChildrenEmpty() {
        List<Element> children = relatedQuestionComplexRule.getChildren(ImmutableMap.of(AIDINKIELI, SV));
        assertTrue(children.isEmpty());
    }

    @Test
    public void testGetChildrenTrue() {
        List<Element> children = relatedQuestionComplexRule.getChildren(ImmutableMap.of(AIDINKIELI, FI));
        assertFalse(children.isEmpty());
    }

    @Test
    public void testGetChildrenValinnainenTrue() {
        List<Element> children = relatedQuestionComplexRule.getChildren(
                ImmutableMap.of(VALINNAINEN_KIELI, FI, VALINNAINEN_KIELI_ARVOSANA, "7"));
        assertFalse(children.isEmpty());
    }

    @Test
    public void testGetChildrenValinnainenFalse() {
        List<Element> children = relatedQuestionComplexRule.getChildren(
                ImmutableMap.of(VALINNAINEN_KIELI, FI, VALINNAINEN_KIELI_ARVOSANA, "6"));
        assertTrue(children.isEmpty());
    }
}
