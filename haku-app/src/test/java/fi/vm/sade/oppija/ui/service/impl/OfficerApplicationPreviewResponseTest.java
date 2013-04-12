package fi.vm.sade.oppija.ui.service.impl;

import fi.vm.sade.oppija.common.valintaperusteet.AdditionalQuestions;
import fi.vm.sade.oppija.lomake.domain.I18nText;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.domain.util.ElementUtil;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OfficerApplicationPreviewResponseTest {


    public static final I18nText TEXT = ElementUtil.createI18NForm("text");
    public static final Form FORM = new Form("id", TEXT);
    private OfficerApplicationPreviewResponse officerApplicationPreviewResponse =
            new OfficerApplicationPreviewResponse();

    @Test(expected = IllegalArgumentException.class)
    public void testSetFormNull() {
        officerApplicationPreviewResponse.setForm(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetElementNull() {
        officerApplicationPreviewResponse.setElement(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetAdditionalQuestionsNull() {
        officerApplicationPreviewResponse.setAdditionalQuestions(null);
    }

    @Test
    public void testSetForm() {
        officerApplicationPreviewResponse.setForm(FORM);
        assertResult(FORM, OfficerApplicationPreviewResponse.FORM);
    }

    @Test
    public void testSetElement() {
        officerApplicationPreviewResponse.setElement(FORM);
        assertResult(FORM, OfficerApplicationPreviewResponse.ELEMENT);
    }

    @Test
    public void testSetAdditionalQuestions() {
        AdditionalQuestions expected = new AdditionalQuestions();
        officerApplicationPreviewResponse.setAdditionalQuestions(expected);
        assertResult(expected, OfficerApplicationPreviewResponse.ADDITIONAL_QUESTIONS);
    }

    private void assertResult(Object expected, final String key) {
        Object actual = officerApplicationPreviewResponse.getModel().get(key);
        assertEquals(expected, actual);
    }
}
