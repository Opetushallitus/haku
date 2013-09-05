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

package fi.vm.sade.oppija.lomake;

import fi.vm.sade.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.oppija.lomake.domain.elements.Element;
import fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil;


public class ApplicationSystemHelper {
    private final ApplicationSystem applicationSystem;

    public ApplicationSystemHelper(final ApplicationSystem applicationSystem) {
        this.applicationSystem = applicationSystem;

    }

    public String getFormUrl(final String id) {
        return "lomake/" + ElementUtil.getPath(applicationSystem, id);
    }


    public Element getFirstPhase() {
        return this.applicationSystem.getForm().getChildren().iterator().next();
    }

    public String getStartUrl() {
        return getFormUrl(getFirstPhase().getId());
    }
}
