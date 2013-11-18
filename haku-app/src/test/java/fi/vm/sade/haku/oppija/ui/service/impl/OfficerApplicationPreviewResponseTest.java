package fi.vm.sade.haku.oppija.ui.service.impl;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OfficerApplicationPreviewResponseTest {


    public static final I18nText TEXT = ElementUtil.createI18NAsIs("text");
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

    private void assertResult(Object expected, final String key) {
        Object actual = officerApplicationPreviewResponse.getModel().get(key);
        assertEquals(expected, actual);
    }
}
