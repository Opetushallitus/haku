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

package fi.vm.sade.haku.oppija.hakemus.it.dao;

import org.apache.commons.lang.StringUtils;

import java.util.*;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class ApplicationQueryParameters {
    private final List<String> state;
    private final String paymentState;
    private final Boolean preferenceChecked;
    private final String preferenceEligibility;
    private final List<String> asIds;
    private final List<String> oids;
    private final String aoId;
    private final String lopOid;
    private final List<String> aoOids;
    private final List<String> personOids;
    private final String groupOid;
    private final Set<String> baseEducation;
    private final boolean discretionaryOnly;
    private final boolean primaryPreferenceOnly;
    private final String sendingSchool;
    private final String sendingClass;
    private final Date updatedAfter;
    private final int start;
    private final int rows;
    private final String orderBy;
    private final int orderDir;
    private final String searchTerms;
    private final String organizationFilter;
    private final Boolean modifiedApplicationsOnly;

    public ApplicationQueryParameters(final String searchTerms, final List<String> state, final String paymentState,
                                      final Boolean preferenceChecked, final String preferenceEligibility,
                                      final List<String> asIds, final String aoId, final String lopOid,
                                      final List<String> aoOids, final List<String> oids, final List<String> personOids,
                                      final String groupOid, final Set<String> baseEducation,
                                      final Boolean discretionaryOnly, final Boolean primaryPreferenceOnly,
                                      final String sendingSchool, final String sendingClass, final Date updatedAfter,
                                      final int start, final int rows, final String orderBy, final int orderDir,
                                      final String organizationFilter, final Boolean modifiedApplicationsOnly) {
        this.searchTerms = searchTerms;
        this.lopOid = isEmpty(lopOid) ? null : lopOid;
        this.asIds = asIds;
        this.oids = oids;
        this.state = state;
        this.paymentState = paymentState;
        this.preferenceChecked = preferenceChecked;
        this.preferenceEligibility = preferenceEligibility;
        this.aoId = isEmpty(aoId) ? null : aoId;
        this.aoOids = nonEmptyStrings(aoOids);
        this.personOids = nonEmptyStrings(personOids);
        this.groupOid = isEmpty(groupOid) ? null : groupOid;
        this.baseEducation = baseEducation;
        this.discretionaryOnly = discretionaryOnly == null ? false : discretionaryOnly;
        this.primaryPreferenceOnly = primaryPreferenceOnly == null ? false : primaryPreferenceOnly;
        this.sendingSchool = sendingSchool;
        this.sendingClass = sendingClass;
        this.updatedAfter = updatedAfter;
        this.start = start;
        this.rows = rows;
        this.orderBy = orderBy;
        this.orderDir = orderDir;
        this.organizationFilter = organizationFilter;
        this.modifiedApplicationsOnly = modifiedApplicationsOnly;
    }

    private List<String> nonEmptyStrings(List<String> xs) {
        if (xs == null) return Collections.emptyList();
        List<String> nonEmpty = new ArrayList<>();
        for (String x : xs) {
            if (!StringUtils.isBlank(x)) nonEmpty.add(x);
        }
        return nonEmpty;
    }

    public List<String> getOids() {
        return oids;
    }

    public List<String> getState() {
        return state;
    }

    public String getPaymentState() {
        return paymentState;
    }

    public Boolean getPreferenceChecked() {
        return preferenceChecked;
    }

    public String getPreferenceEligibility() {
        return preferenceEligibility;
    }

    public List<String> getAsIds() {
        return this.asIds;
    }

    public String getAoId() {
        return aoId;
    }

    public String getLopOid() {
        return lopOid;
    }

    public List<String> getAoOids() {
        return aoOids;
    }

    public String getGroupOid() {
        return groupOid;
    }

    public int getStart() {
        return start;
    }

    public int getRows() {
        return rows;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public int getOrderDir() {
        return orderDir;
    }

    public boolean isDiscretionaryOnly() {
        return discretionaryOnly;
    }

    public boolean isPrimaryPreferenceOnly() {
        return primaryPreferenceOnly;
    }

    public String getSendingSchool() {return sendingSchool; }

    public String getSendingClass() {return sendingClass; }

    public Date getUpdatedAfter() { return updatedAfter; }

    public Set<String> getBaseEducation() {
        return baseEducation;
    }

    public String getSearchTerms() {
        return searchTerms != null ? searchTerms : "";
    }

    public List<String> getPersonOids() {
        return personOids;
    }

    public String getOrganizationFilter() {
        return organizationFilter;
    }

    public Boolean getModifiedApplicationsOnly() {
        return modifiedApplicationsOnly;
    }
}
