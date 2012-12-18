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

package fi.vm.sade.oppija.lomake.service.impl;

import fi.vm.sade.oppija.lomake.service.EncrypterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;

@Service("aesEncrypter")
public class AESEncrypter implements EncrypterService {

    private static final int ITERATION_COUNT = 65436;
    private static final int KEY_LENGTH = 256;
    public static final int IV_SIZE = 16;
    private static final String RANDOM_ALGORITHM = "SHA1PRNG";
    public static final String AES = "AES";
    public static final String AES_CBC_PKCS5_PADDING = "AES/CBC/PKCS5Padding";
    public static final String PBKDF_2_WITH_HMAC_SHA_1 = "PBKDF2WithHmacSHA1";
    private final SecretKey secret;


    @Autowired
    public AESEncrypter(@Value("${hakemus.aes.key}") String passPhrase, @Value("${hakemus.aes.salt}") String salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(PBKDF_2_WITH_HMAC_SHA_1);
        KeySpec spec = new PBEKeySpec(passPhrase.toCharArray(), salt.getBytes(), ITERATION_COUNT, KEY_LENGTH);
        SecretKey tmp = factory.generateSecret(spec);

        this.secret = new SecretKeySpec(tmp.getEncoded(), AES);


    }

    public String encrypt(String encrypt) {
        try {
            return encryptInternal(encrypt);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public String encryptInternal(String encrypt) throws Exception {
        byte[] bytes = encrypt.getBytes("UTF8");
        byte[] encrypted = encrypt(bytes);
        return DatatypeConverter.printBase64Binary(encrypted);
    }

    public byte[] encrypt(byte[] plain) throws Exception {
        byte[] iv = generateIv();

        Cipher ecipher = Cipher.getInstance(AES_CBC_PKCS5_PADDING);

        ecipher.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(iv));
        final byte[] bytes = ecipher.doFinal(plain);

        return merge(iv, bytes);
    }

    protected byte[] merge(byte[] iv, byte[] bytes) {
        final byte[] result = new byte[iv.length + bytes.length];
        System.arraycopy(iv, 0, result, 0, iv.length);
        System.arraycopy(bytes, 0, result, iv.length, bytes.length);
        return result;
    }

    private String decryptInternal(String encrypt) throws Exception {
        byte[] bytes = DatatypeConverter.parseBase64Binary(encrypt);
        byte[] decrypted = decrypt(bytes);
        return new String(decrypted, "UTF8");
    }

    public String decrypt(String encrypt) {
        try {
            return decryptInternal(encrypt);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    protected byte[] generateIv() throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstance(RANDOM_ALGORITHM);
        byte[] iv = new byte[IV_SIZE];
        random.nextBytes(iv);
        return iv;
    }


    public byte[] decrypt(byte[] encrypt) throws Exception {
        Cipher dcipher = Cipher.getInstance(AES_CBC_PKCS5_PADDING);
        dcipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(Arrays.copyOfRange(encrypt, 0, IV_SIZE)));
        return dcipher.doFinal(Arrays.copyOfRange(encrypt, IV_SIZE, encrypt.length));
    }
}
