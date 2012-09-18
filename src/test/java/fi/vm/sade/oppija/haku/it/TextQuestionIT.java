package fi.vm.sade.oppija.haku.it;

import fi.vm.sade.oppija.haku.FormModelHelper;
import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.domain.builders.FormModelBuilder;
import fi.vm.sade.oppija.haku.domain.questions.TextQuestion;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

public class TextQuestionIT extends AbstractRemoteTest {
    protected FormModel formModel;
    private FormModelHelper formModelHelper;

    @Before
    public void init() throws IOException {
        formModel = new FormModelBuilder().buildDefaultFormWithFields(new TextQuestion("sukunimi", "foo", "foo"));
        this.formModelHelper = initModel(formModel);
    }

    @Test
    public void testFormExists() throws IOException {
        beginAt(formModelHelper.getStartUrl());
        final String formId = formModelHelper.getFirstCategoryFormId();
        assertFormPresent(formId);
    }

    @Test
    public void testInputExists() throws IOException {
        beginAt(formModelHelper.getStartUrl());
        assertElementPresent("sukunimi");
    }
    @Test
    public void testLabelExists() throws IOException {
        beginAt("/fi/h/yh/textquestion");
        assertElementPresent("label-sukunimi");
    }
    @Test
    public void testHelpExists() throws IOException {
        beginAt("/fi/h/yh/textquestion");
        assertElementPresent("help-sukunimi");
    }
}
