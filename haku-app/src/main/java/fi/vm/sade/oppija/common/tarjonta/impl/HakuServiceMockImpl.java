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

package fi.vm.sade.oppija.common.tarjonta.impl;

import com.google.common.collect.Lists;
import fi.vm.sade.oppija.common.tarjonta.HakuService;
import fi.vm.sade.oppija.lomake.domain.ApplicationSystem;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Mikko Majapuro
 */
@Service
@Profile("default")
public class HakuServiceMockImpl implements HakuService {

    @Override
    public List<ApplicationSystem> getApplicationSystems() {
        return Lists.newArrayList();
    }
}
