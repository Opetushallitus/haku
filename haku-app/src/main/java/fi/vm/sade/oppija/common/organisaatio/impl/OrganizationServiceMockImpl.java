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
package fi.vm.sade.oppija.common.organisaatio.impl;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.*;
import fi.vm.sade.oppija.common.organisaatio.Organization;
import fi.vm.sade.oppija.common.organisaatio.OrganizationService;
import fi.vm.sade.oppija.common.organisaatio.SearchCriteria;
import fi.vm.sade.oppija.lomake.domain.I18nText;
import fi.vm.sade.oppija.lomake.domain.exception.ConfigurationException;
import fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioPerustietoType;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Mock that returns test data.
 */
@Service
@Profile("dev")
public class OrganizationServiceMockImpl implements OrganizationService {

    /**
     * Predicate that matches organization name.
     */
    static class OrgNamePredicate implements Predicate<Organization> {

        private final String searchString;

        public OrgNamePredicate(String searchString) {
            this.searchString = searchString;
        }

        public boolean apply(Organization org) {
            final String nameFi = org.getName().getTranslations().get("fi");
            final String nameEn = org.getName().getTranslations().get("en");
            final String nameSv = org.getName().getTranslations().get("sv");
            return searchString == null || (nameFi != null && nameFi.contains(searchString))
                    || (nameEn != null && nameEn.contains(searchString))
                    || (nameSv != null && nameSv.contains(searchString));
        }
    }

    static class OrgIncludePassivePredicate implements Predicate<Organization> {

        private final boolean includePassive;

        public OrgIncludePassivePredicate(boolean includePassive) {
            this.includePassive = includePassive;
        }

        public boolean apply(Organization org) {
            return !(!includePassive && org.getEndDate() != null) || org.getEndDate().before(new Date());
        }
    }

    static class OrgIncludePlannedPredicate implements Predicate<Organization> {

        private final boolean includePlanned;

        public OrgIncludePlannedPredicate(boolean includePlanned) {
            this.includePlanned = includePlanned;
        }

        public boolean apply(Organization org) {
            if (!includePlanned) {
                return org.getStartDate().before(new Date());
            }
            return true;
        }
    }

    /**
     * Predicate that matches organization type.
     */
    static class OrgTypePredicate implements Predicate<Organization> {

        private final String type;

        public OrgTypePredicate(String tyyppi) {
            this.type = tyyppi;
        }

        public boolean apply(Organization org) {
            return type == null || org.getTypes().contains(type);
        }
    }

    static class OrgOidPredicate implements Predicate<Organization> {

        private final String oid;

        public OrgOidPredicate(String oid) {
            this.oid = oid;
        }

        public boolean apply(Organization org) {
            return org.getOid().equals(this.oid);
        }
    }

    private final Multimap<String, Organization> parentChild = ArrayListMultimap.create();

    private final Map<String, Organization> oidOrg = new HashMap<String, Organization>();
    final List<Organization> orgs = Lists.newArrayList();

    protected OrganisaatioPerustietoType oph;

    public OrganizationServiceMockImpl() {
        init();
    }

    protected void add(Organization org) {
        orgs.add(org);
        oidOrg.put(org.getOid(), org);
        parentChild.put(org.getParentOid(), org);
    }

    protected void init() {
        final InputStream input = getClass().getResourceAsStream("/org-mock-data.json");
        final ObjectMapper mapper = new ObjectMapper();
        try {
            for (Organization org : mapper.readValue(input, Organization[].class)) {
                add(org);
            }
        } catch (IOException ioe) {
            throw new ConfigurationException("Failed to initialize mock data", ioe);
        }
    }

    /*
     * Fill parents and childs for matches.
     */
    List<Organization> fillTree(final List<Organization> matches) {
        final Map<String, Organization> result = Maps.newHashMap();
        for (Organization org : matches) {
            result.put(org.getOid(), org);
            addParents(org, result);
            addChildren(org, result);
        }
        return Lists.newArrayList(result.values());
    }

    /**
     * Add parent orgs recursively
     *
     * @param org
     * @param result
     */
    private void addParents(final Organization org, final Map<String, Organization> result) {
        Preconditions.checkNotNull(org);

        final Organization parent = oidOrg.get(org.getParentOid());
        if (parent != null && !result.containsKey(parent.getOid())) {
            result.put(org.getParentOid(), parent);
        }

        if (parent != null) {
            addParents(parent, result);
        }

    }

    /**
     * Add children orgs recursively.
     *
     * @param org
     * @param result
     */
    private void addChildren(final Organization org, final Map<String, Organization> result) {
        Preconditions.checkNotNull(org);

        final Collection<Organization> children = parentChild.get(org.getOid());
        for (Organization child : children) {
            if (!result.containsKey(child.getOid())) {
                result.put(child.getOid(), child);
            }
            addChildren(child, result);
        }
    }

    protected Organization getOrganization(final String name, final String oid, final String parentOid,
                                           Date startDate, Date endDate, String... types) {
        final I18nText orgName = ElementUtil.createI18NAsIs(name);
        return new Organization(orgName, oid, parentOid, Arrays.asList(types), startDate, endDate);
    }

    private I18nText getI18nText(String... kv) {
        final HashMap<String, String> translations = Maps.newHashMap();
        for (int i = 0; i < kv.length / 2; i++) {
            translations.put(kv[i * 2], kv[i * 2 + 1]);
        }
        return new I18nText(translations);
    }

    @Override
    public List<Organization> search(SearchCriteria criteria) {
        @SuppressWarnings("unchecked")
        final Predicate<Organization> predicate = Predicates.and(new OrgNamePredicate(criteria.getSearchString()),
                new OrgTypePredicate(criteria.getOrganizationType()),
                new OrgIncludePassivePredicate(criteria.isIncludePassive()),
                new OrgIncludePlannedPredicate(criteria.isIncludePlanned()));
        return Lists.newArrayList(Iterables.filter(orgs, predicate));
    }

    @Override
    public List<String> findParentOids(String organizationOid) {
        List<String> oids = new ArrayList<String>();
        return findParentOids(oids, organizationOid);
    }

    private List<String> findParentOids(List<String> oids, String organizationOid) {
        List<Organization> maybeOrg = Lists.newArrayList(Iterables.filter(orgs, new OrgOidPredicate(organizationOid)));
        if (maybeOrg == null || maybeOrg.isEmpty()) {
            return oids;
        }
        Organization org = maybeOrg.get(0);
        String parentOid = org.getParentOid();
        if (isNotEmpty(parentOid)) {
            oids.add(parentOid);
            return findParentOids(oids, parentOid);
        }
        return oids;

    }
}
