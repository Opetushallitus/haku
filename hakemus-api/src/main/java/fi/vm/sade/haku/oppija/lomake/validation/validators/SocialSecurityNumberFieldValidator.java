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

import fi.vm.sade.haku.oppija.lomake.util.SpringInjector;
import fi.vm.sade.haku.oppija.lomake.validation.FieldValidator;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationInput;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationResult;
import org.springframework.beans.factory.annotation.Value;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SocialSecurityNumberFieldValidator extends FieldValidator {

    // Sonarin mukaan tässä luokassa on isosti taikanumeroita. Niin on.
    // Hetussa on määrättyjä asioita tarkoittavia numeroita määrätyillä
    // paikoilla, enkä ala tehdä niitä varten erityisjärjestelyjä.

    public static final String SOCIAL_SECURITY_NUMBER_PATTERN = "([0-9]{6}[aA-][0-9]{3}([0-9]|[a-z]|[A-Z]))";
    public static final String GENERIC_ERROR_MESSAGE_KEY = "henkilotiedot.hetu.virhe";
    private final Pattern socialSecurityNumberPattern;
    private static final String NOT_A_DATE_ERROR_KEY = "henkilotiedot.hetu.eiPvm";
    private static final String DOB_IN_FUTURE_ERROR_KEY = "henkilotiedot.hetu.tulevaisuudessa";
    private static Map<String, Integer> centuries = new HashMap<String, Integer>();
    private static String[] checks = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C",
            "D", "E", "F", "H", "J", "K", "L", "M", "N", "P", "R", "S", "T", "U", "V", "W", "X", "Y"};

    @Value("${env.is.test.environment:false}")
    private boolean isTestEnvironment;

    static {
        centuries.put("-", 1900); // NOSONAR
        centuries.put("a", 2000); // NOSONAR
        centuries.put("A", 2000); // NOSONAR
    }

    public SocialSecurityNumberFieldValidator() {
        super(GENERIC_ERROR_MESSAGE_KEY);
        this.socialSecurityNumberPattern = Pattern.compile(SOCIAL_SECURITY_NUMBER_PATTERN);
        SpringInjector.injectSpringDependencies(this);
    }

    @Override
    public ValidationResult validate(final ValidationInput validationInput) {

        String socialSecurityNumber = validationInput.getValue();
        ValidationResult validationResult = new ValidationResult();
        if(this.demoMode) {
            return validationResult;
        }
        if (socialSecurityNumber != null) {
            Matcher matcher = socialSecurityNumberPattern.matcher(socialSecurityNumber);
            if (!matcher.matches()) {
                validationResult = getInvalidValidationResult(validationInput);
            }
            if (!validationResult.hasErrors()) {
                validationResult = checkSeparator(validationInput, socialSecurityNumber);
            }
            if (!validationResult.hasErrors()) {
                validationResult = checkDOB(validationInput, socialSecurityNumber);
            }
            if (!validationResult.hasErrors() && !validChecksum(socialSecurityNumber)) {
                validationResult = getInvalidValidationResult(validationInput);
            }
        }
        return validationResult;
    }

    private boolean validChecksum(String socialSecurityNumber) {
        String dob = socialSecurityNumber.substring(0, 6); // NOSONAR
        String id = socialSecurityNumber.substring(7, 10); // NOSONAR
        String check = socialSecurityNumber.substring(10, 11); // NOSONAR
        int ssnNumber = Integer.valueOf(dob + id);
        String myCheck = checks[ssnNumber % 31]; // NOSONAR
        return check.equalsIgnoreCase(myCheck);
    }

    /**
     * Tarkistus, että 00 - nykyvuosi hetuissa on A/a ja muissa - . ts. satavuotiaat on poistettu ilmoittautumisesta
     */
    private ValidationResult checkSeparator(final ValidationInput validationInput, final String socialSecurityNumber) {
        ValidationResult result = new ValidationResult();

        int currentYear = Calendar.getInstance().get(Calendar.YEAR) - 2000;
        int yearPart = Integer .valueOf(socialSecurityNumber.substring(4, 6));

        String separator = socialSecurityNumber.substring(6, 7);

        // separator should be a/A and not in any case + (added for backward compatibility)
        // except in test environment, where some test SSNs from 1901 are required
        boolean is1800s = separator.equals("+");
        boolean is100YearsAway = (yearPart <= currentYear) && !separator.equalsIgnoreCase("a");
        if ( (is100YearsAway && !isTestEnvironment) || is1800s ) {
            result = new ValidationResult(validationInput.getFieldName(), getI18Text(GENERIC_ERROR_MESSAGE_KEY, validationInput.getApplicationSystemId()));
        }

        return result;
    }

    /**
     * Tarkistaa, että annetussa hetussa on tunnistettava päivämäärä, ja että päivämäärä on menneisyydessä.
     *
     * @param socialSecurityNumber tarkastettavaksi
     * @return ValidationResult-olio mahdollisine virheviesteineen.
     */
    private ValidationResult checkDOB(final ValidationInput validationInput, final String socialSecurityNumber) {
        ValidationResult result = new ValidationResult();
        String dayAndMonth = socialSecurityNumber.substring(0, 4); // NOSONAR

        String year = Integer.toString((centuries.get(socialSecurityNumber.substring(6, 7)) + Integer // NOSONAR
          .valueOf(socialSecurityNumber.substring(4, 6)))); // NOSONAR
        Date dob = null;
        try {
            dob = date().parse(dayAndMonth + year);
        } catch (ParseException e) {
            result = new ValidationResult(validationInput.getFieldName(), getI18Text(NOT_A_DATE_ERROR_KEY, validationInput.getApplicationSystemId()));
        }
        if (dob != null && dob.after(new Date())) {
            result = new ValidationResult(validationInput.getFieldName(), getI18Text(DOB_IN_FUTURE_ERROR_KEY, validationInput.getApplicationSystemId()));
        }
        return result;
    }

    private DateFormat date() {
        DateFormat format = new SimpleDateFormat("ddMMyyyy");
        format.setLenient(false);
        return format;
    }
}
