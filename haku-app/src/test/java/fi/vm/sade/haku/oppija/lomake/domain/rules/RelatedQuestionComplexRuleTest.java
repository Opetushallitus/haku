package fi.vm.sade.haku.oppija.lomake.domain.rules;

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.Equals;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.Value;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.Variable;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RelatedQuestionComplexRuleTest {

    public static final String AIDINKIELI = "aidinkieli";
    public static final String FI = "fi";
    public static final String SV = "sv";
    private RelatedQuestionRule relatedQuestionRule;

    @Before
    public void setUp() throws Exception {
        Equals equals = new Equals(new Variable(AIDINKIELI), new Value(FI));
        relatedQuestionRule = new RelatedQuestionRule(ElementUtil.randomId(), equals);
        relatedQuestionRule.addChild(ElementUtil.createHiddenGradeGridRowWithId(ElementUtil.randomId()));
    }

    @Test
    public void testGetChildrenEmpty() {
        List<Element> children = relatedQuestionRule.getChildren(ImmutableMap.of(AIDINKIELI, SV));
        assertTrue(children.isEmpty());
    }

    @Test
    public void testGetChildrenTrue() {
        List<Element> children = relatedQuestionRule.getChildren(ImmutableMap.of(AIDINKIELI, FI));
        assertFalse(children.isEmpty());
    }
}
