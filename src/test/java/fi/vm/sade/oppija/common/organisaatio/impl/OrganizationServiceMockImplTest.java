package fi.vm.sade.oppija.common.organisaatio.impl;

import static junit.framework.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import fi.vm.sade.oppija.common.organisaatio.impl.OrganisaatioServiceMockImpl.OrgNamePredicate;
import fi.vm.sade.oppija.common.organisaatio.impl.OrganisaatioServiceMockImpl.OrgTypePredicate;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioPerustietoType;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioTyyppi;

/**
 * Tests for the mock.
 */
public class OrganizationServiceMockImplTest {

    /**
     * Custom impl that initializes with known test data.
     */
    static final OrganisaatioServiceMockImpl organisaatioService = new OrganisaatioServiceMockImpl() {

        public void init() {
            index(getPerustieto("nimi1", "1", null, OrganisaatioTyyppi.KOULUTUSTOIMIJA));
            index(getPerustieto("nimi2", "2", "1", OrganisaatioTyyppi.OPPILAITOS));
            index(getPerustieto("nimi3", "3", "1", OrganisaatioTyyppi.OPPILAITOS));
            index(getPerustieto("nimi4", "4", "1", OrganisaatioTyyppi.OPPILAITOS));
            index(getPerustieto("nimi5", "5", "4", OrganisaatioTyyppi.OPPILAITOS));
        };
    };

    /**
     * Filter ors by predicate
     */
    private List<OrganisaatioPerustietoType> filter(final Predicate<?> predicate) {
        final List<OrganisaatioPerustietoType> result = Lists.newArrayList(Iterables.filter(organisaatioService.orgs,
                (Predicate<OrganisaatioPerustietoType>)predicate));
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

        assertEquals(1, filter(new OrgTypePredicate(OrganisaatioTyyppi.KOULUTUSTOIMIJA)).size());
        assertEquals(4, filter(new OrgTypePredicate(OrganisaatioTyyppi.OPPILAITOS)).size());
        assertEquals(5, filter(new OrgTypePredicate(null)).size());

        assertEquals(
                1,
                filter(
                        Predicates.and(new OrgTypePredicate(OrganisaatioTyyppi.KOULUTUSTOIMIJA), new OrgNamePredicate(
                                "nimi1"))).size());
        assertEquals(
                0,
                filter(
                        Predicates.and(new OrgTypePredicate(OrganisaatioTyyppi.KOULUTUSTOIMIJA), new OrgNamePredicate(
                                "nimi2"))).size());
        assertEquals(
                4,
                filter(
                        Predicates.and(new OrgTypePredicate(OrganisaatioTyyppi.OPPILAITOS),
                                new OrgNamePredicate("nimi"))).size());
    }
}
