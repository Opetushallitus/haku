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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * European Union Public Licence for more details.
 */

package fi.vm.sade.oppija.haku.service.impl;

import fi.vm.sade.oppija.haku.domain.Hakemus;
import fi.vm.sade.oppija.haku.validation.HakemusState;

/**
 * @author jukka
 * @version 11/21/124:44 PM}
 * @since 1.1
 */
public class VireillepanoState extends HakemusState {

    public static final String VAIHEID = "send";

    public VireillepanoState(Hakemus hakemus) {
        super(hakemus, VAIHEID);
    }

    @Override
    public boolean mustValidate() {
        return true;
    }

    @Override
    public boolean isFinalStage() {
        return true;
    }
}
