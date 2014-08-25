package fi.vm.sade.haku.oppija.lomake.validation.validators;

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.TextQuestion;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationInput;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationResult;
import fi.vm.sade.haku.oppija.lomake.validation.Validator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class YearValidatorTest {

    public static final String FIELD_NAME = "fieldname";
    public static final I18nText TEXT = ElementUtil.createI18NAsIs("text");
    public static final Element question = new TextQuestion(FIELD_NAME, TEXT);

    public static final Map<String, String> year1984 = ImmutableMap.of(FIELD_NAME, "1984");
    public static final Map<String, String> yearEmpty = ImmutableMap.of(FIELD_NAME, "");
    public static final Map<String, String> yearNaN = ImmutableMap.of(FIELD_NAME, "one");

    public FormParameters formParameters;

    @Before
    public void setUp() {
        formParameters = Mockito.mock(FormParameters.class);
        when(formParameters.getI18nText(any(String.class))).thenReturn(new I18nText(new HashMap<String, String>()));
    }

    @Test
    public void testYearValidator1984() throws Exception {
        assertFalse(validationResult(new YearValidator(formParameters, null, null, true), year1984).hasErrors());
        assertFalse(validationResult(new YearValidator(formParameters, null, null, false), year1984).hasErrors());
        assertFalse(validationResult(new YearValidator(formParameters, 1983, 1985, false), year1984).hasErrors());
        assertFalse(validationResult(new YearValidator(formParameters, 1984, 1984, false), year1984).hasErrors());
        assertFalse(validationResult(new YearValidator(formParameters, 1900, 2000, false), year1984).hasErrors());
        assertTrue(validationResult(new YearValidator(formParameters, 1900, 1983, false), year1984).hasErrors());
        assertTrue(validationResult(new YearValidator(formParameters, 1985, 2000, false), year1984).hasErrors());
    }

    @Test
    public void testYearValidatorEmpty() throws Exception {
        assertFalse(validationResult(new YearValidator(formParameters, null, null, true), yearEmpty).hasErrors());
        assertTrue(validationResult(new YearValidator(formParameters, null, null, false), yearEmpty).hasErrors());
        assertTrue(validationResult(new YearValidator(formParameters, 1983, 1985, false), yearEmpty).hasErrors());
        assertFalse(validationResult(new YearValidator(formParameters, 1984, 1984, true), yearEmpty).hasErrors());
    }

    @Test
    public void testYearValidatorNaN() throws Exception {
        assertTrue(validationResult(new YearValidator(formParameters, null, null, true), yearNaN).hasErrors());
        assertTrue(validationResult(new YearValidator(formParameters, null, null, false), yearNaN).hasErrors());
        assertTrue(validationResult(new YearValidator(formParameters, 1983, 1985, false), yearNaN).hasErrors());
        assertTrue(validationResult(new YearValidator(formParameters, 1984, 1984, true), yearNaN).hasErrors());
    }

    private ValidationResult validationResult(Validator validator, Map<String, String> input) {
        return validator.validate(new ValidationInput(question, input, null, null));
    }
}
