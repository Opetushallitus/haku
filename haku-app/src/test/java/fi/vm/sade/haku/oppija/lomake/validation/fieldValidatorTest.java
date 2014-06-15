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

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import org.apache.commons.lang.NotImplementedException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class fieldValidatorTest {

    public static final I18nText ERROR_MESSAGE = ElementUtil.createI18NText("error_message");

    @Test
    public void testErrorMessageConstructor() throws Exception {
        FieldValidator validator = createValidator(ERROR_MESSAGE);
        assertEquals(ERROR_MESSAGE, validator.getErrorMessage());
    }




    @Test(expected = NullPointerException.class)
    public void testNullErrorMessage() throws Exception {
        createValidator(null);
    }

    private FieldValidator createValidator(I18nText errorMessage) {
        return new FieldValidator(errorMessage) {
            @Override
            public ValidationResult validate(final ValidationInput validationInput) {
                throw new NotImplementedException();
            }
        };
    }
}
