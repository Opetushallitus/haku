package fi.vm.sade.oppija.lomake.validation.validators;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import fi.vm.sade.oppija.lomake.domain.I18nText;
import fi.vm.sade.oppija.lomake.validation.ValidationResult;
import fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UniqValuesValidatorTest {

    public static final String ID = "id";
    public static final I18nText MSG_KEY = ElementUtil.createI18NTextError("msg.key");
    private UniqValuesValidator uniqValuesValidator;

    @Before
    public void setUp() throws Exception {
        uniqValuesValidator = new UniqValuesValidator(ID, ImmutableList.of("AI", "BI"), MSG_KEY);
    }

    @Test
    public void testValidateFalse() throws Exception {
        ValidationResult validate = uniqValuesValidator.validate(ImmutableMap.of("AI", "1", "BI", "1"));
        assertTrue(validate.hasErrors());
    }

    @Test
    public void testValidateTrue() throws Exception {
        UniqValuesValidator uniqValuesValidator = new UniqValuesValidator(ID, ImmutableList.of("AI", "BI"), MSG_KEY);
        ValidationResult validate = uniqValuesValidator.validate(ImmutableMap.of("AI", "1", "BI", "2"));
        assertFalse(validate.hasErrors());
    }

    @Test
    public void testValidateFalseNullValues() throws Exception {
        UniqValuesValidator uniqValuesValidator = new UniqValuesValidator(ID, ImmutableList.of("AI", "BI"), MSG_KEY);
        Map<String, String> values = new HashMap<String, String>(1);
        values.put("AI", null);
        values.put("BI", null);
        ValidationResult validate = uniqValuesValidator.validate(values);
        assertFalse(validate.hasErrors());
    }
}
