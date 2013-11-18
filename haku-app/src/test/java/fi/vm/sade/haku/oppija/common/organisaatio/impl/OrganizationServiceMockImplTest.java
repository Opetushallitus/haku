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
package fi.vm.sade.haku.oppija.common.organisaatio.impl;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import fi.vm.sade.haku.oppija.common.organisaatio.Organization;
import fi.vm.sade.haku.oppija.common.organisaatio.impl.OrganizationServiceMockImpl.OrgNamePredicate;
import fi.vm.sade.haku.oppija.common.organisaatio.impl.OrganizationServiceMockImpl.OrgTypePredicate;
import fi.vm.sade.organisaatio.api.search.OrganisaatioSearchCriteria;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the mock.
 */
public class OrganizationServiceMockImplTest {

    /**
     * Custom impl that initializes with known test data.
     */
    static final OrganizationServiceMockImpl organisaatioService = new OrganizationServiceMockImpl() {

        final Date now = new Date();
        final Date start = new Date(now.getTime() - 1000 * 60 * 60 * 24);
        final Date end = new Date(now.getTime() + 1000 * 60 * 60 * 24);

        public void init() {
            add(getOrganization("nimi1", "1", null, start, end, "Koulutustoimija"));
            add(getOrganization("nimi2", "2", "1", start, end, "Oppilaitos"));
            add(getOrganization("nimi3", "3", "1", start, end, "Oppilaitos"));
            add(getOrganization("nimi4", "4", "1", start, end, "Oppilaitos"));
            add(getOrganization("nimi5", "5", "4", start, end, "Oppilaitos"));
        }
    };

    /**
     * Filter orgs by predicate
     */
    private List<Organization> filter(final Predicate<?> predicate) {
        final List<Organization> result = Lists.newArrayList(Iterables.filter(organisaatioService.orgs,
                (Predicate<Organization>) predicate));
        return result;
    }

    @Test
    public void testFillTree() {
        assertEquals(2, organisaatioService.fillTree(filter(new OrgNamePredicate("nimi2"))).size());
        assertEquals(5, organisaatioService.fillTree(filter(new OrgNamePredicate("nimi1"))).size());
        assertEquals(3, organisaatioService.fillTree(filter(new OrgNamePredicate("nimi4"))).size());
    }

    @Test
    public void testPredicates() {
        assertEquals(1, filter(new OrgNamePredicate("nimi2")).size());
        assertEquals(1, filter(new OrgNamePredicate("nimi2")).size());
        assertEquals(5, filter(new OrgNamePredicate("nimi")).size());
        assertEquals(5, filter(new OrgNamePredicate(null)).size());

        assertEquals(1, filter(new OrgTypePredicate("Koulutustoimija")).size());
        assertEquals(4, filter(new OrgTypePredicate("Oppilaitos")).size());
        assertEquals(5, filter(new OrgTypePredicate(null)).size());

        assertEquals(
                1,
                filter(
                        Predicates.and(new OrgTypePredicate("Koulutustoimija"), new OrgNamePredicate(
                                "nimi1"))).size());
        assertEquals(
                0,
                filter(
                        Predicates.and(new OrgTypePredicate("Koulutustoimija"), new OrgNamePredicate(
                                "nimi2"))).size());
        assertEquals(
                4,
                filter(Predicates.and(new OrgTypePredicate("Oppilaitos"), new OrgNamePredicate("nimi")))
                        .size());
    }

    @Test
    public void testReadTestData() throws IOException {
        OrganizationServiceMockImpl impl = new OrganizationServiceMockImpl();
        OrganisaatioSearchCriteria criteria = new OrganisaatioSearchCriteria();
        criteria.setSearchStr("espoo");
        assertTrue("No search results found", impl.search(criteria).size() > 0);
    }
}
