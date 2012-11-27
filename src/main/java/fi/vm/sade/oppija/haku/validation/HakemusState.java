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

package fi.vm.sade.oppija.haku.validation;


import fi.vm.sade.oppija.haku.domain.Hakemus;

import java.util.HashMap;
import java.util.Map;

public class HakemusState {

    private static final String HAKEMUS_KEY = "hakemus";
    public static final String VALMIS = "valmis";
    private final Map<String, String> errors = new HashMap<String, String>();
    private final Map<String, Object> modelObjects = new HashMap<String, Object>();
    private final String vaiheId;

    public HakemusState(final Hakemus hakemus, final String vaiheId) {
        modelObjects.put(HAKEMUS_KEY, hakemus);
        modelObjects.put("categoryData", hakemus.getVastauksetMerged());
        modelObjects.put("errorMessages", errors);
        this.vaiheId = vaiheId;
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public void addError(Map<String, String> errorMessages) {
        this.errors.putAll(errorMessages);
    }

    public Map<String, String> getErrors() {
        return this.errors;
    }

    public Map<String, Object> getModelObjects() {
        return modelObjects;
    }

    public Hakemus getHakemus() {
        return (Hakemus) modelObjects.get(HAKEMUS_KEY);
    }

    public void setHakemus(final Hakemus hakemus) {
        modelObjects.put(HAKEMUS_KEY, hakemus);
    }

    public String getVaiheId() {
        return vaiheId;
    }

    public boolean isFinalStage() {
        return VALMIS.equals(vaiheId);
    }

}
