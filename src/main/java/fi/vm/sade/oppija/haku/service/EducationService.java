package fi.vm.sade.oppija.haku.service;

import fi.vm.sade.oppija.haku.domain.Opetuspiste;

import java.util.List;

/**
 * Service for searching education institutes
 * @author Mikko Majapuro
 */
public interface EducationService {

    /**
     *
     * @param term search term
     * @return list of education institutes matching search term
     */
    List<Opetuspiste> searchEducationInstitutes(final String term);
}
