package fi.vm.sade.oppija.ui.common;

import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.assertEquals;

public class UriUtilTest {

    @Test(expected = NullPointerException.class)
    public void testPathSegmentsToUri() throws Exception {
        UriUtil.pathSegmentsToUri(null);
    }

    @Test
    public void testPathSegmentsToUriEmpty() throws Exception {
        URI uri = UriUtil.pathSegmentsToUri("");
        assertEquals("", uri.toString());
    }

    @Test
    public void testPathSegmentsToUriOneSegment() throws Exception {
        URI uri = UriUtil.pathSegmentsToUri("seg");
        assertEquals("seg", uri.toString());
    }

    @Test
    public void testPathSegmentsToUriTwoSegments() throws Exception {
        URI uri = UriUtil.pathSegmentsToUri("seg1", "seg2");
        assertEquals("seg1/seg2", uri.toString());
    }

    @Test
    public void testPathSegmentsToUriSkipNulls() throws Exception {
        URI uri = UriUtil.pathSegmentsToUri("seg1", null , "seg2");
        assertEquals("seg1/seg2", uri.toString());
    }
}
