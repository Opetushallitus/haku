/*
 *
 *  * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *  *
 *  * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 *  * soon as they will be approved by the European Commission - subsequent versions
 *  * of the EUPL (the "Licence");
 *  *
 *  * You may not use this work except in compliance with the Licence.
 *  * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * European Union Public Licence for more details.
 *
 */
package fi.vm.sade.oppija.common.organisaatio.impl;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import fi.vm.sade.oppija.common.organisaatio.Organization;
import fi.vm.sade.oppija.common.organisaatio.SearchCriteria;
import fi.vm.sade.oppija.common.organisaatio.impl.OrganizationServiceMockImpl.OrgNamePredicate;
import fi.vm.sade.oppija.common.organisaatio.impl.OrganizationServiceMockImpl.OrgTypePredicate;

/**
 * Tests for the mock.
 */
public class OrganizationServiceMockImplTest {

    /**
     * Custom impl that initializes with known test data.
     */
    static final OrganizationServiceMockImpl organisaatioService = new OrganizationServiceMockImpl() {

        public void init() {
            add(getOrganization("nimi1", "1", null, Organization.Type.KOULUTUSTOIMIJA));
            add(getOrganization("nimi2", "2", "1", Organization.Type.OPPILAITOS));
            add(getOrganization("nimi3", "3", "1", Organization.Type.OPPILAITOS));
            add(getOrganization("nimi4", "4", "1", Organization.Type.OPPILAITOS));
            add(getOrganization("nimi5", "5", "4", Organization.Type.OPPILAITOS));
        };
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
        assertEquals(1, filter(new OrgNamePredicate("nimi2_fi")).size());
        assertEquals(1, filter(new OrgNamePredicate("nimi2_sv")).size());
        assertEquals(5, filter(new OrgNamePredicate("nimi")).size());
        assertEquals(5, filter(new OrgNamePredicate(null)).size());

        assertEquals(1, filter(new OrgTypePredicate(Organization.Type.KOULUTUSTOIMIJA)).size());
        assertEquals(4, filter(new OrgTypePredicate(Organization.Type.OPPILAITOS)).size());
        assertEquals(5, filter(new OrgTypePredicate(null)).size());

        assertEquals(
                1,
                filter(
                        Predicates.and(new OrgTypePredicate(Organization.Type.KOULUTUSTOIMIJA), new OrgNamePredicate(
                                "nimi1"))).size());
        assertEquals(
                0,
                filter(
                        Predicates.and(new OrgTypePredicate(Organization.Type.KOULUTUSTOIMIJA), new OrgNamePredicate(
                                "nimi2"))).size());
        assertEquals(
                4,
                filter(Predicates.and(new OrgTypePredicate(Organization.Type.OPPILAITOS), new OrgNamePredicate("nimi")))
                        .size());
    }

    @Test
    public void testReadTestData() throws IOException {
        OrganizationServiceMockImpl impl = new OrganizationServiceMockImpl();
        SearchCriteria criteria = new SearchCriteria(null, "Espoo");
        assertTrue("No search results found", impl.search(criteria).size() > 0);
    }
}
