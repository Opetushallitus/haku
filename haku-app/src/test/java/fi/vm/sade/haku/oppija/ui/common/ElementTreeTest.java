package fi.vm.sade.haku.oppija.ui.common;

import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.DataRelatedQuestion;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Question;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.TextQuestion;
import fi.vm.sade.haku.oppija.lomake.exception.ElementNotFound;
import fi.vm.sade.haku.oppija.lomake.util.ElementTree;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import org.junit.Test;

import java.util.HashMap;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.createI18NAsIs;
import static org.junit.Assert.assertEquals;

public class ElementTreeTest {

    private Form form = new Form(ElementUtil.randomId(), createI18NAsIs("title"));
    private TextQuestion expectedElement = new TextQuestion(ElementUtil.randomId(), createI18NAsIs("title2"));
    private ElementTree elementTree = new ElementTree(form);

    @Test
    public void testGetRelatedData() {
        Question relatedElement = ElementUtil.createRequiredTextQuestion(ElementUtil.randomId(), "name", "form_messages_yhteishaku_syksy",
                "form_errors_yhteishaku_syksy", 10);
        HashMap<String, Element> relatedData = new HashMap<String, Element>();
        String id = ElementUtil.randomId();
        relatedData.put(id, relatedElement);
        DataRelatedQuestion<Element> dataRelatedQuestion = new DataRelatedQuestion<Element>(
                id, ElementUtil.createI18NAsIs("test"), relatedData) {
        };
        ElementTree elementTree = new ElementTree(dataRelatedQuestion);
        assertEquals(relatedElement, elementTree.getRelatedData(id, id));
    }

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
