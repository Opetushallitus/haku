package fi.vm.sade.oppija.haku.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author jukka
 * @version 10/8/129:52 AM}
 * @since 1.1
 */
public class HakemusIdTest {

    private static final String ID = "foo_foo_foo_foo";

    @Test
    public void testHakemusId() {
        final String foo = "foo";
        final HakemusId hakemusId = new HakemusId(foo, foo, foo, foo);
        assertEquals(hakemusId.asKey(), ID);
    }

    @Test
    public void testHakemusIdFromString() {
        final HakemusId hakemusId = HakemusId.fromKey(ID);
        assertEquals(hakemusId.asKey(), ID);
    }

    @Test
    public void testEquals() {
        final HakemusId hakemusId = HakemusId.fromKey(ID);
        final HakemusId hakemusId2 = HakemusId.fromKey("foo_foo_foo_foo");
        assertEquals(hakemusId, hakemusId2);
    }
}
