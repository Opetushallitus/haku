/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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

package fi.vm.sade.haku.oppija.common.koulutusinformaatio;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mikko Majapuro
 */
public class ApplicationOption {

    private String id;
    private String name;
    private boolean sora;
    private List<String> requiredBaseEducations;
    private boolean specificApplicationDates;
    private boolean canBeApplied;
    private List<String> teachingLanguages;
    private LearningOpportunityProvider provider;
    private String aoIdentifier;
    private boolean kaksoistutkinto;
    private boolean athleteEducation;
    private String educationCode;
    private List<ApplicationOptionGroup> groups;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSora() {
        return sora;
    }

    public void setSora(boolean sora) {
        this.sora = sora;
    }

    public List<String> getRequiredBaseEducations() {
        return requiredBaseEducations;
    }

    public void setRequiredBaseEducations(List<String> requiredBaseEducations) {
        this.requiredBaseEducations = requiredBaseEducations;
    }

    public boolean isSpecificApplicationDates() {
        return specificApplicationDates;
    }

    public void setSpecificApplicationDates(boolean specificApplicationDates) {
        this.specificApplicationDates = specificApplicationDates;
    }

    public boolean isCanBeApplied() {
        return canBeApplied;
    }

    public void setCanBeApplied(boolean canBeApplied) {
        this.canBeApplied = canBeApplied;
    }

    public List<String> getTeachingLanguages() {
        return teachingLanguages;
    }

    public void setTeachingLanguages(List<String> teachingLanguages) {
        this.teachingLanguages = teachingLanguages;
    }

    public LearningOpportunityProvider getProvider() {
        return provider;
    }

    public void setProvider(LearningOpportunityProvider provider) {
        this.provider = provider;
    }

    public String getAoIdentifier() {
        return aoIdentifier;
    }

    public void setAoIdentifier(String aoIdentifier) {
        this.aoIdentifier = aoIdentifier;
    }

    public boolean isKaksoistutkinto() {
        return kaksoistutkinto;
    }

    public void setKaksoistutkinto(boolean kaksoistutkinto) {
        this.kaksoistutkinto = kaksoistutkinto;
    }

    public boolean isAthleteEducation() {
        return athleteEducation;
    }

    public void setAthleteEducation(boolean athleteEducation) {
        this.athleteEducation = athleteEducation;
    }

    public String getEducationCode() {
        return educationCode;
    }

    public void setEducationCode(String educationCode) {
        this.educationCode = educationCode;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("ApplicationOption:{ ")
                .append("id: ").append(id).append(", ")
                .append("name: ").append(name).append("}");
        return builder.toString();
    }

    public void addGroup(ApplicationOptionGroup group) {
        if (groups == null) {
            groups = new ArrayList<>();
        }
        groups.add(group);
    }

    public void setGroups(List<ApplicationOptionGroup> groups) {
        this.groups = groups;
    }

    public List<ApplicationOptionGroup> getGroups() {
        return groups;
    }
}
