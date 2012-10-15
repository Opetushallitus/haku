package fi.vm.sade.oppija.haku.selenium;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author jukka
 * @version 10/15/123:25 PM}
 * @since 1.1
 */
public class HautKoulutuksiinTest extends AbstractSeleniumTest {

    @Test
    public void testLogin() {
        new HautKoulutuksiinPage(getBaseUrl(), seleniumHelper).login();
        assertTrue(seleniumHelper.getSelenium().isTextPresent("Ajankohtaiset hakemukset"));
    }

}
