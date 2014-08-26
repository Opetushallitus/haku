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

package fi.vm.sade.haku.virkailija.lomakkeenhallinta.util;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Titled;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.gradegrid.GradeGridRow;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.Regexp;
import fi.vm.sade.haku.oppija.lomake.validation.Validator;
import fi.vm.sade.haku.oppija.lomake.validation.validators.*;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static fi.vm.sade.haku.oppija.lomake.domain.I18nText.LANGS;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.RelatedQuestionRuleBuilder.Rule;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.FORM_COMMON_BUNDLE_NAME;

public final class ElementUtil {

    public static final String ISO88591_NAME_REGEX = "^$|^[a-zA-ZÀ-ÖØ-öø-ÿ]$|^[a-zA-ZÀ-ÖØ-öø-ÿ'][a-zA-ZÀ-ÖØ-öø-ÿ ,-.']*(?:[a-zA-ZÀ-ÖØ-öø-ÿ.']+$)$";
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)+$|^$";
    public static final String YEAR_REGEX = "^(19[0-9][0-9])|(20[0-9][0-9])$|^$"; // "^[1-2][0-9]{3}$";
    public static final String KYLLA = Boolean.TRUE.toString();
    public static final String EI = Boolean.FALSE.toString();
    private static Logger log = LoggerFactory.getLogger(ElementUtil.class);
    public static final String DISABLED = "disabled";
    public static final String HIDDEN = "hidden";

    private ElementUtil() {
    }

    /**
     * For tests
     */
    public static I18nText createI18NAsIs(final String text) {
        Map<String, String> translations = new HashMap<String, String>();
        for (String lang : LANGS) {
            translations.put(lang, text);
        }
        return new I18nText(translations);
    }

    public static I18nText createI18NText(final String key, final FormParameters formParameters) {
        return formParameters.getI18nText(key);
    }

    public static I18nText createI18NText(final String key) { // Todo get rid of this function
        return createI18NText(key, OppijaConstants.FORM_COMMON_BUNDLE_NAME);
    }

    public static I18nText createI18NText(final String key, final String bundleName) { // Todo get rid of this function
        Validate.notNull(key, "key can't be null");
        Validate.notNull(bundleName, "bundleName can't be null");

        Map<String, String> translations = new HashMap<String, String>();
        for (String lang : LANGS) {

            String text = getString(bundleName, key.toLowerCase(), lang);

            if (text != null) {
                translations.put(lang, text);
            }
        }
        return new I18nText(translations);
    }

    public static I18nText addSpaceAtTheBeginning(final I18nText i18nText) {
        Map<String, String> newTranslations = new HashMap<String, String>();
        for (Map.Entry<String, String> stringStringEntry : i18nText.getTranslations().entrySet()) {
            // Add space at the beginning of string, making it appear before regular words in
            // alphabetical order.
            newTranslations.put(stringStringEntry.getKey(), "\u0020" + stringStringEntry.getValue());
        }
        return new I18nText(newTranslations);
    }

    private static String getString(final String bundleName, final String key, final String lang) {
        String text = null;
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(bundleName, new Locale(lang));
            if (bundle.containsKey(key)) {
                text = bundle.getString(key);
            } else {
                ResourceBundle commonBundle = ResourceBundle.getBundle(FORM_COMMON_BUNDLE_NAME, new Locale(lang));
                text = commonBundle.getString(key);
            }
        } catch (MissingResourceException mre) {
            log.warn("No translation found for key '{}' bundle: {} lang: {}", key, bundleName, lang);
        }
        return text;

    }

    public static List<Element> filterElements(final Element element, final Predicate<Element> predicate, final Map<String, String> answers) {
        List<Element> elements = new ArrayList<Element>();
        filterElements(element, elements, predicate, answers);
        return elements;
    }

    public static <E extends Element> Map<String, E> findElementsByType(Element element, Class<E> eClass) {
        Map<String, E> elements = new LinkedHashMap<String, E>();
        findElementByType(element, elements, eClass);
        return elements;
    }

    public static <E extends Element> List<E> findElementsByTypeAsList(Element element, Class<E> eClass) {
        List<E> elements = new ArrayList<E>();
        findElementByType(element, elements, eClass);
        return elements;
    }

    public static void setDisabled(final Element element) {
        element.addAttribute(DISABLED, DISABLED);
    }

    public static GradeGridRow createHiddenGradeGridRowWithId(final String id) {
        GradeGridRow gradeGridRow = new GradeGridRow(id);
        gradeGridRow.addAttribute(HIDDEN, HIDDEN);
        return gradeGridRow;
    }

    public static Validator createRegexValidator(final String pattern, final FormParameters formParameters) {
        return createRegexValidator(pattern, formParameters, "yleinen.virheellinenarvo");
    }

    public static Validator createRegexValidator(final String pattern, final FormParameters formParameters,
                                                 final String messageKey) {
        return new RegexFieldValidator(formParameters.getI18nText(messageKey), pattern);
    }

    public static Validator createValueSetValidator(final List<String> validValues, final FormParameters formParameters) {
        return new ValueSetValidator(formParameters.getI18nText("yleinen.virheellinenarvo"), validValues);
    }


    public static Validator createYearValidator(FormParameters formParameters, final Integer toYear, final boolean allowEmpty, final Integer fromYear) {
        return new YearValidator(fromYear, toYear, allowEmpty);
    }

    public static void addRequiredValidator(final Element element, final FormParameters formParameters) {
        String required = "required";
        element.addAttribute(required, required);
        element.setValidator(
                new RequiredFieldValidator(
                        ElementUtil.createI18NText("yleinen.pakollinen", formParameters)));
    }

    public static void addUniqueApplicantValidator(final Element element, final String asType) {
        if (OppijaConstants.VARSINAINEN_HAKU.equals(asType)) {
            element.setValidator(new SsnUniqueValidator());
        }
    }

    public static void setVerboseHelp(final Element element, final String helpId, final FormParameters formParameters) {
        if (element instanceof Titled) {
            ((Titled) element).setVerboseHelp(formParameters.getI18nText(helpId));
        }
    }

    public static void setVerboseHelp(Element element, I18nText i18nText) {
        if (element instanceof Titled) {
            ((Titled) element).setVerboseHelp(i18nText);
        }
    }

    public static String randomId() {
        //starting random id with a letter preventing some javascript errors
        return 'a' + UUID.randomUUID().toString().replace('.', '_');
    }

    private static void filterElements(
            final Element element, final List<Element> elements, final Predicate<Element> predicate, final Map<String, String> answers) {
        if (predicate.apply(element)) {
            elements.add(element);
        }
        if (answers != null) {
            for (Element child : element.getChildren(answers)) {
                filterElements(child, elements, predicate, answers);
            }
        } else {
            for (Element child : element.getChildren()) {
                filterElements(child, elements, predicate, answers);
            }
        }
    }

    private static <E extends Element> void findElementByType(
            final Element element, final List<E> elements, Class<E> eClass) {
        if (eClass.isAssignableFrom(element.getClass())) {
            elements.add((E) element);
        }
        for (Element child : element.getChildren()) {
            findElementByType(child, elements, eClass);
        }
    }

    private static <E extends Element> void findElementByType(
            final Element element, final Map<String, E> elements, Class<E> eClass) {
        if (eClass.isAssignableFrom(element.getClass())) {
            System.out.println("Lisätään löytynyt elementti " + element.getId());
            elements.put(element.getId(), (E) element);
        }
        for (Element child : element.getChildren()) {
            findElementByType(child, elements, eClass);
        }
    }

    public static String getPath(final ApplicationSystem applicationSystem, final String id) {
        List<String> paths = paths(applicationSystem.getForm(), id);
        paths.remove(0);
        paths.add(0, applicationSystem.getId());
        return Joiner.on("/").skipNulls().join(paths);
    }

    private static List<String> paths(final Element from, final String id) {
        List<String> result = new ArrayList<String>();
        if (from.getId().equals(id)) {
            result.add(id);
        } else {
            List<Element> children = from.getChildren();
            for (Element child : children) {
                List<String> paths = paths(child, id);
                if (!paths.isEmpty()) {
                    result.add(from.getId());
                    result.addAll(paths);
                }
            }
        }
        return result;
    }


    public static Element createVarEqualsToValueRule(final String variable, final String... values) {
        return Rule(ExprUtil.atLeastOneValueEqualsToVariable(variable, values)).build();
    }

    public static Element createRuleIfVariableIsTrue(final String variable) {
        return Rule(ExprUtil.isAnswerTrue(variable)).build();
    }

    public static Element createRegexpRule(final Element element, final String pattern) {
        return createRegexpRule(element.getId(), pattern);
    }

    public static Element createRegexpRule(final String variable, final String pattern) {
        return Rule(new Regexp(variable, pattern)).build();
    }

}
