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

import fi.vm.sade.oppija.lomake.domain.I18nText;
import fi.vm.sade.oppija.lomake.domain.elements.Element;
import fi.vm.sade.oppija.lomake.domain.elements.custom.DiscretionaryQuestion;
import fi.vm.sade.oppija.lomake.domain.elements.custom.PreferenceRow;
import fi.vm.sade.oppija.lomake.domain.elements.custom.gradegrid.GradeGridRow;
import fi.vm.sade.oppija.lomake.domain.elements.questions.DropdownSelect;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Option;
import org.apache.log4j.Logger;

import java.text.MessageFormat;
import java.util.*;

public final class ElementUtil {

    public static final String DISABLED = "disabled";
    public static final String REQUIRED = "required";
    public static final String HIDDEN = "hidden";
    private static Logger log = Logger.getLogger(ElementUtil.class);
    private static String[] LANGS = {"fi", "sv", "en"};

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

    public static PreferenceRow createI18NPreferenceRow(final String id, final String title, final Integer discretionaryEducationDegree) {
        String followUpId = id + "-followUp";
        DiscretionaryQuestion discretionary = new DiscretionaryQuestion(id + "-Harkinnanvarainen", createI18NForm("form.hakutoiveet.harkinnanvarainen"), followUpId);
        Option o1 = discretionary.addOption("discretionary_option_1", createI18NForm("form.yleinen.en"), "false");
        o1.addAttribute("data-followUpId", id + "-followUp");
        Option o2 = discretionary.addOption("discretionary_option_2", createI18NForm("form.yleinen.kylla"), "true");
        o2.addAttribute("data-followUpId", id + "-followUp");
        discretionary.addAttribute("required", "required");

        DropdownSelect discretionaryFollowUp = new DropdownSelect(id + " - harkinnanvarainen_jatko", createI18NForm("form.hakutoiveet.harkinnanvarainen.perustelu"));
        discretionaryFollowUp.addOption("harkinnanvarainena_jatko_option_1",
                createI18NForm("form.hakutoiveet.harkinnanvarainen.perustelu.oppimisvaikeudet"), "oppimisvaikudet");
        discretionaryFollowUp.addOption("harkinnanvarainena_jatko_option_2",
                createI18NForm("form.hakutoiveet.harkinnanvarainen.perustelu.sosiaaliset"), "sosiaalisetsyyt");

        PreferenceRow pr = new PreferenceRow(id,
                createI18NForm("form.hakutoiveet.hakutoive", title),
                createI18NForm("form.yleinen.tyhjenna"),
                createI18NForm("form.hakutoiveet.koulutus"),
                createI18NForm("form.hakutoiveet.opetuspiste"),
                createI18NForm("form.hakutoiveet.sisaltyvatKoulutusohjelmat"),
                "Valitse koulutus", discretionaryEducationDegree, discretionary, discretionaryFollowUp);
        return pr;
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

    private static <E extends Element> void findElementByType(final Element element, final List<E> elements, Class<E> eClass) {
        if (element.getClass().isAssignableFrom(eClass)) {
            elements.add((E) element);
        }
        for (Element child : element.getChildren()) {
            findElementByType(child, elements, eClass);
        }
    }

    private static <E extends Element> void findElementByType(final Element element, final Map<String, E> elements, Class<E> eClass) {
        if (element.getClass().isAssignableFrom(eClass)) {
            elements.put(element.getId(), (E) element);
        }
        for (Element child : element.getChildren()) {
            findElementByType(child, elements, eClass);
        }
    }

    public static void setDisabled(Element element) {
        element.addAttribute(DISABLED, DISABLED);
    }

    public static void setRequired(Element element) {
        element.addAttribute(REQUIRED, REQUIRED);
    }

    public static GradeGridRow createHiddenGradeGridRowWithId(final String id) {
        GradeGridRow gradeGridRow = new GradeGridRow(id);
        gradeGridRow.addAttribute(HIDDEN, HIDDEN);
        return gradeGridRow;
    }
}
