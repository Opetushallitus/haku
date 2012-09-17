package fi.vm.sade.oppija.haku.it;

import fi.vm.sade.oppija.haku.FormModelHelper;
import fi.vm.sade.oppija.haku.domain.builders.FormModelBuilder;
import fi.vm.sade.oppija.haku.domain.questions.TextQuestion;
import org.junit.Test;

import java.io.IOException;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

public class TextQuestionIT extends AbstractRemoteTest {

    private final FormModelHelper formModelHelper;

    public TextQuestionIT() {
        formModel = new FormModelBuilder().withDefaults().addChildToCategory(new TextQuestion("sukunimi", "foo", "foo")).build();
        this.formModelHelper = new FormModelHelper(formModel);
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

}
