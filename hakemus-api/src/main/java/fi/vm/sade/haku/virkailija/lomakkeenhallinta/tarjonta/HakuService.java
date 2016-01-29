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

package fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta;

import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;

import java.util.List;

/**
 * @author Mikko Majapuro
 */
public interface HakuService {

    /**
     * NOTE Does not return hakukohteet, because that tarjonta API is too slow.
     * Use getApplicationSystem for getting invidual application systems with all info.
     */
    List<ApplicationSystem> getApplicationSystems();

    ApplicationSystem getApplicationSystem(String oid);

    HakuV1RDTO getRawApplicationSystem(String oid);

    List<String> getRelatedApplicationOptionIds(String oid);

    boolean kayttaaJarjestelmanLomaketta(String oid);
}
