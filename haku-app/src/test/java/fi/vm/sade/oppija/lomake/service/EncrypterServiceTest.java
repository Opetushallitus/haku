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

package fi.vm.sade.oppija.lomake.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.vm.sade.oppija.lomake.service.impl.AESEncrypter;

/**
 * @author jukka
 * @version 12/13/124:08 PM}
 * @since 1.1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@ActiveProfiles(profiles = "dev")
public class EncrypterServiceTest {

    @Autowired
    @Qualifier("aesEncrypter")
    EncrypterService encrypterService;

    @Test
    public void testEncryption() throws Exception {
        assertEquals("viesti", encrypterService.decrypt(encrypterService.encrypt("viesti")));
    }

    @Test
    public void testMerge() throws Exception {
        final MockEncrypter mockEncrypter = new MockEncrypter("foo", "bar");
        final byte[] bytes = "foo".getBytes();
        final byte[] bytes2 = "bar".getBytes();

        final byte[] result = mockEncrypter.leakMerge(bytes, bytes2);

        assertEquals(new String(bytes) + new String(bytes2), new String(result));
    }


    private class MockEncrypter extends AESEncrypter {

        public MockEncrypter(String passPhrase, String salt) throws Exception {
            super(passPhrase, salt);
        }

        public byte[] leakMerge(byte[] first, byte[] second) {
            return merge(first, second);
        }

    }
}
