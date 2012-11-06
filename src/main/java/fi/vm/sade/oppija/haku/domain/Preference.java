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

package fi.vm.sade.oppija.haku.domain;

/**
 * @author jukka
 * @version 10/18/1210:33 AM}
 * @since 1.1
 */
public class Preference {

    private final String koulutus;

    private final String koulutusId;
    private final Integer order;
    private final String opetusPiste;
    private final String opetusPisteId;

    public Preference(Integer order, String opetusPiste, String opetusPisteId, String koulutus, String koulutusId) {
        this.order = order;
        this.opetusPiste = opetusPiste;
        this.opetusPisteId = opetusPisteId;
        this.koulutus = koulutus;
        this.koulutusId = koulutusId;
    }

    public String getKoulutus() {
        return koulutus;
    }

    public String getKoulutusId() {
        return koulutusId;
    }

    public String getOpetusPiste() {
        return opetusPiste;
    }

    public String getOpetusPisteId() {
        return opetusPisteId;
    }


    public Integer getOrder() {
        return order;
    }
}
