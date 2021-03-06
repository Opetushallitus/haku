package fi.vm.sade.haku.oppija.lomake.domain;

import com.google.common.collect.ImmutableMap;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

import static com.google.common.collect.ImmutableMap.*;

public class I18nTextTest {

    @Test
    public void testJsonContainsDefaultValuesAsWell() throws IOException {
        final String expectedEnglish = "English";
        Map<String,String> translation = of(
                "en", expectedEnglish);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(new I18nText(translation));
        Assert.assertTrue(
                "{\"translations\":{\"fi\":\"English\",\"sv\":\"English\",\"en\":\"English\"}}".length() <=json.length());

    }
    @Test
    public void testWithSpecialWhiteSpace() {
        String someWhitespace = "\u0020";
        Map<String,String> translation = of(
                "en", someWhitespace);
        Assert.assertEquals(0,new I18nText(translation).size());
    }
    @Test
    public void testFallbackToFinlandWhenMissingText() {
        final String expectedDefault = "Suomea";
        final String expectedEnglish = "English";
        Map<String,String> translation = of(
                "en", expectedEnglish,
                "fi", expectedDefault);

        Assert.assertEquals(expectedDefault, new I18nText(translation).getText("ru"));
        Assert.assertEquals(expectedDefault, new I18nText(translation).getText("sv"));
        Assert.assertEquals(expectedEnglish, new I18nText(translation).getText("en"));
    }
    @Test
    public void testFallbackToFinlandWithEmptyHtml() {
        final String expectedDefault = "<div>Suomea</div>";
        final String notExpectedEnglish = "<div></div>";
        Map<String,String> translation = of(
                "en", expectedDefault,
                "fi", notExpectedEnglish);
        Assert.assertEquals(expectedDefault, new I18nText(translation).getText("fi"));
        Assert.assertEquals(expectedDefault, new I18nText(translation).getText("en"));
    }
}
