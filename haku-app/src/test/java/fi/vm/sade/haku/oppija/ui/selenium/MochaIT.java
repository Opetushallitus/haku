package fi.vm.sade.haku.oppija.ui.selenium;

import fi.vm.sade.haku.oppija.common.selenium.AbstractSeleniumBase;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

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
        Object result = js.executeScript("return runResults");
        System.out.println("\n\n--------------------------------Mocha testing results start-------------------------------------------------------------");
        System.out.println(getPrettyPrintResult("", result));
        System.out.println("--------------------------------Mocha testing results end---------------------------------------------------------------\n\n");

        Long failed = (Long) js.executeScript("return runResults.failed");
        if (failed > 0) {
            throw new RuntimeException(failed + " Mocha tests failed, see log for details!");
        }
    }

    private String getPrettyPrintResult(String prefix, Object result) {
        if(result instanceof Map) {
            return getPrettyPrintResult(prefix, (Map) result);
        }
        if(result instanceof Collection) {
            return getPrettyPrintResult(prefix, (Collection)result);
        }
        return prefix + result;
    }

    private String getPrettyPrintResult(String prefix, Map result) {
        StringBuilder rString = new StringBuilder();
        Iterator<?> keys = result.keySet().iterator();
        while(keys.hasNext()) {
            Object key = keys.next();
            rString.append(prefix);
            rString.append(key);
            rString.append(":\n");
            rString.append(getPrettyPrintResult(prefix + "  ", result.get(key)));
            if(keys.hasNext()) {
                rString.append("\n");
            }
        }
        return rString.toString();
    }

    private String getPrettyPrintResult(String prefix, Collection result) {
        StringBuilder rString = new StringBuilder();
        Iterator<?> iterator = result.iterator();
        while(iterator.hasNext()) {
            rString.append(getPrettyPrintResult(prefix, iterator.next()));
            if(iterator.hasNext()) {
                rString.append("\n");
            }
        }
        return rString.toString();
    }
}
