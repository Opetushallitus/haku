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

import com.google.common.base.Function;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionDTO;

/**
 * @author Mikko Majapuro
 */
public class ApplicationOptionDTOToApplicationOptionFunction implements Function<ApplicationOptionDTO, ApplicationOption> {

    @Override
    public ApplicationOption apply(ApplicationOptionDTO applicationOptionDTO) {
        if (applicationOptionDTO != null) {
            ApplicationOption ao = new ApplicationOption();
            ao.setId(applicationOptionDTO.getId());
            ao.setName(applicationOptionDTO.getName());
            ao.setCanBeApplied(applicationOptionDTO.isCanBeApplied());
            ao.setRequiredBaseEducations(applicationOptionDTO.getRequiredBaseEducations());
            ao.setSora(applicationOptionDTO.isSora());
            ao.setSpecificApplicationDates(applicationOptionDTO.isSpecificApplicationDates());
            ao.setTeachingLanguages(applicationOptionDTO.getTeachingLanguages());
            ao.setAoIdentifier(applicationOptionDTO.getAoIdentifier());
            ao.setKaksoistutkinto(applicationOptionDTO.isKaksoistutkinto());
            LearningOpportunityProvider lop = new LearningOpportunityProvider();
            lop.setId(applicationOptionDTO.getProvider().getId());
            lop.setName(applicationOptionDTO.getProvider().getName());
            lop.setAthleteEducation(applicationOptionDTO.getProvider().isAthleteEducation());
            lop.setApplicationSystemIds(applicationOptionDTO.getProvider().getApplicationSystemIds());
            ao.setProvider(lop);

            return ao;
        } else {
            return null;
        }
    }
}
