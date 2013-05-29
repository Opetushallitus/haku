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

package fi.vm.sade.oppija.lomake.domain.util;

import com.google.common.base.Joiner;
import fi.vm.sade.oppija.lomake.domain.I18nText;
import fi.vm.sade.oppija.lomake.domain.elements.Element;
import fi.vm.sade.oppija.lomake.domain.elements.custom.gradegrid.GradeGridRow;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Question;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Radio;
import fi.vm.sade.oppija.lomake.domain.elements.questions.TextQuestion;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.FormConstants;
import org.apache.log4j.Logger;

import java.text.MessageFormat;
import java.util.*;

public final class ElementUtil {

    public static final String KYLLA = Boolean.TRUE.toString().toLowerCase();
    public static final String EI = Boolean.FALSE.toString().toLowerCase();
    private static Logger log = Logger.getLogger(ElementUtil.class);
    public static final String DISABLED = "disabled";
    public static final String REQUIRED = "required";
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
        return new I18nText(text + Long.toString(System.currentTimeMillis()), translations);
    }

    public static I18nText createI18NForm(final String text, final String... params) {
        return createI18NText(text, "form_messages", params);
    }

    public static I18nText createI18NTextError(final String text, final String... params) {
        return createI18NText(text, "form_errors", params);
    }

    private static I18nText createI18NText(final String key, final String bundleName, final String... params) {
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
        return new I18nText(key + Long.toString(System.currentTimeMillis()), translations);
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

    private static <E extends Element> void findElementByType(
            final Element element, final List<E> elements, Class<E> eClass) {
        if (element.getClass().isAssignableFrom(eClass)) {
            elements.add((E) element);
        }
        for (Element child : element.getChildren()) {
            findElementByType(child, elements, eClass);
        }
    }

    private static <E extends Element> void findElementByType(
            final Element element, final Map<String, E> elements, Class<E> eClass) {
        if (element.getClass().isAssignableFrom(eClass)) {
            elements.put(element.getId(), (E) element);
        }
        for (Element child : element.getChildren()) {
            findElementByType(child, elements, eClass);
        }
    }

    public static void setDisabled(final Element element) {
        element.addAttribute(DISABLED, DISABLED);
    }

    public static void setRequired(final Element element) {
        element.addAttribute(REQUIRED, REQUIRED);
    }

    public static void addDefaultTrueFalseOptions(final Radio radio) {
        radio.addOption(KYLLA, createI18NForm("form.yleinen.kylla"), KYLLA);
        radio.addOption(EI, createI18NForm("form.yleinen.ei"), EI);
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

    public static final String orStr(String... values) {
        return "(" + Joiner.on('|').skipNulls().join(values) + ")";
    }

    public static Question createRequiredTextQuestion(final String id, final String name, final String size) {
        TextQuestion textQuestion = new TextQuestion(id, createI18NForm(name));
        setRequired(textQuestion);
        textQuestion.addAttribute("size", size);
        return textQuestion;
    }

    public static void setRequiredInlineAndVerboseHelp(final Question question) {
        setRequired(question);
        question.setVerboseHelp(FormConstants.VERBOSE_HELP);
        question.setInline(true);
    }
}
