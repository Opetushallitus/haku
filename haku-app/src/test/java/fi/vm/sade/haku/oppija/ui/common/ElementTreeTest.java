package fi.vm.sade.haku.oppija.ui.common;

import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.TextQuestion;
import fi.vm.sade.haku.oppija.lomake.domain.builder.TextQuestionBuilder;
import fi.vm.sade.haku.oppija.lomake.exception.ElementNotFound;
import fi.vm.sade.haku.oppija.lomake.util.ElementTree;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import org.junit.Test;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.createI18NAsIs;
import static org.junit.Assert.assertEquals;

public class ElementTreeTest {

    private Form form = new Form(ElementUtil.randomId(), createI18NAsIs("title"));
    private TextQuestion expectedElement = (TextQuestion) new TextQuestionBuilder(ElementUtil.randomId()).i18nText(createI18NAsIs("title2")).build();
    private ElementTree elementTree = new ElementTree(form);

    @Test
    public void testGetElementById() throws Exception {
        form.addChild(expectedElement);
        Element actualElement = elementTree.getChildById(expectedElement.getId());
        assertEquals(expectedElement, actualElement);
    }

    @Test(expected = ElementNotFound.class)
    public void testGetElementByIdNotFound() throws Exception {
        form.addChild(expectedElement);
        elementTree.getChildById(expectedElement.getId() + "xxx");
    }

}
