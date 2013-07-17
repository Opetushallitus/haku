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

package fi.vm.sade.oppija.lomakkeenhallinta.util;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import fi.vm.sade.oppija.lomake.domain.I18nText;
import fi.vm.sade.oppija.lomake.domain.elements.Element;
import fi.vm.sade.oppija.lomake.domain.elements.Notification;
import fi.vm.sade.oppija.lomake.domain.elements.Titled;
import fi.vm.sade.oppija.lomake.domain.elements.custom.gradegrid.GradeGridRow;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Question;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Radio;
import fi.vm.sade.oppija.lomake.domain.elements.questions.TextQuestion;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundExceptionRuntime;
import fi.vm.sade.oppija.lomake.domain.rules.RelatedQuestionRule;
import fi.vm.sade.oppija.lomake.validation.Validator;
import fi.vm.sade.oppija.lomake.validation.validators.RegexFieldValidator;
import fi.vm.sade.oppija.lomake.validation.validators.RequiredFieldValidator;
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
    private static final String[] LANGS = {"fi", "sv", "en"};

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

    public static I18nText createI18NForm(final String text, final String... params) {
        return createI18NText(text, "form_messages", params);
    }

    public static I18nText createI18NTextError(final String text, final String... params) {
        return createI18NText(text, "form_errors", params);
    }

    private static I18nText createI18NText(final String key, final String bundleName, final String... params) {
        Validate.notNull(key, "key can't be null");
        Validate.notNull(bundleName, "bundleName can't be null");

        Map<String, String> translations = new HashMap<String, String>();
        for (String lang : LANGS) {
            ResourceBundle bundle = ResourceBundle.getBundle(bundleName, new Locale(lang));

            String text = "";
            try {
                if (key != null) {
                    text = bundle.getString(key);
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

    public static void addDefaultTrueFalseOptions(final Radio radio) {
        radio.addOption(KYLLA, createI18NForm("form.yleinen.kylla"), KYLLA);
        radio.addOption(EI, createI18NForm("form.yleinen.ei"), EI);
    }

    public static void addYesAndIDontOptions(final Radio radio) {
        radio.addOption(KYLLA, createI18NForm("form.yleinen.kylla"), KYLLA);
        radio.addOption(EI, createI18NForm("form.yleinen.en"), EI);
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

    public static Question createRequiredTextQuestion(final String id, final String name, final String size) {
        TextQuestion textQuestion = new TextQuestion(id, createI18NForm(name));
        addRequiredValidator(textQuestion);
        textQuestion.addAttribute("size", size);
        return textQuestion;
    }

    public static Validator createRegexValidator(final String id, final String pattern) {
        return new RegexFieldValidator(id,
                ElementUtil.createI18NTextError("yleinen.virheellinenArvo"),
                pattern);
    }

    public static void addRequiredValidator(final Element element) {
        element.addAttribute("required", "required");
        element.setValidator(
                new RequiredFieldValidator(
                        element.getId(),
                        ElementUtil.createI18NTextError("yleinen.pakollinen")));
    }

    public static void setRequiredInlineAndVerboseHelp(final Question question) {
        addRequiredValidator(question);
        setVerboseHelp(question);
        question.setInline(true);
    }

    public static void setVerboseHelp(final Titled titled) {
        titled.setVerboseHelp(OppijaConstants.VERBOSE_HELP);
    }

    public static String randomId() {
        return UUID.randomUUID().toString().replace('.', '_');
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

    public static Element notificationWhenTrue(final String id, final String messageKey, final Notification.NotificationType type) {
        RelatedQuestionRule rule = new RelatedQuestionRule(ElementUtil.randomId(),
                id, "^true$", false);

        Notification notification = new Notification(
                ElementUtil.randomId(),
                createI18NForm(messageKey),
                type);
        rule.addChild(notification);

        return rule;
    }
}
