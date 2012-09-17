package fi.vm.sade.oppija.haku.domain;

import fi.vm.sade.oppija.haku.domain.builders.FormModelBuilder;
import fi.vm.sade.oppija.haku.domain.elements.Category;
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
        final FormModel formModel = new FormModelBuilder().withDefaults().addChildToCategory(new TextQuestion("doo", "foo", "foo")).build();
        assertEquals("doo", formModel.getApplicationPeriodById("test").getFormById("test").getFirstCategory().getChildren().get(0).getId());
    }

    @Test
    public void testBuilderWithCategory() throws Exception {
        final Category category = new Category("ekaKategoria", "ensimmäinen kategoria");
        final FormModel formModel = new FormModelBuilder(category).withDefaults().addChildToCategory(new TextQuestion("doo", "foo", "foo")).build();
        assertEquals("doo", category.getChildren().get(0).getId());
    }
}
