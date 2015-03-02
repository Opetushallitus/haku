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

package fi.vm.sade.haku.oppija.lomake.domain.elements;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class ElementTest {

    public static final String ID = "test";
    private Element testElement;

    @Before
    public void setUp() throws Exception {
        testElement = new Element(ID) {
            private static final long serialVersionUID = 4741463711894448833L;
        };
    }

    @Test
    public void testDefaultAttributeString() throws Exception {
        assertEquals("", testElement.getAttributeString());
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


    @Test(expected = NullPointerException.class)
    public void testNullKeyAttributeString() throws AssertionError {
        testElement.addAttribute(null, "v");
    }

    @Test(expected = NullPointerException.class)
    public void testNullValueAttributeString() {
        testElement.addAttribute("v", null);
    }

    @Test(expected = NullPointerException.class)
    public void testNullKeyAndValueAttributeString() throws Exception {
        testElement.addAttribute(null, null);
    }

    @Test
    public void testEqualsToNull() throws Exception {
        assertFalse(testElement.equals(null));
    }

    @Test
    public void testEqualsDifferentClass() throws Exception {
        assertFalse(testElement.equals(""));
    }

    @Test
    public void testEqualsSame() throws Exception {
        assertTrue(testElement.equals(testElement));
    }

    @Test
    public void testEqualsTrue() throws Exception {
        assertTrue(new TestElement(ID).equals(new TestElement(ID)));
    }

    @Test
    public void testEqualsFalse() throws Exception {
        assertFalse(new TestElement(ID).equals(new TestElement(ID + "1")));
    }

    private class TestElement extends Element {
        private static final long serialVersionUID = 5232304386159753660L;

        protected TestElement(final String id) {
            super(id);
        }
    }
}
