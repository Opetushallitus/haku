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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * European Union Public Licence for more details.
 */

package fi.vm.sade.oppija.lomake.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author jukka
 * @version 12/14/125:03 PM}
 * @since 1.1
 */
@Service("shaEncrypter")
public class SHA2Encrypter implements EncrypterService {

    public static final String ALGORITHM = "SHA-256";
    private final String salt;

    @Autowired
    public SHA2Encrypter(@Value("${hakemus.sha.salt}") String salt) {
        this.salt = salt;
    }

    @Override
    public String decrypt(String encrypt) {
        throw new UnsupportedOperationException("SHA-2 is irreversible");
    }

    @Override
    public String encrypt(String encrypt) {
        try {
            return countDigest(encrypt + salt);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String countDigest(String encrypt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(ALGORITHM);
        md.update(encrypt.getBytes());

        return convertToHex(md.digest());
    }

    private String convertToHex(byte[] byteData) {
        //convert the byte to hex format
        StringBuilder sb = new StringBuilder();
        for (byte aByteData : byteData) {
            sb.append(Integer.toString((aByteData & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }
}
