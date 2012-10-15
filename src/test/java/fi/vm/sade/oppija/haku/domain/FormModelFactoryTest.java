package fi.vm.sade.oppija.haku.domain;

import fi.vm.sade.oppija.haku.FormModelHelper;
import fi.vm.sade.oppija.haku.domain.builders.FormModelBuilder;
import fi.vm.sade.oppija.haku.domain.elements.Vaihe;
import fi.vm.sade.oppija.haku.domain.questions.TextQuestion;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author jukka
 * @version 9/14/121:50 PM}
 * @since 1.1
 */
public class FormModelFactoryTest {
    @Test
    public void testFromJSONString() throws Exception {
        final FormModel formModel = FormModelFactory.fromClassPathResource("test-data.json");
        assertEquals("yhteishaku", formModel.getApplicationPeriodById("test").getFormById("yhteishaku").getTitle());
    }

    @Test
    public void testBuilderFrom() throws Exception {
        final FormModel formModel = new FormModelBuilder().withDefaults().addChildToCategory(new TextQuestion("doo", "foo")).build();
        final FormModelHelper formModelHelper = new FormModelHelper(formModel);
        assertEquals("doo", formModelHelper.getFirstCategoryChild().getId());
    }

    @Test
    public void testBuilderWithCategory() throws Exception {
        final Vaihe vaihe = new Vaihe("ekaKategoria", "ensimmäinen kategoria");
        final FormModel formModel = new FormModelBuilder(vaihe).withDefaults().addChildToCategory(new TextQuestion("doo", "foo")).build();
        assertEquals("doo", vaihe.getChildren().get(0).getId());
    }
}
