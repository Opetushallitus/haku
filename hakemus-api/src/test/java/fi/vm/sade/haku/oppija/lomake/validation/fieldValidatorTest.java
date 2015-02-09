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

package fi.vm.sade.haku.oppija.lomake.validation;

import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystemBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Text;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.I18nBundle;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.i18n.I18nBundleService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import net.sf.saxon.functions.Collection;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class fieldValidatorTest {

    public static final String ERROR_MESSAGE_KEY = "error_message_key";

    private I18nBundle i18nBundle;
    private I18nBundleService i18nBundleService;

    @Before
    public void setUp() {
        i18nBundleService = spy(new I18nBundleService(null));
        ApplicationSystem synth = new ApplicationSystemBuilder().setId("haku")
          .setName(ElementUtil.createI18NAsIs("haku"))
          .setHakukausiUri(OppijaConstants.HAKUKAUSI_KEVAT)
          .setApplicationSystemType(OppijaConstants.HAKUTYYPPI_VARSINAINEN_HAKU)
          .setHakutapa(OppijaConstants.HAKUTAPA_YHTEISHAKU)
          .get();
        i18nBundle = spy(i18nBundleService.getBundle(synth));
        doReturn(i18nBundle).when(i18nBundleService).getBundle((String) isNull());
    }

    @Test
    public void testErrorMessageConstructor() throws Exception {
        FieldValidator validator = createValidator(ERROR_MESSAGE_KEY);
        validator.setI18nBundleService(i18nBundleService);
        ValidationResult vr = validator.validate(new ValidationInput(new Text("element", null), Collections.EMPTY_MAP,null, null,null));
        assertTrue(vr.hasErrors());
        verify(i18nBundle, times(1)).get(ERROR_MESSAGE_KEY);
    }


    @Test(expected = NullPointerException.class)
    public void testNullErrorMessage() throws Exception {
        createValidator(null);
    }

    private FieldValidator createValidator(final String errorMessageKey) {
        return new FieldValidator(errorMessageKey) {
            @Override
            public ValidationResult validate(final ValidationInput validationInput) {
                return this.getInvalidValidationResult(validationInput);
            }
        };
    }
}
