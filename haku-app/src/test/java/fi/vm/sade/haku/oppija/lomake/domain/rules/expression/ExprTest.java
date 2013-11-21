package fi.vm.sade.haku.oppija.lomake.domain.rules.expression;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ExprTest {

    public static final String VALUE = "value";
    private Expr expr;

    @Before
    public void setUp() throws Exception {
        this.expr = new Expr(null, null, VALUE) {
            @Override
            public boolean evaluate(Map<String, String> context) {
                return false;
            }
        };
    }

    @Test
    public void testGetValueContext() {
        assertEquals(VALUE, expr.getValue(null));
    }

    @Test
    public void testGetValue() {
        assertEquals(VALUE, expr.getValue());
    }
}
