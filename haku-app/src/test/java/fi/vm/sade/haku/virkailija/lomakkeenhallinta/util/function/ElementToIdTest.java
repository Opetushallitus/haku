package fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.function;

import fi.vm.sade.haku.oppija.lomake.domain.builder.TextQuestionBuilder;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import org.junit.Assert;
import org.junit.Test;

public class ElementToIdTest {

    public static final String EXPECTED_ID = "id";
    private ElementToId elementToId = new ElementToId();

    @Test
    public void testApply() throws Exception {
        String actualId = elementToId.apply(new TextQuestionBuilder(EXPECTED_ID).i18nText(ElementUtil.createI18NAsIs("jotain")).build());
        Assert.assertEquals(actualId, EXPECTED_ID);
    }

    @Test
    public void testApplyFail() throws Exception {
        String actualId = elementToId.apply(new TextQuestionBuilder(EXPECTED_ID + "t").i18nText(ElementUtil.createI18NAsIs("jotain")).build());
        Assert.assertNotEquals(actualId, EXPECTED_ID);
    }
}
