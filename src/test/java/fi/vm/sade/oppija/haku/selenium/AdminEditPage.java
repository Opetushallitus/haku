package fi.vm.sade.oppija.haku.selenium;

import com.thoughtworks.selenium.Selenium;
import fi.vm.sade.oppija.haku.converter.FormModelToJsonString;
import fi.vm.sade.oppija.haku.domain.FormModel;

/**
 * @author jukka
 * @version 10/15/123:47 PM}
 * @since 1.1
 */
public class AdminEditPage extends LoginPage implements PageObject {
    private final String baseUrl;
    private final Selenium selenium;

    public AdminEditPage(String baseUrl, Selenium selenium) {
        super(selenium);
        this.baseUrl = baseUrl;
        this.selenium = selenium;
    }


    @Override
    public String getUrl() {
        return baseUrl + "/admin/edit";
    }


    public void submitForm(FormModel formModel1) {
        final String convert = new FormModelToJsonString().convert(formModel1);
        selenium.type("model", convert);
        selenium.submit("tallenna");
    }
}
