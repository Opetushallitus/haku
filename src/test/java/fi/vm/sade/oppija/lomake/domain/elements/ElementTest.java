/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */

package fi.vm.sade.oppija.lomake.domain.elements;

import fi.vm.sade.oppija.lomake.domain.Attribute;
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
    public void testDefaultAttributeString() throws Exception {
        assertEquals("id=\"test\" ", testElement.getAttributeString());
    }

    @Test
    public void testOneAttributeString() throws Exception {
        String defaultAttributes = testElement.getAttributeString();
        testElement.addAttribute("k", "v");
        assertEquals(defaultAttributes + "k=\"v\" ", testElement.getAttributeString());
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
