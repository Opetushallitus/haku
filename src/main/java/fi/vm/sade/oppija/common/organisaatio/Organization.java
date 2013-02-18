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

import com.google.common.collect.ImmutableList;
import fi.vm.sade.oppija.lomake.domain.I18nText;

import java.util.Date;
import java.util.List;

public class Organization {

    private Date endDate;

    private I18nText name;

    private String oid;

    private List<String> types;

    private String parentOid;

    private Date startDate;

    public Organization() {
    }

    public Organization(final I18nText name, final String oid, final String parentOid,
                        final List<String> types, Date startDate, Date endDate) {
        if (oid == null)
            throw new NullPointerException("Oid cannot be null");
        this.name = name;
        this.oid = oid;
        this.parentOid = parentOid;
        this.types = ImmutableList.copyOf(types);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Organization that = (Organization) o;

        if (!oid.equals(that.oid)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return oid.hashCode();
    }

    public Date getEndDate() {
        return endDate;
    }

    public I18nText getName() {
        return name;
    }

    public String getOid() {
        return oid;
    }

    public String getParentOid() {
        return parentOid;
    }

    public Date getStartDate() {
        return startDate;
    }

    public List<String> getTypes() {
        return types;
    }
}
