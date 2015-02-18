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

package fi.vm.sade.haku.oppija.common.koulutusinformaatio.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import fi.vm.sade.haku.oppija.common.koulutusinformaatio.ApplicationOption;
import fi.vm.sade.haku.oppija.common.koulutusinformaatio.ApplicationOptionDTOToApplicationOptionFunction;
import fi.vm.sade.haku.oppija.common.koulutusinformaatio.ApplicationOptionService;
import fi.vm.sade.haku.oppija.common.koulutusinformaatio.KoulutusinformaatioService;

/**
 * @author Mikko Majapuro
 */
@Service
public class ApplicationOptionServiceImpl implements ApplicationOptionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationOptionServiceImpl.class);
    private final KoulutusinformaatioService koulutusinformaatioService;
    private final ApplicationOptionDTOToApplicationOptionFunction converterFunction;

    @Autowired
    public ApplicationOptionServiceImpl(KoulutusinformaatioService koulutusinformaatioService) {
        this.koulutusinformaatioService = koulutusinformaatioService;
        converterFunction = new ApplicationOptionDTOToApplicationOptionFunction();
    }

    @Override
    public ApplicationOption get(String oid) {
        return get(oid, null);
    }

    @Override
    public ApplicationOption get(String oid, String lang) {
        LOGGER.debug("get application option : {}", oid);
        return converterFunction.apply(koulutusinformaatioService.getApplicationOption(oid, lang));
    }
}
