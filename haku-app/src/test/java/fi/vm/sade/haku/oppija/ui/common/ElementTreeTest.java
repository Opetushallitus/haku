package fi.vm.sade.haku.oppija.ui.common;

import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.TextQuestion;
import fi.vm.sade.haku.oppija.lomake.domain.builder.TextQuestionBuilder;
import fi.vm.sade.haku.oppija.lomake.exception.ElementNotFound;
import fi.vm.sade.haku.oppija.lomake.util.ElementTree;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.createI18NAsIs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ElementTreeTest {

    private static Form form = new Form(ElementUtil.randomId(), createI18NAsIs("title"));
    private static Phase firstPhase = new Phase("first", null, false, new ArrayList<String>());
    private static Phase secondPhase = new Phase("second", null, false, new ArrayList<String>());
    private static Phase lastPhase = new Phase("last", null, false, new ArrayList<String>());
    private ElementTree elementTree = new ElementTree(form);

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @BeforeClass
    public static void initForm() {
        form = new Form("id", createI18NAsIs("title"));
        form.addChild(firstPhase);
        form.addChild(secondPhase);
        form.addChild(lastPhase);
    }

    @Test
    public void testYouCanGoTheFirstPhase() throws Exception {
        elementTree.checkPhaseTransfer(null, "first");
    }

    @Test
    public void testYouCannotSkipTheFirstPhase() throws Exception {
        exception.expect(ElementNotFound.class);
        elementTree.checkPhaseTransfer(null, "second");
    }

    @Test
    public void testYouAreAllowedToMoveEarlierPhase() throws Exception {
        elementTree.checkPhaseTransfer("second", "first");
        elementTree.checkPhaseTransfer("last", "first");
        elementTree.checkPhaseTransfer("last", "second");
    }

    @Test
    public void testYouAreAllowedToMoveFromEsikatseluToAnyPhase() throws Exception {
        elementTree.checkPhaseTransfer("esikatselu", "first");
        elementTree.checkPhaseTransfer("esikatselu", "second");
        elementTree.checkPhaseTransfer("esikatselu", "last");
    }

    @Test
    public void testYouCannotSkipPhase() throws Exception {
        exception.expect(ElementNotFound.class);
        elementTree.checkPhaseTransfer("first", "second");
        exception.expect(ElementNotFound.class);
        elementTree.checkPhaseTransfer("second", "last");
        exception.expect(ElementNotFound.class);
        elementTree.checkPhaseTransfer("last", "esikatselu");
    }

    @Test
    public void testYouCanMoveToCurrentPhase() throws Exception {
        elementTree.checkPhaseTransfer("first", "first");
        elementTree.checkPhaseTransfer("second", "second");
        elementTree.checkPhaseTransfer("last", "last");
    }

    @Test
    public void testYouCannotMoveToUnexistingPhase() throws Exception {
        exception.expect(ElementNotFound.class);
        elementTree.checkPhaseTransfer("first", "some");
    }

    @Test
    public void testValidationIsNeededAfterLastPhase() throws Exception {
        assertTrue(elementTree.isValidationNeeded("last", null));
    }

    @Test
    public void testValidationIsNeededAfterLastPhaseEvenWhenUnknownPhaseNext() throws Exception {
        assertTrue(elementTree.isValidationNeeded("last", "some"));
        assertTrue(elementTree.isValidationNeeded("last", "esikatselu"));
    }

    @Test
    public void testValidationFailsIfNextPhaseIsUnknown() throws Exception {
        exception.expect(ElementNotFound.class);
        elementTree.isValidationNeeded("fist", "some");
        exception.expect(ElementNotFound.class);
        elementTree.isValidationNeeded("fist", "some");
    }

    @Test
    public void testValidationIsNotNeededIfNextPhaseIsBefore() throws Exception {
        assertFalse(elementTree.isValidationNeeded("second", "first"));
        assertFalse(elementTree.isValidationNeeded("last", "second"));
        assertFalse(elementTree.isValidationNeeded("last", "first"));
    }

    @Test
    public void testValidationIsNotNeededWhenStayingSamePhase() throws Exception {
        assertFalse(elementTree.isValidationNeeded("first", "first"));
        assertFalse(elementTree.isValidationNeeded("second", "second"));
        assertFalse(elementTree.isValidationNeeded("last", "last"));
    }

    @Test
    public void testValidationIsNeededIfMovingForward() throws Exception {
        assertTrue(elementTree.isValidationNeeded("first", "second"));
        assertTrue(elementTree.isValidationNeeded("first", "last"));
        assertTrue(elementTree.isValidationNeeded("second", "last"));
    }

}
