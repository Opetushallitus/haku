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
package fi.vm.sade.oppija.common.valintaperusteet;

public class InputParameter {

    /* syötteen avain, esim "aidinkieli_yo" */
    private final String key;

    /* vaihe? johon liittyy, esim "1" */
    private final String phase;

    /*
     * syötteen tyyppi, esim "DESIMAALILUKU","KOKONAISLUKU", "MERKKIJONO" tai
     * "TOTUUSARVO"
     */
    private final String type;

    public InputParameter(final String key, final String type, final String phase) {
        this.key = key;
        this.phase = phase;
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public String getPhase() {
        return phase;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.format("%s: key='%s' phase='%s' type='%s'", getClass().getName(), key, phase, type);
    }

}
