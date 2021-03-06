package fi.vm.sade.haku.oppija.lomake.domain.rules.expression;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.HashMap;
import java.util.Map;

public class ValidateEducationExprTest {

    @Test
    public void testBaseEducationIsEnough() {
        ValidateEducationExpr expr = new ValidateEducationExpr("Preference1");
        Map<String, String> answers = new HashMap<String, String>();

        answers.put("Preference1-Koulutus-requiredBaseEducations", "1234,pohjakoulutusvaatimuskorkeakoulut_102");
        answers.put("pohjakoulutus_kk_taso", "2");
        answers.put("pohjakoulutus_kk_nimike", "xxxx");
        answers.put("pohjakoulutus_kk", "true");

        assertFalse(expr.evaluate(answers));
    }

    @Test
    public void testBaseEducationIsNotEnough() {
        ValidateEducationExpr expr = new ValidateEducationExpr("Preference1");
        Map<String, String> answers = new HashMap<String, String>();

        answers.put("Preference1-Koulutus-requiredBaseEducations", "1234");
        answers.put("pohjakoulutus_kk_taso", "2");
        answers.put("pohjakoulutus_kk_nimike", "xxxx");
        answers.put("pohjakoulutus_kk", "true");

        assertTrue(expr.evaluate(answers));

        answers = new HashMap<String, String>();

        answers.put("Preference1-Koulutus-requiredBaseEducations", "pohjakoulutusvaatimuskorkeakoulut_101,pohjakoulutusvaatimuskorkeakoulut_102,pohjakoulutusvaatimuskorkeakoulut_116");
        answers.put("pohjakoulutus_yo_vuosi", "2015");
        answers.put("pohjakoulutus_yo_tutkinto", "fi");
        answers.put("pohjakoulutus_yo", "true");

        assertTrue(expr.evaluate(answers));


    }


}
