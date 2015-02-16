package fi.vm.sade.haku.virkailija.lomakkeenhallinta.util;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;

import org.junit.Test;

import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.Value;

public class ExprUtilTest {
    @Test
    public void testEquals() throws Exception {
        assertTrue(ExprUtil.equals("var", "value").evaluate(new HashMap<String, String>() {{
            put("var", "value");
        }}));
        assertFalse(ExprUtil.equals("var", "value").evaluate(new HashMap<String, String>() {{
            put("var", "not the value");
        }}));
    }

    @Test
    public void testAtLeastOneVariableEqualsToValue() throws Exception {
        assertTrue(ExprUtil.atLeastOneValueEqualsToVariable("var", "v1", "v2", "v3").evaluate(new HashMap<String, String>() {{
            put("var", "v2");
        }}));
        assertFalse(ExprUtil.atLeastOneValueEqualsToVariable("var", "v1", "v2", "v3").evaluate(new HashMap<String, String>() {{
            put("var", "v5");
        }}));
    }

    @Test
    public void testAtLeastOneVariableContainsValue() throws Exception {
        assertTrue(ExprUtil.atLeastOneVariableContainsValue("value", "var1", "var2", "var3").evaluate(new HashMap<String, String>() {{
            put("var1", "value");
        }}));
        assertFalse(ExprUtil.atLeastOneVariableContainsValue("value", "var1", "var2", "var3").evaluate(new HashMap<String, String>() {{
            put("var1", "v5");
        }}));
    }

    @Test
    public void testAtLeastOneValueEqualsToVariable() throws Exception {
        assertTrue(ExprUtil.atLeastOneVariableEqualsToValue("value", "var1", "var2", "var3").evaluate(new HashMap<String, String>() {{
            put("var1", "value");
        }}));
        assertFalse(ExprUtil.atLeastOneVariableEqualsToValue("value", "var1", "var2", "var3").evaluate(new HashMap<String, String>() {{
            put("var1", "not the value");
        }}));
    }

    @Test
    public void testReduceToOr() throws Exception {
        assertTrue(ExprUtil.any(Arrays.asList(new Value("false"), new Value("false"), new Value("true"))).evaluate(new HashMap<String, String>() {{}}));
        assertFalse(ExprUtil.any(Arrays.asList(new Value("false"), new Value("false"), new Value("false"))).evaluate(new HashMap<String, String>() {{}}));
    }

    @Test
    public void testIsAnswerTrue() throws Exception {
        assertTrue(ExprUtil.isAnswerTrue("answer1").evaluate(new HashMap<String, String>() {{
            put("answer1", "true");
        }}));
        assertFalse(ExprUtil.isAnswerTrue("answer1").evaluate(new HashMap<String, String>() {{
            put("answer1", "not true");
        }}));
    }

    @Test
    public void testLessThanRule() throws Exception {
        assertTrue(ExprUtil.lessThanRule("answer1", "10").evaluate(new HashMap<String, String>() {{
            put("answer1", "9");
        }}));
        assertFalse(ExprUtil.lessThanRule("answer1", "8").evaluate(new HashMap<String, String>() {{
            put("answer1", "9");
        }}));
    }
}