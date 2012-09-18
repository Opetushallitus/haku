package fi.vm.sade.oppija.haku.domain.elements;

import fi.vm.sade.oppija.haku.domain.Attribute;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ElementTest {
    final Element testElement;

    public ElementTest() {
        testElement = new Element("test") {
        };
    }

    @Test
    public void testEmptyAttributeString() throws Exception {
        assertEquals("", testElement.getAttributeString());
    }

    @Test
    public void testOneAttributeString() throws Exception {
        testElement.addAttribute("k", "v");
        assertEquals("k=\"v\" ", testElement.getAttributeString());
    }

    @Test
    public void testTwoAttributeString() throws Exception {
        testElement.addAttribute("k1", "v1");
        testElement.addAttribute("k2", "v2");
        String actual = testElement.getAttributeString();
        assertTrue(actual.contains("k1=\"v1\" ") && actual.contains("k2=\"v2\" "));
    }

    @Test(expected = NullPointerException.class)
    public void testNullKeyAttributeString() throws Exception {
        testElement.addAttribute(null, "v");
    }

    @Test(expected = NullPointerException.class)
    public void testNullValueAttributeString() throws Exception {
        testElement.addAttribute(null, "v");
    }

    @Test(expected = NullPointerException.class)
    public void testNullKeyAndValueAttributeString() throws Exception {
        testElement.addAttribute(null, null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testClearAttributeSet() throws Exception {
        Set<Attribute> attributes = testElement.getAttributes();
        attributes.clear();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddAttributes() throws Exception {
        Set<Attribute> attributes = testElement.getAttributes();
        attributes.add(new Attribute("k", "v"));
    }
}
