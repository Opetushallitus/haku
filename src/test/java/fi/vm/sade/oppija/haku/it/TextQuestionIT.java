package fi.vm.sade.oppija.haku.it;

import org.junit.Test;

import java.io.IOException;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

public class TextQuestionIT extends AbstractIT {

    public TextQuestionIT() {
        jsonModelFileName = "text-question-test.json";
    }

    @Test
    public void testFormExists() throws IOException {
        beginAt("/fi/h/yh/textquestion");
        assertFormPresent("form-textquestion");
    }

    @Test
    public void testInputExists() throws IOException {
        beginAt("/fi/h/yh/textquestion");
        assertElementPresent("sukunimi");
    }

}
