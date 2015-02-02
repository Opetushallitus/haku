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
package fi.vm.sade.haku.oppija.lomake.validation.validators;

import fi.vm.sade.haku.oppija.common.koulutusinformaatio.ApplicationOption;
import fi.vm.sade.haku.oppija.common.koulutusinformaatio.ApplicationOptionService;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.validation.GroupRestrictionValidator;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationInput;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationResult;
import fi.vm.sade.haku.oppija.lomake.validation.groupvalidators.GroupRestrictionMaxNumberValidator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.i18n.I18nBundleService;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
/**
 * Unit tests for preference table validator
 *
 * @author Mikko Majapuro
 */
public class PreferenceTableValidatorTest {

    PreferenceTableValidator validator;
    private final Map<String, String> maxErrors = new HashMap<String, String>();
    {
        maxErrors.put("fi", "max error");
    }

    @Before
    public void setUp() {
        List<String> learningInstitutionInputIds = new ArrayList<String>();
        learningInstitutionInputIds.add("li1");
        learningInstitutionInputIds.add("li2");
        learningInstitutionInputIds.add("li3");
        learningInstitutionInputIds.add("li4");
        learningInstitutionInputIds.add("li5");
        List<String> educationInputIds = new ArrayList<String>();
        educationInputIds.add("e1");
        educationInputIds.add("e2");
        educationInputIds.add("e3");
        educationInputIds.add("e4");
        educationInputIds.add("e5");
        List<GroupRestrictionValidator> groupRestrictionValidators = new ArrayList<GroupRestrictionValidator>();
        groupRestrictionValidators.add(new GroupRestrictionMaxNumberValidator("test.max.group", 1, new I18nText(maxErrors)));
        validator = new PreferenceTableValidator(learningInstitutionInputIds, educationInputIds, groupRestrictionValidators);
        ApplicationOptionService aos = mock(ApplicationOptionService.class);
        final ApplicationOption partOfTestGroup = new ApplicationOption();
        partOfTestGroup.setGroups(Arrays.asList("test-group1", "test.max.group"));
        when(aos.get("ao-with-test-group")).thenReturn(partOfTestGroup);
        when(aos.get("ao-with-no-test-group")).thenReturn(new ApplicationOption());
        validator.setApplicationOptionService(aos);
    }


    @Test
    public void testValidateValid() {
        Map<String, String> values = new HashMap<String, String>();
        values.put("li1", "li1");
        values.put("li2", "li2");
        values.put("li3", "li3");
        values.put("li4", "li4");
        values.put("li5", "li5");
        values.put("e1", "e1");
        values.put("e2", "e2");
        values.put("e3", "e3");
        values.put("e4", "e4");
        values.put("e5", "e5");
        values.put("e1-id", "ao-with-no-test-group");
        values.put("e2-id", "ao-with-test-group");
        values.put("e3-id", "ao-with-no-test-group");
        values.put("e4-id", "ao-with-no-test-group");
        values.put("e5-id", "ao-with-no-test-group");
        ValidationResult result = validator.validate(new ValidationInput(null, values, null, null, false));
        assertNotNull(result);
        assertFalse(result.hasErrors());
    }

    @Test
    public void testValidateWhenTooManyWithRestrictedGroup() {
        Map<String, String> values = new HashMap<String, String>();
        values.put("li1", "li1");
        values.put("li2", "li2");
        values.put("li3", "li3");
        values.put("li4", "li4");
        values.put("li5", "li5");
        values.put("e1", "e1");
        values.put("e2", "e2");
        values.put("e3", "e3");
        values.put("e4", "e4");
        values.put("e5", "e5");
        values.put("e1-id", "ao-with-no-test-group");
        values.put("e2-id", "ao-with-test-group");
        values.put("e3-id", "ao-with-no-test-group");
        values.put("e4-id", "ao-with-test-group");
        values.put("e5-id", "ao-with-no-test-group");
        ValidationResult result = validator.validate(new ValidationInput(null, values, null, null, false));
        assertNotNull(result);
        assertTrue(result.hasErrors());
        assertEquals(2, result.getErrorMessages().values().size());
        assertEquals("{fi=max error}", getFirstErrorAsString(result));
    }

    @Test
    public void testValidateNotUniquePreferences() {
        Map<String, String> values = new HashMap<String, String>();
        values.put("li1", "li1");
        values.put("li2", "li1");
        values.put("e1", "e1");
        values.put("e2", "e1");
        ValidationResult result = validator.validate(new ValidationInput(null, values, null, null, false));
        assertNotNull(result);
        assertTrue(result.hasErrors());
        assertEquals(1, result.getErrorMessages().values().size());
        assertTrue(getFirstErrorAsString(result), getFirstErrorAsString(result).contains("Et voi syöttää samaa hakutoivetta useaan kertaan."));
    }

    @Test
    public void testValidateEmptyRows() {
        Map<String, String> values = new HashMap<String, String>();
        values.put("li1", "li1");
        values.put("li2", "li2");
        values.put("li4", "li4");
        values.put("e1", "e1");
        values.put("e2", "e2");
        values.put("e4", "e4");
        ValidationResult result = validator.validate(new ValidationInput(null, values, null, null, false));
        assertNotNull(result);
        assertTrue(result.hasErrors());
        assertEquals(1, result.getErrorMessages().values().size());
        assertTrue(getFirstErrorAsString(result), getFirstErrorAsString(result).contains("Et voi jättää tyhjää hakutoivetta täytettyjen hakutoiveiden väliin."));
    }

    @Test
    public void testValidateEducationValueMissing() {
        Map<String, String> values = new HashMap<String, String>();
        values.put("li1", "li1");
        values.put("li2", "li2");
        values.put("e1", "e1");
        ValidationResult result = validator.validate(new ValidationInput(null, values, null, null, false));
        assertNotNull(result);
        assertTrue(result.hasErrors());
        assertEquals(1, result.getErrorMessages().values().size());
        assertTrue(getFirstErrorAsString(result), getFirstErrorAsString(result).contains("Pakollinen tieto."));
    }

    @Test
    public void testValidateLearningInstituteValueMissing() {
        Map<String, String> values = new HashMap<String, String>();
        values.put("li1", "li1");
        values.put("e1", "e1");
        values.put("e2", "e2");
        ValidationResult result = validator.validate(new ValidationInput(null, values, null, null, false));
        assertNotNull(result);
        assertTrue(result.hasErrors());
        assertEquals(1, result.getErrorMessages().values().size());
        assertTrue(getFirstErrorAsString(result), getFirstErrorAsString(result).contains("Pakollinen tieto."));
    }

    private String getFirstErrorAsString(ValidationResult result) {
        return result.getErrorMessages().values().iterator().next().toString();
    }
}
