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
import fi.vm.sade.haku.oppija.common.koulutusinformaatio.ApplicationOptionGroup;
import fi.vm.sade.haku.oppija.common.koulutusinformaatio.ApplicationOptionService;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystemBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.PreferenceRow;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.PreferenceTable;
import fi.vm.sade.haku.oppija.lomake.validation.GroupRestrictionValidator;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationInput;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationResult;
import fi.vm.sade.haku.oppija.lomake.validation.groupvalidators.GroupPrioritisationValidator;
import fi.vm.sade.haku.oppija.lomake.validation.groupvalidators.GroupRestrictionMaxNumberValidator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.i18n.I18nBundleService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
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
    private final Map<String, String> prioErrors = new HashMap<String, String>();
    {
        prioErrors.put("fi", "prio error: korkeampi={hakukohde_korkeampi}, alempi={hakukohde_alempi}");
    }

    @Before
    public void setUp() {
        //NOTE: Hacking around I18nBundleService - ApplicationSystemService dependency
        I18nBundleService i18nBundleService = spy(new I18nBundleService(null));
        ApplicationSystem synth = new ApplicationSystemBuilder().setId("haku").setName(ElementUtil.createI18NAsIs("haku")).setHakukausiUri(OppijaConstants.HAKUKAUSI_KEVAT).setApplicationSystemType(OppijaConstants.HAKUTYYPPI_VARSINAINEN_HAKU).setHakutapa(
          OppijaConstants.HAKUTAPA_YHTEISHAKU).get();
        doReturn(i18nBundleService.getBundle(synth)).when(i18nBundleService).getBundle((String) isNull());

        List<GroupRestrictionValidator> groupRestrictionValidators = new ArrayList<GroupRestrictionValidator>();
        groupRestrictionValidators.add(new GroupRestrictionMaxNumberValidator("test.max.group", 2, new I18nText(maxErrors)));
        groupRestrictionValidators.add(new GroupPrioritisationValidator("test-group1", new I18nText(prioErrors)));

        final PreferenceTable table = new PreferenceTable("tableid", null, false, 1000000);
        table.setGroupRestrictionValidators(groupRestrictionValidators);
        for (int i = 1; i <= 5; i++) {
            table.addChild(new PreferenceRow("preference" + i, null, null, null, null, null));
        }

        validator = new PreferenceTableValidator(table);
        validator.setI18nBundleService(i18nBundleService);
        ApplicationOptionService aos = mock(ApplicationOptionService.class);
        final ApplicationOption partOfTestGroup = new ApplicationOption();
        partOfTestGroup.setName("korkeampi prio");
        partOfTestGroup.setGroups(Arrays.asList(new ApplicationOptionGroup("test-group1" , 1), new ApplicationOptionGroup("test.max.group", null)));
        when(aos.get("ao-with-test-group")).thenReturn(partOfTestGroup);
        final ApplicationOption notPartOfTestGroup = new ApplicationOption();
        notPartOfTestGroup.setName("alempi prio");
        notPartOfTestGroup.setGroups(Arrays.asList(new ApplicationOptionGroup("test-group1", null)));
        when(aos.get("ao-with-no-test-group")).thenReturn(notPartOfTestGroup);
        validator.setApplicationOptionService(aos);
    }


    @Test
    public void testValidateValid() {
        Map<String, String> values = new HashMap<String, String>();
        values.put("preference1-Opetuspiste", "li1");
        values.put("preference2-Opetuspiste", "li2");
        values.put("preference3-Opetuspiste", "li3");
        values.put("preference4-Opetuspiste", "li4");
        values.put("preference5-Opetuspiste", "li5");
        values.put("preference1-Koulutus", "e1");
        values.put("preference2-Koulutus", "e2");
        values.put("preference3-Koulutus", "e3");
        values.put("preference4-Koulutus", "e4");
        values.put("preference5-Koulutus", "e5");
        values.put("preference1-Koulutus-id", "ao-with-test-group");
        values.put("preference2-Koulutus-id", "ao-with-test-group");
        values.put("preference3-Koulutus-id", "ao-with-no-test-group");
        values.put("preference4-Koulutus-id", "ao-with-no-test-group");
        values.put("preference5-Koulutus-id", "ao-with-no-test-group");
        ValidationResult result = validator.validate(new ValidationInput(null, values, null, null, ValidationInput.ValidationContext.officer_modify));
        assertNotNull(result);
        assertFalse(result.hasErrors());
    }

    @Test
    public void testValidateWhenTooManyWithRestrictedGroup() {
        Map<String, String> values = new HashMap<String, String>();
        values.put("preference1-Opetuspiste", "li1");
        values.put("preference2-Opetuspiste", "li2");
        values.put("preference3-Opetuspiste", "li3");
        values.put("preference4-Opetuspiste", "li4");
        values.put("preference5-Opetuspiste", "li5");
        values.put("preference1-Koulutus", "e1");
        values.put("preference2-Koulutus", "e2");
        values.put("preference3-Koulutus", "e3");
        values.put("preference4-Koulutus", "e4");
        values.put("preference5-Koulutus", "e5");
        values.put("preference1-Koulutus-id", "ao-with-test-group");
        values.put("preference2-Koulutus-id", "ao-with-test-group");
        values.put("preference3-Koulutus-id", "ao-with-test-group");
        values.put("preference4-Koulutus-id", "ao-with-no-test-group");
        values.put("preference5-Koulutus-id", "ao-with-no-test-group");
        ValidationResult result = validator.validate(new ValidationInput(null, values, null, null, ValidationInput.ValidationContext.officer_modify));
        assertNotNull(result);
        assertTrue(result.hasErrors());
        assertEquals(3, result.getErrorMessages().values().size());
        assertEquals(new HashSet<>(Arrays.asList("preference1-Koulutus", "preference2-Koulutus", "preference3-Koulutus")), result.getErrorMessages().keySet());
        assertEquals("{fi=max error}", getFirstErrorAsString(result));
    }

    @Test
    public void testValidateWrongOrder() {
        Map<String, String> values = new HashMap<String, String>();
        values.put("preference1-Opetuspiste", "li1");
        values.put("preference2-Opetuspiste", "li2");
        values.put("preference3-Opetuspiste", "li3");
        values.put("preference4-Opetuspiste", "li4");
        values.put("preference5-Opetuspiste", "li5");
        values.put("preference1-Koulutus", "e1");
        values.put("preference2-Koulutus", "e2");
        values.put("preference3-Koulutus", "e3");
        values.put("preference4-Koulutus", "e4");
        values.put("preference5-Koulutus", "e5");
        values.put("preference1-Koulutus-id", "ao-with-test-group");
        values.put("preference2-Koulutus-id", "ao-with-no-test-group");
        values.put("preference3-Koulutus-id", "ao-with-test-group");
        values.put("preference4-Koulutus-id", "ao-with-no-test-group");
        values.put("preference5-Koulutus-id", "ao-with-no-test-group");
        ValidationResult result = validator.validate(new ValidationInput(null, values, null, null, ValidationInput.ValidationContext.officer_modify));
        assertNotNull(result);
        assertTrue(result.hasErrors());
        assertEquals(2, result.getErrorMessages().values().size());
        assertEquals(new HashSet<>(Arrays.asList("preference2-Koulutus", "preference3-Koulutus")), result.getErrorMessages().keySet());
        assertEquals("{fi=prio error: korkeampi=korkeampi prio, alempi=alempi prio}", getFirstErrorAsString(result));
    }

    @Test
    public void testValidateNotUniquePreferences() {
        Map<String, String> values = new HashMap<String, String>();
        values.put("preference1-Opetuspiste", "li1");
        values.put("preference2-Opetuspiste", "li1");
        values.put("preference1-Koulutus", "e1");
        values.put("preference2-Koulutus", "e1");
        ValidationResult result = validator.validate(new ValidationInput(null, values, null, null, ValidationInput.ValidationContext.officer_modify));
        assertNotNull(result);
        assertTrue(result.hasErrors());
        assertEquals(1, result.getErrorMessages().values().size());
        assertTrue(getFirstErrorAsString(result), getFirstErrorAsString(result).contains("Et voi syöttää samaa hakutoivetta useaan kertaan."));
    }

    @Test
    public void testValidateEmptyRows() {
        Map<String, String> values = new HashMap<String, String>();
        values.put("preference1-Opetuspiste", "li1");
        values.put("preference2-Opetuspiste", "li2");
        values.put("preference4-Opetuspiste", "li4");
        values.put("preference1-Koulutus", "e1");
        values.put("preference2-Koulutus", "e2");
        values.put("preference4-Koulutus", "e4");
        ValidationResult result = validator.validate(new ValidationInput(null, values, null, null, ValidationInput.ValidationContext.officer_modify));
        assertNotNull(result);
        assertTrue(result.hasErrors());
        assertEquals(1, result.getErrorMessages().values().size());
        assertTrue(getFirstErrorAsString(result), getFirstErrorAsString(result).contains("Et voi jättää tyhjää hakutoivetta täytettyjen hakutoiveiden väliin."));
    }

    @Test
    public void testValidateEducationValueMissing() {
        Map<String, String> values = new HashMap<String, String>();
        values.put("preference1-Opetuspiste", "li1");
        values.put("preference2-Opetuspiste", "li2");
        values.put("preference1-Koulutus", "e1");
        ValidationResult result = validator.validate(new ValidationInput(null, values, null, null, ValidationInput.ValidationContext.officer_modify));
        assertNotNull(result);
        assertTrue(result.hasErrors());
        assertEquals(1, result.getErrorMessages().values().size());
        assertTrue(getFirstErrorAsString(result), getFirstErrorAsString(result).contains("Pakollinen tieto."));
    }

    @Test
    public void testValidateLearningInstituteValueMissing() {
        Map<String, String> values = new HashMap<String, String>();
        values.put("preference1-Opetuspiste", "li1");
        values.put("preference1-Koulutus", "e1");
        values.put("preference2-Koulutus", "e2");
        ValidationResult result = validator.validate(new ValidationInput(null, values, null, null, ValidationInput.ValidationContext.officer_modify));
        assertNotNull(result);
        assertTrue(result.hasErrors());
        assertEquals(1, result.getErrorMessages().values().size());
        assertTrue(getFirstErrorAsString(result), getFirstErrorAsString(result).contains("Pakollinen tieto."));
    }

    private String getFirstErrorAsString(ValidationResult result) {
        return result.getErrorMessages().values().iterator().next().toString();
    }
}
