package fi.vm.sade.haku.oppija.lomake.validation.validators;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.TextQuestion;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationInput;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationResult;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UniqValuesValidatorTest {

    public static final String ID = "id";
    public static final I18nText MSG_KEY = ElementUtil.createI18NText("msg.key");
    private static final Element element = new TextQuestion(ID, ElementUtil.createI18NAsIs(ID));
    private UniqValuesValidator uniqValuesValidator;

    @Before
    public void setUp() throws Exception {
        uniqValuesValidator = new UniqValuesValidator(ImmutableList.of("AI", "BI"), Collections.EMPTY_LIST, MSG_KEY);
    }

    @Test
    public void testValidateFalse() throws Exception {
        ValidationResult validate = uniqValuesValidator.validate(new ValidationInput(element, ImmutableMap.of("AI", "1", "BI", "1"),
                null, null, false));
        assertTrue(validate.hasErrors());
    }

    @Test
    public void testValidateTrue() throws Exception {
        ValidationResult validate = uniqValuesValidator.validate(new ValidationInput(element, ImmutableMap.of("AI", "1", "BI", "2"),
                null, null, false));
        assertFalse(validate.hasErrors());
    }

    @Test
    public void testValidateFalseNullValues() throws Exception {
        Map<String, String> values = new HashMap<String, String>(1);
        values.put("AI", "a");
        values.put("BI", "b");
        ValidationResult validate = uniqValuesValidator.validate(new ValidationInput(element, values, null, null, false));
        assertFalse(validate.hasErrors());
    }
}
