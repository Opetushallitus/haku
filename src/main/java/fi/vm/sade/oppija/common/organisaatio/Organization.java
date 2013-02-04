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
package fi.vm.sade.oppija.common.organisaatio;

import java.util.List;

import com.google.common.collect.ImmutableList;

import fi.vm.sade.oppija.lomake.domain.I18nText;

public class Organization {

    public static enum Type {
        KOULUTUSTOIMIJA, MUU_ORGANISAATIO, OPETUSPISTE, OPPILAITOS, OPPISOPIMUSTOIMIPISTE
    }

    private final I18nText name;

    private final String oid;

    private final String parentOid;

    public String getParentOid() {
        return parentOid;
    }

    private final List<Type> types;

    public Organization(final I18nText name, final String oid, final String parentOid, final List<Type> types) {
        if (oid == null)
            throw new NullPointerException("Oid cannot be null");
        this.name = name;
        this.oid = oid;
        this.parentOid = parentOid;
        this.types = ImmutableList.copyOf(types);
    }

    public I18nText getName() {
        return name;
    }

    public String getOid() {
        return oid;
    }

    public List<Type> getTypes() {
        return types;
    }

    public boolean equals(Object oOther) {
        if (oOther instanceof Organization) {
            Organization other = (Organization) oOther;
            return other.getOid().equals(oid);
        }

        return false;

    };
}
