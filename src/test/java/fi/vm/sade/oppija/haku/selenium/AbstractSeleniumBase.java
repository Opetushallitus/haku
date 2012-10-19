package fi.vm.sade.oppija.haku.selenium;

import fi.vm.sade.oppija.haku.FormModelHelper;
import fi.vm.sade.oppija.haku.SeleniumContainer;
import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.it.TomcatContainerBase;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author jukka
 * @version 10/15/121:13 PM}
 * @since 1.1
 */
public abstract class AbstractSeleniumBase extends TomcatContainerBase {

    protected SeleniumHelper seleniumHelper;

    @Autowired
    SeleniumContainer container;

    public AbstractSeleniumBase() {
        super();
    }

    @Before
    public void before() {
        seleniumHelper = container.getSeleniumHelper();
    }

    protected FormModelHelper initModel(FormModel formModel1) {

        final AdminEditPage adminEditPage = new AdminEditPage(getBaseUrl(), seleniumHelper.getSelenium());
        seleniumHelper.navigate(adminEditPage);
        adminEditPage.login("admin");
        seleniumHelper.navigate(adminEditPage);
        adminEditPage.submitForm(formModel1);
        logout();
        return new FormModelHelper(formModel1);
    }

    @After
    public void logout() {
        seleniumHelper.logout();
    }


}
