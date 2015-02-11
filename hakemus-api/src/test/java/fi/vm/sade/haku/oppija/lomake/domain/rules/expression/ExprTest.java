package fi.vm.sade.haku.oppija.lomake.domain.rules.expression;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ExprTest {

    public static final String VALUE = "value";
    private Expr expr;

    @Before
    public void setUp() throws Exception {
        this.expr = new Expr() {
            @Override
            public boolean evaluate(Map<String, String> context) {
                return false;
            }

            @Override
            public List<Expr> children() {
                return Collections.EMPTY_LIST;
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
