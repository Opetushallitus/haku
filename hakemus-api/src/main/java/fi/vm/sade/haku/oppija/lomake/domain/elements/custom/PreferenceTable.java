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

package fi.vm.sade.haku.oppija.lomake.domain.elements.custom;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Question;
import fi.vm.sade.haku.oppija.lomake.validation.GroupRestrictionValidator;
import fi.vm.sade.haku.oppija.lomake.validation.Validator;
import fi.vm.sade.haku.oppija.lomake.validation.validators.PreferenceTableValidator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.data.annotation.Transient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Preference table element with row sorting functionality
 *
 * @author Mikko Majapuro
 */
public class PreferenceTable extends Question {

    private static final long serialVersionUID = 1289678491786047575L;
    private boolean usePriorities;
    private int preferencesInitiallyVisible;
    private final Boolean useDropdowns;

    private List<GroupRestrictionValidator> groupRestrictionValidators;

    public PreferenceTable(@JsonProperty(value = "id") final String id,
                           @JsonProperty(value = "i18nText") final I18nText i18nText,
                           @JsonProperty(value = "usePriorities") final Boolean usePriorities,
                           @JsonProperty(value = "preferencesInitiallyVisible") final Integer preferencesInitiallyVisible,
                           @JsonProperty(value = "useDropdowns") final Boolean useDropdowns) {
        super(id, i18nText);
        this.usePriorities = usePriorities != null && usePriorities;
        this.preferencesInitiallyVisible = preferencesInitiallyVisible.intValue();
        this.groupRestrictionValidators = new ArrayList<GroupRestrictionValidator>();
        this.useDropdowns = useDropdowns;
    }

    public boolean isUsePriorities() {
        return usePriorities;
    }

    public int getPreferencesInitiallyVisible() {
        return preferencesInitiallyVisible;
    }

    @Override
    @Transient
    public List<Validator> getValidators() {
        return (List)Arrays.asList(new PreferenceTableValidator(this));
    }

    @Transient
    public List<PreferenceRow> getRows() {
        return (List)getChildren();
    }

    public List<GroupRestrictionValidator> getGroupRestrictionValidators() {
        return groupRestrictionValidators;
    }

    public Boolean getUseDropdownForLearningInstitution() {
        return useDropdowns;
    }

    public void setGroupRestrictionValidators(List<GroupRestrictionValidator> groupRestrictionValidators) {
        this.groupRestrictionValidators = groupRestrictionValidators;
    }

}
