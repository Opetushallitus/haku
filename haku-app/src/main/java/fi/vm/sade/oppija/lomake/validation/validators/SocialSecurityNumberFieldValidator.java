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
package fi.vm.sade.oppija.lomake.validation.validators;

import fi.vm.sade.oppija.lomake.validation.FieldValidator;
import fi.vm.sade.oppija.lomake.validation.ValidationResult;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validoi suomalaiset henkilötunnukset.
 *
 * @author Mikko Majapuro
 * @author jteuho
 */
public class SocialSecurityNumberFieldValidator extends FieldValidator {

    // Sonarin mukaan tässä luokassa on isosti taikanumeroita. Niin on.
    // Hetussa on määrättyjä asioita tarkoittavia numeroita määrätyillä
    // paikoilla, enkä ala tehdä niitä varten erityisjärjestelyjä.
    
    private String nationalityId;
    private final Pattern socialSecurityNumberPattern;
    private static final String FI = "fi";
    private static final String SOCIAL_SECURITY_NUMBER_PATTERN = "([0-9]{6}[aA+-][0-9]{3}([0-9]|[a-z]|[A-Z]))";
    private static final String NOT_A_DATE_ERROR = "Henkilötunnuksen alkuosa ei muodosta päivämäärää";
    private static final String DOB_IN_FUTURE = "Henkilötunnuksen syntymäaika on tulevaisuudessa";
    private static final String GENERIC_ERROR_MESSAGE = "Henkilötunnus puuttuu tai on virheellinen";
    private static Map<String, Integer> centuries = new HashMap<String, Integer>();
    private DateFormat fmt;
    private static String[] checks = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C",
            "D", "E", "F", "H", "J", "K", "L", "M", "N", "P", "R", "S", "T", "U", "V", "W", "X", "Y"};

    static {
        centuries.put("+", 1800); // NOSONAR
        centuries.put("-", 1900); // NOSONAR
        centuries.put("a", 2000); // NOSONAR
        centuries.put("A", 2000); // NOSONAR
    }

    public SocialSecurityNumberFieldValidator(final String socialSecurityNumberId, final String nationalityId) {
        this(socialSecurityNumberId, nationalityId, GENERIC_ERROR_MESSAGE, SOCIAL_SECURITY_NUMBER_PATTERN);
    }

    public SocialSecurityNumberFieldValidator(final String socialSecurityNumberId, final String nationalityId,
                                              final String errorMessage, final String socialSecurityNumberPattern) {
        super(socialSecurityNumberId, errorMessage);
        this.nationalityId = nationalityId;
        this.socialSecurityNumberPattern = Pattern.compile(socialSecurityNumberPattern);
        fmt = new SimpleDateFormat("ddMMyyyy");
        fmt.setLenient(false);
    }

    @Override
    public ValidationResult validate(Map<String, String> values) {
        String socialSecurityNumber = values.get(fieldName);
        String nationality = values.get(nationalityId);
        ValidationResult validationResult = new ValidationResult();
        if (socialSecurityNumber != null && nationality != null) {
            Matcher matcher = socialSecurityNumberPattern.matcher(socialSecurityNumber);
            if (nationality.equalsIgnoreCase(FI)) {
                if (!matcher.matches()) {
                    validationResult = new ValidationResult(fieldName, GENERIC_ERROR_MESSAGE);
                }
                if (!validationResult.hasErrors()) {
                    validationResult = checkDOB(socialSecurityNumber);
                }
                if (!validationResult.hasErrors()) {
                    validationResult = checkCheckSum(socialSecurityNumber);
                }
            }
        }
        return validationResult;
    }

    private ValidationResult checkCheckSum(String socialSecurityNumber) {
        ValidationResult result = new ValidationResult();
        String dob = socialSecurityNumber.substring(0, 6); // NOSONAR
        String id = socialSecurityNumber.substring(7, 10); // NOSONAR
        String check = socialSecurityNumber.substring(10, 11); // NOSONAR
        int ssnNumber = Integer.valueOf(dob + id);
        String myCheck = checks[ssnNumber % 31]; // NOSONAR
        if (!check.equalsIgnoreCase(myCheck)) {
            result = new ValidationResult(fieldName, GENERIC_ERROR_MESSAGE);
        }
        return result;
    }
    
    /**
     * Tarkistaa, että annetussa hetussa on tunnistettava päivämäärä, ja että päivämäärä on menneisyydessä.
     *
     * @param socialSecurityNumber tarkastettavaksi
     * @return ValidationResult-olio mahdollisine virheviesteineen.
     */
    private ValidationResult checkDOB(String socialSecurityNumber) {
        ValidationResult result = new ValidationResult();
        String dayAndMonth = socialSecurityNumber.substring(0, 4); // NOSONAR
        String year = Integer.toString((centuries.get(socialSecurityNumber.substring(6, 7)) + Integer // NOSONAR
                .valueOf(socialSecurityNumber.substring(4, 6)))); // NOSONAR
        Date dob = null;
        try {
            dob = fmt.parse(dayAndMonth + year);
        } catch (ParseException e) {
            result = new ValidationResult(fieldName, NOT_A_DATE_ERROR);
        }
        if (dob != null && dob.after(new Date())) {
            result = new ValidationResult(fieldName, DOB_IN_FUTURE);
        }
        return result;
    }
}
