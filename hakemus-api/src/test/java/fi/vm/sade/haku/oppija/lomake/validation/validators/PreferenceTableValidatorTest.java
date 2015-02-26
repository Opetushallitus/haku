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
import org.mockito.Mockito;

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
    private static class TestInput {
        public final String koulutusNimi;
        public final String koulutusId;
        public final String opetuspisteNimi;
        public final String opetuspisteId;

        public TestInput(int number) {
            this("koulutus" + number, "koulutus-id-" + number, "opetuspiste" + number, "opetuspiste-id-" + number);
        }
        public TestInput(final String koulutusNimi, final String koulutusId, final String opetuspisteNimi, final String opetuspisteId) {
            this.koulutusNimi = koulutusNimi;
            this.koulutusId = koulutusId;
            this.opetuspisteNimi = opetuspisteNimi;
            this.opetuspisteId = opetuspisteId;
        }
    }
    final Map<String, String> maxErrors = new HashMap<String, String>() {{
        put("fi", "max error");
    }};
    final Map<String, String> prioErrors = new HashMap<String, String>() {{
        put("fi", "prio error: korkeampi={hakukohde_korkeampi}, alempi={hakukohde_alempi}");
    }};
    final ApplicationSystem applicationSystem = new ApplicationSystemBuilder().setId("haku").setName(ElementUtil.createI18NAsIs("haku")).setHakukausiUri(OppijaConstants.HAKUKAUSI_KEVAT).setApplicationSystemType(OppijaConstants.HAKUTYYPPI_VARSINAINEN_HAKU).setHakutapa(
        OppijaConstants.HAKUTAPA_YHTEISHAKU).get();

    final String limitedGroup = "test.max.group";
    final String priorizationGroup = "test-group1";

    ApplicationOptionService aos = mock(ApplicationOptionService.class);

    final PreferenceTable table = new PreferenceTable("tableid", null, false, 1000000) {{
        setGroupRestrictionValidators(Arrays.asList(new GroupRestrictionMaxNumberValidator(limitedGroup, 2, new I18nText(maxErrors)), new GroupPrioritisationValidator(priorizationGroup, new I18nText(prioErrors))));
        for (int i = 1; i <= 5; i++) {
            addChild(new PreferenceRow("preference" + i, null, null, null, null, null));
        }
    }};

    PreferenceTableValidator validator = new PreferenceTableValidator(table) {{
        I18nBundleService i18nBundleService = spy(new I18nBundleService(null));
        doReturn(i18nBundleService.getBundle(applicationSystem)).when(i18nBundleService).getBundle((String) isNull());
        setI18nBundleService(i18nBundleService);
        setApplicationOptionService(aos);
    }};

    final ApplicationOption partOfTestGroup = new ApplicationOption() {{
        setName("korkeampi prio");
        setGroups(Arrays.asList(new ApplicationOptionGroup(priorizationGroup , 1), new ApplicationOptionGroup(limitedGroup, null)));
    }};

    final ApplicationOption notPartOfTestGroup = new ApplicationOption() {{
        setName("alempi prio");
        setGroups(Arrays.asList(new ApplicationOptionGroup(priorizationGroup, null)));
    }};
    {
        when(aos.get(Mockito.anyString())).thenReturn(notPartOfTestGroup);
    }

    private Map<String, String> testData(TestInput... inputs) {
        Map<String, String> values = new HashMap<String, String>();
        for (int i = 0; i < inputs.length; i++) {
            values.put("preference"+(i+1)+"-Opetuspiste", inputs[i].opetuspisteNimi);
            values.put("preference"+(i+1)+"-Opetuspiste-id", inputs[i].opetuspisteId);
            values.put("preference"+(i+1)+"-Koulutus", inputs[i].koulutusNimi);
            values.put("preference"+(i+1)+"-Koulutus-id", inputs[i].koulutusId);
        }
        return values;
    }

    @Test
    public void testValidateValid() {
        Map<String, String> values = testData(new TestInput(1), new TestInput(2), new TestInput(3), new TestInput(4), new TestInput(5));
        assertFalse(validate(values).hasErrors());
    }

    @Test
    public void testValidateWhenTooManyWithRestrictedGroup() {
        when(aos.get("koulutus-id-1")).thenReturn(partOfTestGroup);
        when(aos.get("koulutus-id-2")).thenReturn(partOfTestGroup);
        when(aos.get("koulutus-id-3")).thenReturn(partOfTestGroup);

        Map<String, String> values = testData(new TestInput(1), new TestInput(2), new TestInput(3), new TestInput(4), new TestInput(5));

        verifyErrors(validate(values), Arrays.asList("preference1-Koulutus", "preference2-Koulutus", "preference3-Koulutus"), "{fi=max error}");
    }

    @Test
    public void testValidateWrongOrder() {
        when(aos.get("koulutus-id-1")).thenReturn(partOfTestGroup);
        when(aos.get("koulutus-id-3")).thenReturn(partOfTestGroup);
        Map<String, String> values = testData(new TestInput(1), new TestInput(2), new TestInput(3), new TestInput(4), new TestInput(5));


        ValidationResult result = validate(values);
        verifyErrors(result, Arrays.asList("preference2-Koulutus", "preference3-Koulutus"), "{fi=prio error: korkeampi=korkeampi prio, alempi=alempi prio}");
    }

    @Test
    public void testValidateNotUniquePreferences() {
        Map<String, String> values = testData(new TestInput("koulutus1", "koulutus-id-1", "opetuspiste1", "opetuspiste-id-1"), new TestInput("koulutus1", "koulutus-id-1", "opetuspiste1", "opetuspiste-id-1"));
        ValidationResult result = validate(values);
        verifySingleError(result, "Et voi syöttää samaa hakutoivetta useaan kertaan.");
    }

    @Test
    public void testValidateEmptyRows() {
        Map<String, String> values = new HashMap<String, String>();
        values.put("preference1-Opetuspiste", "opetuspiste1");
        values.put("preference2-Opetuspiste", "opetuspiste2");
        values.put("preference4-Opetuspiste", "opetuspiste4");
        values.put("preference1-Koulutus", "koulutus1");
        values.put("preference2-Koulutus", "koulutus2");
        values.put("preference4-Koulutus", "koulutus4");
        verifySingleError(validate(values), "Et voi jättää tyhjää hakutoivetta täytettyjen hakutoiveiden väliin.");
    }

    @Test
    public void testValidateEducationValueMissing() {
        Map<String, String> values = new HashMap<String, String>();
        values.put("preference1-Opetuspiste", "opetuspiste1");
        values.put("preference2-Opetuspiste", "opetuspiste2");
        values.put("preference1-Koulutus", "koulutus1");
        verifySingleError(validate(values), "Pakollinen tieto.");
    }

    @Test
    public void testValidateLearningInstituteValueMissing() {
        Map<String, String> values = new HashMap<String, String>();
        values.put("preference1-Opetuspiste", "opetuspiste1");
        values.put("preference1-Koulutus", "koulutus1");
        values.put("preference2-Koulutus", "koulutus2");
        verifySingleError(validate(values), "Pakollinen tieto.");
    }

    private ValidationResult validate(final Map<String, String> values) {
        final ValidationResult result = validator.validate(new ValidationInput(null, values, null, null, ValidationInput.ValidationContext.officer_modify));
        assertNotNull(result);
        return result;
    }

    private void verifyErrors(final ValidationResult result, final List<String> fieldsInError, final String errorString) {
        assertTrue(result.hasErrors());
        assertEquals(fieldsInError.size(), result.getErrorMessages().values().size());
        assertEquals(new HashSet<>(fieldsInError), result.getErrorMessages().keySet());
        assertEquals(errorString, getFirstErrorAsString(result));
    }

    private void verifySingleError(final ValidationResult result, final String errorText) {
        assertTrue(result.hasErrors());
        assertEquals(1, result.getErrorMessages().values().size());
        assertTrue(getFirstErrorAsString(result), getFirstErrorAsString(result).contains(errorText));
    }
    private String getFirstErrorAsString(ValidationResult result) {
        return result.getErrorMessages().values().iterator().next().toString();
    }
}
