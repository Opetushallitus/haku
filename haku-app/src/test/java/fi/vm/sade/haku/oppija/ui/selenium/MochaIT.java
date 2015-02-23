package fi.vm.sade.haku.oppija.ui.selenium;

import fi.vm.sade.haku.oppija.common.selenium.AbstractSeleniumBase;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class MochaIT extends AbstractSeleniumBase {

    protected WebDriver driver;

    @Before
    public void init() {
        driver = seleniumContainer.getDriver();
        driver.get(getBaseUrl() + "test/index.html");
    }

    @Test
    public void testRunMocha() throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        new WebDriverWait(seleniumContainer.getDriver(), 10*60, 1000)
                .until(new ExpectedCondition<Object>() {
                    @Override
                    public Object apply(WebDriver webDriver) {
                        JavascriptExecutor executor = (JavascriptExecutor) webDriver;
                        return executor.executeScript("return runResults.allDone");
                    }
                });
        Long failed  = (Long) js.executeScript("return runResults.failed");
        Long passed  = (Long) js.executeScript("return runResults.passed");
        Long pending = (Long) js.executeScript("return runResults.pending");
        Object result = js.executeScript("return runResults");
        System.out.println("\n\n--------------------------------Mocha testing results start-------------------------------------------------------------");
        System.out.println("Summary, Failed: " + failed + " Passed: " + passed + " Pending: " + pending);
        System.out.println("Details:");
        System.out.println(result);
        System.out.println("--------------------------------Mocha testing results end---------------------------------------------------------------\n\n");

        if (failed > 0) {
            throw new RuntimeException("Mocha tests failed, see log for details!");
        }
    }
}
