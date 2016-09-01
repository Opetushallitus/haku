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

package fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.impl;

import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.domain.Code;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Hannu Lyytikainen
 */
public class KoodiTypeToCodeFunctionTest {

    KoodiTypeToCodeFunction function;
    KoodiType koodiType;

    @Before
    public void setup() {
        function = new KoodiTypeToCodeFunction();
        koodiType = TestObjectCreator.createKoodiType(TestObjectCreator.KOODI_KOODI_URI_AND_ARVO);
    }

    @Test
    public void testApplyValue() {
        Code code = function.apply(koodiType);
        assertEquals(TestObjectCreator.KOODI_KOODI_URI_AND_ARVO, code.getValue());
    }

    @Test
    public void testApplyText() {
        Code code = function.apply(koodiType);
        assertEquals(TestObjectCreator.NIMI, code.getMetadata()
                .getText(TestObjectCreator.LANG.value().toLowerCase()));

    }
}
