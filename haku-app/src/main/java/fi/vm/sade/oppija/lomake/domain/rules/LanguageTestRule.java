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

package fi.vm.sade.oppija.lomake.domain.rules;

import fi.vm.sade.oppija.lomake.domain.elements.Element;
import fi.vm.sade.oppija.lomake.domain.elements.Group;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;
import java.util.Map;

public class LanguageTestRule extends Element {

    private static final long serialVersionUID = -6030200061901263949L;
    private final String language;

    public LanguageTestRule(@JsonProperty(value = "id") String id,
                            @JsonProperty(value = "language") String language) {
        super(id);
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }


    @JsonIgnore
    public String getTests(String aidinkieli, String a1Kieli, String a2Kieli, String a1Grade, String a2Grade) {
        boolean needed = true;
        if (language.equals(aidinkieli)
                || (language.equals(a1Kieli) && checkGrade(a1Grade))
                || (language.equals(a2Kieli) && checkGrade(a2Grade))) {
            needed = false;
        }

        return "{ \"needed\" : " + Boolean.toString(needed) + " }";
    }

    private boolean checkGrade(String gradeStr) {
        int grade;
        try {
            grade = Integer.parseInt(gradeStr);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return grade >= 7;
    }

    @Override
    public List<Element> getChildren(final Map<String, String> values) {
        return children;
    }

    @Override
    public Element addChild(Element... children) {
        if (this.children.isEmpty()) {
            this.children.add(new Group(this.id + "Group"));
        }
        this.children.get(0).addChild(children);
        return this;
    }
}
