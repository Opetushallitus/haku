package fi.vm.sade.oppija.haku.it;

import fi.vm.sade.oppija.haku.FormModelHelper;
import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.domain.builders.FormModelBuilder;
import fi.vm.sade.oppija.haku.domain.questions.Option;
import fi.vm.sade.oppija.haku.domain.questions.Radio;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

/**
 * @author jukka
 * @version 9/18/121:44 PM}
 * @since 1.1
 */
public class RadioGroupIT extends AbstractRemoteTest {

    private FormModelHelper formModelHelper;
    private Radio radio;

    @Before
    public void init() throws IOException {
        radio = new Radio("radio", "foo");
        radio.addOption("value1", "radio", "title");
        radio.addOption("value2", "radio2", "title2");
        FormModel formModel = new FormModelBuilder().buildDefaultFormWithFields(radio);
        this.formModelHelper = initModel(formModel);
    }

    @Test
    public void testInputExists() throws IOException {
        final String startUrl = formModelHelper.getStartUrl();
        beginAt(startUrl);
        List<Option> options = radio.getOptions();
        for (Option option : options) {
            assertElementPresentByXPath("//input[@value='" + option.getValue() + "']");
        }
    }
}
