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
package fi.vm.sade.oppija.common.organisaatio;

public class SearchCriteria {

    private boolean includePassive;

    private boolean includePlanned;

    private String learningInstitutionType;

    private String organizationType;

    private String searchString;

    public SearchCriteria() {
    }

    public String getLearningInstitutionType() {
        return learningInstitutionType;
    }

    public String getOrganizationType() {
        return organizationType;
    }

    public String getSearchString() {
        return searchString;
    }

    public boolean isIncludePassive() {
        return includePassive;
    }

    public boolean isIncludePlanned() {
        return includePlanned;
    }

    public void setIncludePassive(boolean includePassive) {
        this.includePassive = includePassive;
    }

    public void setIncludePlanned(boolean includePlanned) {
        this.includePlanned = includePlanned;
    }

    /*
     * Koodistosta Oppilaitostyyppi
     */
    public void setLearningInstitutionType(String learningInstitutionType) {
        this.learningInstitutionType = learningInstitutionType;
    }

    /*
     * Koodistosta Organisaatiotyyppi
     */
    public void setOrganizationType(String organizationType) {
        this.organizationType = organizationType;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

}
