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
package fi.vm.sade.haku.oppija.common.organisaatio;

import fi.vm.sade.organisaatio.service.search.SearchCriteria;

import java.util.List;

public interface OrganizationService {

    /**
     * Search Organisations from Organisaatio service. Throws
     * {@link java.io.IOException} if there's a problem accessing the service.
     *
     * @param criteria
     * @return
     */
    List<Organization> search(SearchCriteria searchCriteria);

    List<String> findParentOids(String organizationOid);

    Organization findByOid(String oid);

    List<Organization> findByOppilaitosnumero(List<String> oppilaitosnumeros);
}
