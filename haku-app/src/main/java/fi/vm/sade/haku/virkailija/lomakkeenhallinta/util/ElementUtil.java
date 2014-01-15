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
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystemBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Titled;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.PreferenceRow;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.SinglePreference;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.gradegrid.GradeGridRow;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Question;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Radio;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.TextQuestion;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.*;
import fi.vm.sade.haku.oppija.lomake.validation.Validator;
import fi.vm.sade.haku.oppija.lomake.validation.validators.*;
import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;

import java.text.MessageFormat;
import java.util.*;

public final class ElementUtil {

    public static final String ISO88591_NAME_REGEX = "^$|^[a-zA-ZÀ-ÖØ-öø-ÿ]$|^[a-zA-ZÀ-ÖØ-öø-ÿ][a-zA-ZÀ-ÖØ-öø-ÿ ,-]*(?:[a-zA-ZÀ-ÖØ-öø-ÿ]+$)$";
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$|^$";

    public static final String KYLLA = Boolean.TRUE.toString().toLowerCase();
    public static final String EI = Boolean.FALSE.toString().toLowerCase();
    private static Logger log = Logger.getLogger(ElementUtil.class);
    public static final String DISABLED = "disabled";
    public static final String HIDDEN = "hidden";
    private static final String[] LANGS = {"fi", "sv"};

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

    public static I18nText createI18NText(final String key, final String bundleName, final String... params) {
        return createI18NText(key, bundleName, false, params);
    }

    public static I18nText createI18NText(final String key, final String bundleName, final boolean keepFirst,
                                          final String... params) {
        Validate.notNull(key, "key can't be null");
        Validate.notNull(bundleName, "bundleName can't be null");

        Map<String, String> translations = new HashMap<String, String>();
        for (String lang : LANGS) {
            ResourceBundle bundle = ResourceBundle.getBundle(bundleName, new Locale(lang));

            String text = "";
            try {
                if (key != null) {
                    text = bundle.getString(key);
                    if (keepFirst) {
                        // Add space at the beginning of string, making it appear before regular words in
                        // alphabetical order.
                        text = "\u0020" + text;
                    }
                }
                if (params != null && params.length > 0) {
                    text = MessageFormat.format(text, (Object[]) params);
                }
            } catch (MissingResourceException mre) {
                text = key + " [" + lang + "]";
                log.warn("No translation found for key '" + key + "' in " + lang);
            }
            translations.put(lang, text);
        }
        return new I18nText(translations);
    }

    public static List<Element> filterElements(final Element element, final Predicate<Element> predicate) {
        List<Element> elements = new ArrayList<Element>();
        filterElements(element, elements, predicate);
        return elements;
    }

    public static <E extends Element> Map<String, E> findElementsByType(Element element, Class<E> eClass) {
        Map<String, E> elements = new HashMap<String, E>();
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

    public static void addDefaultTrueFalseOptions(final Radio radio, final String bundleName) {
        radio.addOption(createI18NText("form.yleinen.kylla", bundleName), KYLLA);
        radio.addOption(createI18NText("form.yleinen.ei", bundleName), EI);
    }

    public static void addYesAndIDontOptions(final Radio radio, final String bundleName) {
        radio.addOption(createI18NText("form.yleinen.kylla", bundleName), KYLLA);
        radio.addOption(createI18NText("form.yleinen.en", bundleName), EI);
    }

    public static GradeGridRow createHiddenGradeGridRowWithId(final String id) {
        GradeGridRow gradeGridRow = new GradeGridRow(id);
        gradeGridRow.addAttribute(HIDDEN, HIDDEN);
        return gradeGridRow;
    }


    public static void setDefaultOption(final String value, final List<Option> options) {
        for (Option opt : options) {
            opt.setDefaultOption(opt.getValue().equalsIgnoreCase(value));
        }
    }

    public static String orStr(String... values) {
        return "(" + Joiner.on('|').skipNulls().join(values) + ")";
    }

    public static Question createRequiredTextQuestion(final String id, final String name, final String bundleName,
                                                      final String errorBundleName, final int size) {
        TextQuestion textQuestion = new TextQuestion(id, createI18NText(name, bundleName));
        addRequiredValidator(textQuestion, errorBundleName);
        addSizeAttribute(textQuestion, size);
        return textQuestion;
    }

    public static Element addSizeAttribute(final Element element, final int size) {
        element.addAttribute("size", String.valueOf(size));
        return element;
    }


    public static Validator createRegexValidator(final String id, final String pattern, final String bundleName) {
        return new RegexFieldValidator(id,
                ElementUtil.createI18NText("yleinen.virheellinenArvo", bundleName),
                pattern);
    }

    public static Validator createValueSetValidator(final String id, final List<String> validValues, final String bundleName) {
        return new ValueSetValidator(id,
                ElementUtil.createI18NText("yleinen.virheellinenArvo", bundleName),
                validValues);
    }

    public static void addRequiredValidator(final Element element, final String bundleName) {
        element.addAttribute("required", "required");
        element.setValidator(
                new RequiredFieldValidator(
                        element.getId(),
                        ElementUtil.createI18NText("yleinen.pakollinen", bundleName)));
    }

    public static void addApplicationUniqueValidator(final Element element, final String asType) {
        if (OppijaConstants.LISA_HAKU.equals(asType)) {
            element.setValidator(new SsnAndPreferenceUniqueValidator());
        } else {
            element.setValidator(new SsnUniqueValidator());
        }
    }

    public static void addPreferenceValidator(final Element element) {
        Preconditions.checkArgument(element instanceof PreferenceRow || element instanceof SinglePreference);
        element.setValidator(new PreferenceValidator());
    }


    public static void setRequiredInlineAndVerboseHelp(final Question question, final String helpId, final String bundleName,
                                                       final String errorBundleName) {
        addRequiredValidator(question, errorBundleName);
        setVerboseHelp(question, helpId, bundleName);
        question.setInline(true);
    }

    public static void setVerboseHelp(final Titled titled, final String helpId, final String bundleName) {
        titled.setVerboseHelp(createI18NText(helpId, bundleName));
    }

    public static String randomId() {
        //starting random id with a letter preventing some javascript errors
        return 'a' + UUID.randomUUID().toString().replace('.', '_');
    }

    private static void filterElements(
            final Element element, final List<Element> elements, final Predicate<Element> predicate) {
        if (predicate.apply(element)) {
            elements.add(element);
        }
        for (Element child : element.getChildren()) {
            filterElements(child, elements, predicate);
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
            elements.put(element.getId(), (E) element);
        }
        for (Element child : element.getChildren()) {
            findElementByType(child, elements, eClass);
        }
    }

    public static ApplicationSystem createActiveApplicationSystem(final String id, Form form) {
        final Calendar instance = Calendar.getInstance();
        instance.roll(Calendar.YEAR, -1);
        Date start = new Date(instance.getTimeInMillis());
        instance.roll(Calendar.YEAR, 2);
        Date end = new Date(instance.getTimeInMillis());
        List<ApplicationPeriod> applicationPeriods = Lists.newArrayList(new ApplicationPeriod(start, end));
        return new ApplicationSystemBuilder().addId(id).addForm(form)
                .addName(ElementUtil.createI18NAsIs("test application period"))
                .addApplicationPeriods(applicationPeriods)
                .addApplicationSystemType(OppijaConstants.VARSINAINEN_HAKU)
                .get();
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


    //
    // Refactor to use Operator factory or in the future high order function.
    //
    public static Expr atLeastOneVariableEqualsToValue(final String value, final String... ids) {
        if (ids.length == 1) {
            return new Equals(new Variable(ids[0]), new Value(value));
        } else {
            Expr current = null;
            Expr equal;
            for (String id : ids) {
                equal = new Equals(new Variable(id), new Value(value));
                if (current == null) {
                    current = new Equals(new Variable(id), new Value(value));
                } else {
                    current = new Or(current, equal);
                }
            }
            return current;
        }
    }
    public static Expr atLeastOneValueEqualsToVariable(final String variable, final String... values) {
        if (values.length == 1) {
            return new Equals(new Value(values[0]), new Variable(variable));
        } else {
            Expr current = null;
            Expr equal;
            for (String value : values) {
                equal = new Equals(new Variable(variable), new Value(value));
                if (current == null) {
                    current = equal;
                } else {
                    current = new Or(current, equal);
                }
            }
            return current;
        }
    }
}
