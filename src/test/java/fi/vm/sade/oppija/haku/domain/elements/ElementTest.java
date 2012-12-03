package fi.vm.sade.oppija.haku.domain.elements;

import fi.vm.sade.oppija.lomake.domain.Attribute;
import fi.vm.sade.oppija.lomake.domain.elements.Element;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

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


    @Test
    public void testNullKeyAttributeString() throws AssertionError {
        try {
            testElement.addAttribute(null, "v");
            fail();
        } catch (AssertionError e) {
            // expected=AssertionError.class cannot be used here, because junit uses it internally
        }
    }

    @Test
    public void testNullValueAttributeString() {
        try {
            testElement.addAttribute(null, "v");
            fail();
        } catch (AssertionError e) {
            // expected=AssertionError.class cannot be used here, because junit uses it internally
        }
    }

    @Test
    public void testNullKeyAndValueAttributeString() throws Exception {
        try {
            testElement.addAttribute(null, null);
            fail();
        } catch (AssertionError e) {
            // expected=AssertionError.class cannot be used here, because junit uses it internally
        }
    }


    @Test(expected = UnsupportedOperationException.class)
    public void testAddAttributes() throws Exception {
        Collection<Attribute> attributes = testElement.getAttributes().values();
        attributes.add(new Attribute("k", "v"));
    }
}
