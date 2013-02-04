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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import fi.vm.sade.organisaatio.api.model.GenericFault;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.FindBasicOrganisaatioChildsToOidTypesParameter;
import fi.vm.sade.organisaatio.api.model.types.FindBasicOrganisaatioChildsToOidTypesResponse;
import fi.vm.sade.organisaatio.api.model.types.FindBasicParentOrganisaatioTypesParameter;
import fi.vm.sade.organisaatio.api.model.types.FindBasicParentOrganisaatioTypesResponse;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioOidListType;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioPerustietoType;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioSearchCriteriaDTO;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioSearchOidType;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioTyyppi;
import fi.vm.sade.organisaatio.api.model.types.RemoveByOidResponseType;
import fi.vm.sade.organisaatio.api.model.types.RemoveByOidType;
import fi.vm.sade.organisaatio.api.model.types.SearchCriteriaDTO;
import fi.vm.sade.organisaatio.api.model.types.YhteystietoArvoDTO;
import fi.vm.sade.organisaatio.api.model.types.YhteystietoDTO;
import fi.vm.sade.organisaatio.api.model.types.YhteystietojenTyyppiDTO;

/**
 * OrganisaatioService mock with limited implementation for methods required by
 * haku.
 */
public class OrganisaatioServiceMockImpl implements OrganisaatioService {

    /**
     * Predicate that matches organization name.
     */
    static class OrgNamePredicate implements Predicate<OrganisaatioPerustietoType> {

        private final String searchString;

        public OrgNamePredicate(String searchString) {
            this.searchString = searchString;
        }

        public boolean apply(OrganisaatioPerustietoType org) {
            final String nameFi = org.getNimiFi();
            final String nameEn = org.getNimiEn();
            final String nameSv = org.getNimiSv();
            return searchString == null ? true : (nameFi != null && nameFi.contains(searchString))
                    || (nameEn != null && nameEn.contains(searchString))
                    || (nameSv != null && nameSv.contains(searchString));
        }
    }

    /**
     * Predicate that matches organization type.
     */
    static class OrgTypePredicate implements Predicate<OrganisaatioPerustietoType> {

        private final OrganisaatioTyyppi tyyppi;

        public OrgTypePredicate(OrganisaatioTyyppi tyyppi) {
            this.tyyppi = tyyppi;
        }

        public boolean apply(OrganisaatioPerustietoType org) {
            return tyyppi == null ? true : org.getTyypit().contains(tyyppi);
        }
    }

    private final Multimap<String, OrganisaatioPerustietoType> parentChild = ArrayListMultimap.create();

    private final HashMap<String, OrganisaatioPerustietoType> oidOrg = new HashMap<String, OrganisaatioPerustietoType>();
    final ArrayList<OrganisaatioPerustietoType> orgs = Lists.newArrayList();

    protected OrganisaatioPerustietoType oph;

    public OrganisaatioServiceMockImpl() {
        init();
    }

    protected void index(OrganisaatioPerustietoType org) {
        orgs.add(org);
        oidOrg.put(org.getOid(), org);
        parentChild.put(org.getParentOid(), org);
    }

    protected void init() {

    }

    public OrganisaatioDTO createOrganisaatio(OrganisaatioDTO organisaatio) throws GenericFault {
        throw new RuntimeException("not implemented");
    }

    public YhteystietoDTO createYhteystieto(YhteystietoDTO yhteystieto) throws GenericFault {
        throw new RuntimeException("not implemented");
    }

    public YhteystietojenTyyppiDTO createYhteystietojenTyyppi(YhteystietojenTyyppiDTO yhteystietojenTyyppi)
            throws GenericFault {
        throw new RuntimeException("not implemented");
    }

    /*
     * Fill parents and childs for matches.
     */
    List<OrganisaatioPerustietoType> fillTree(final List<OrganisaatioPerustietoType> matches) {
        Map<String, OrganisaatioPerustietoType> result = Maps.newHashMap();
        for (OrganisaatioPerustietoType org : matches) {
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
    private void addParents(final OrganisaatioPerustietoType org, final Map<String, OrganisaatioPerustietoType> result) {
        Preconditions.checkNotNull(org);

        final OrganisaatioPerustietoType parent = oidOrg.get(org.getParentOid());
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
    private void addChildren(final OrganisaatioPerustietoType org, final Map<String, OrganisaatioPerustietoType> result) {
        Preconditions.checkNotNull(org);

        Collection<OrganisaatioPerustietoType> children = parentChild.get(org.getOid());
        for (OrganisaatioPerustietoType child : children) {
            if (!result.containsKey(child.getOid())) {
                result.put(child.getOid(), child);
            }
            addChildren(child, result);
        }
    }

    public FindBasicOrganisaatioChildsToOidTypesResponse findBasicOrganisaatioChildsToOid(
            FindBasicOrganisaatioChildsToOidTypesParameter parameters) {
        throw new RuntimeException("not implemented");
    }

    public FindBasicParentOrganisaatioTypesResponse findBasicParentOrganisaatios(
            FindBasicParentOrganisaatioTypesParameter parameters) {
        throw new RuntimeException("not implemented");
    }

    public OrganisaatioDTO findByOid(String oid) {
        throw new RuntimeException("not implemented");
    }

    public List<OrganisaatioDTO> findByOidList(List<String> oids, int maxResults) {
        throw new RuntimeException("not implemented");
    }

    public OrganisaatioOidListType findChildrenOidsByOid(OrganisaatioSearchOidType parameters) {
        throw new RuntimeException("not implemented");
    }

    public List<OrganisaatioDTO> findChildrenTo(String oid) {
        throw new RuntimeException("not implemented");
    }

    public List<OrganisaatioDTO> findParentsByOidList(List<String> oids) {
        throw new RuntimeException("not implemented");
    }

    public List<OrganisaatioDTO> findParentsTo(String oid) {
        throw new RuntimeException("not implemented");
    }

    public List<YhteystietoArvoDTO> findYhteystietoArvosForOrganisaatio(String organisaatioOid) {
        throw new RuntimeException("not implemented");
    }

    public List<YhteystietojenTyyppiDTO> findYhteystietojenTyyppis(SearchCriteriaDTO yhteystietojenTyyppiSearchCriteria) {
        throw new RuntimeException("not implemented");
    }

    public List<YhteystietojenTyyppiDTO> findYhteystietoMetadataForOrganisaatio(List<String> organisaatioTyyppi) {
        throw new RuntimeException("not implemented");
    }

    public List<YhteystietoDTO> findYhteystietos(SearchCriteriaDTO yhteystietoSearchCriteria) {
        throw new RuntimeException("not implemented");
    }

    protected OrganisaatioPerustietoType getPerustieto(final String name, final String oid, final String parentOid,
            OrganisaatioTyyppi... tyypit) {
        OrganisaatioPerustietoType org = new OrganisaatioPerustietoType() {

            @Override
            public String toString() {
                return "oid:" + this.getOid() + " pOid:" + this.getParentOid() + " n:" + this.getNimiFi();
            }
        };
        org.setNimiFi(name + "_fi");
        org.setNimiEn(name + "_en");
        org.setNimiSv(name + "_sv");
        org.setOid(oid);
        org.setParentOid(parentOid);
        org.getTyypit().addAll(Arrays.asList(tyypit));
        return org;
    }

    public String ping(String arg0) throws GenericFault {
        throw new RuntimeException("not implemented");
    }

    public YhteystietoDTO readYhteystieto(String yhteystietoOid) {
        throw new RuntimeException("not implemented");
    }

    public YhteystietojenTyyppiDTO readYhteystietojenTyyppi(String yhteystietojenTyyppiOid) {
        throw new RuntimeException("not implemented");
    }

    public RemoveByOidResponseType removeOrganisaatioByOid(RemoveByOidType parameters) throws GenericFault {
        throw new RuntimeException("not implemented");
    }

    public void removeYhteystietojenTyyppiByOid(String oid) throws GenericFault {
        throw new RuntimeException("not implemented");
    }

    public List<OrganisaatioPerustietoType> searchBasicOrganisaatios(
            OrganisaatioSearchCriteriaDTO organisaatioSearchCriteria) {

        Predicate<OrganisaatioPerustietoType> predicate = Predicates.and(new OrgNamePredicate(
                organisaatioSearchCriteria.getSearchStr()),
                new OrgTypePredicate(OrganisaatioTyyppi.valueOf(organisaatioSearchCriteria.getOrganisaatioTyyppi())));

        return Lists.newArrayList(Iterables.filter(orgs, predicate));
    }

    public List<OrganisaatioDTO> searchOrganisaatios(OrganisaatioSearchCriteriaDTO organisaatioSearchCriteria) {
        throw new RuntimeException("not implemented");
    }

    public OrganisaatioDTO updateOrganisaatio(OrganisaatioDTO organisaatio) throws GenericFault {
        throw new RuntimeException("not implemented");
    }

    public void updateYhteystieto(YhteystietoDTO yhteystieto) throws GenericFault {
        throw new RuntimeException("not implemented");
    }

    public void updateYhteystietojenTyyppi(YhteystietojenTyyppiDTO yhteystietojenTyyppi) throws GenericFault {
        throw new RuntimeException("not implemented");
    }

}
