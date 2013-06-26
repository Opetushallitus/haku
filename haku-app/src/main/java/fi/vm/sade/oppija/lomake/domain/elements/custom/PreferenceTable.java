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

package fi.vm.sade.oppija.lomake.domain.elements.custom;

import com.google.common.base.Predicate;
import fi.vm.sade.oppija.lomake.domain.I18nText;
import fi.vm.sade.oppija.lomake.domain.elements.Element;
import fi.vm.sade.oppija.lomake.domain.elements.Titled;
import fi.vm.sade.oppija.lomake.validation.Validator;
import fi.vm.sade.oppija.lomake.validation.validators.*;
import fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Predicates.*;
import static fi.vm.sade.oppija.lomake.validation.validators.FunctionalValidator.ValidatorPredicate.validate;

/**
 * Preference table element with row sorting functionality
 *
 * @author Mikko Majapuro
 */
public class PreferenceTable extends Titled {

    private static final long serialVersionUID = 1289678491786047575L;

    public PreferenceTable(@JsonProperty(value = "id") final String id,
                           @JsonProperty(value = "i18nText") final I18nText i18nText
    ) {
        super(id, i18nText);
    }

    @Override
    @JsonIgnore
    public List<Validator> getValidators() {
        List<Validator> listOfValidators = new ArrayList<Validator>();
        List<String> learningInstitutionInputIds = new ArrayList<String>();
        List<String> educationInputIds = new ArrayList<String>();
        List<Predicate<Map<String, String>>> preferencePredicates = new ArrayList<Predicate<Map<String, String>>>();

        for (Element element : this.getChildren()) {
            PreferenceRow pr = (PreferenceRow) element;
            learningInstitutionInputIds.add(pr.getLearningInstitutionInputId());
            educationInputIds.add(pr.getEducationInputId());
            preferencePredicates.add(
                    validate(
                            new EqualsValidator(pr.getEducationInputId() + "-educationDegree",
                                    ElementUtil.createI18NTextError("yleinen.virheellinenArvo"), "32")));
        }

        listOfValidators.add(new PreferenceTableValidator(learningInstitutionInputIds, educationInputIds));
        Predicate<Map<String, String>> predicate =
                and(
                        not(
                                and(
                                        and(
                                                validate(new EqualsValidator("ammatillinenTutkintoSuoritettu",
                                                        ElementUtil.createI18NTextError("yleinen.virheellinenArvo"), "true")),
                                                validate(new RequiredFieldValidator("ammatillinenTutkintoSuoritettu",
                                                        ElementUtil.createI18NTextError("yleinen.pakollinen")))
                                        ),
                                        or(preferencePredicates)
                                )
                        )
                );

        FunctionalValidator fv = new FunctionalValidator(predicate, this.getId(),
                ElementUtil.createI18NTextError("hakutoiveet.ammatillinenSuoritettu"));
        listOfValidators.add(fv);
        return listOfValidators;
    }
}
