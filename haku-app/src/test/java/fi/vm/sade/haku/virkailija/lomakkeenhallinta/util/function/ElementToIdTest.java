package fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.function;

import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.TextQuestion;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import org.junit.Assert;
import org.junit.Test;

public class ElementToIdTest {

    public static final String EXPECTED_ID = "id";
    private ElementToId elementToId = new ElementToId();

    @Test
    public void testApply() throws Exception {
        String actualId = elementToId.apply(new TextQuestion(EXPECTED_ID, ElementUtil.createI18NAsIs("jotain")));
        Assert.assertEquals(actualId, EXPECTED_ID);
    }

    @Test
    public void testApplyFail() throws Exception {
        String actualId = elementToId.apply(new TextQuestion(EXPECTED_ID + "t", ElementUtil.createI18NAsIs("jotain")));
        Assert.assertNotEquals(actualId, EXPECTED_ID);
    }
}
