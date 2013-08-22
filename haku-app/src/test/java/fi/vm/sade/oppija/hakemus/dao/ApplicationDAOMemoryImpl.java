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

package fi.vm.sade.oppija.hakemus.dao;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.mongodb.DBObject;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.domain.dto.ApplicationSearchResultDTO;
import fi.vm.sade.oppija.lomake.validation.ApplicationState;
import fi.vm.sade.oppija.ui.HakuPermissionService;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author jukka
 * @version 9/27/129:21 AM}
 * @since 1.1
 */
@Component("sessionDataHolder")
public class ApplicationDAOMemoryImpl implements Serializable, ApplicationDAO {

    private static final long serialVersionUID = -3751714345380438532L;
    public final List<Application> hakemukset = new ArrayList<Application>();

    private HakuPermissionService hakuPermissionService;

    public List<Application> find(final Application application) {
        Collection<Application> applications = Collections2.filter(hakemukset, new Predicate<Application>() {
            @Override
            public boolean apply(final Application hakemus) {
                return hakemus.getUser().equals(application.getUser()) && hakemus.getApplicationSystemId().equals(application.getApplicationSystemId());
            }
        });
        return Lists.newArrayList(applications);
    }

    @Override
    public List<Application> find(DBObject query) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void update(Application o, Application n) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void save(Application application) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Application findDraftApplication(Application application) {
        return application;
    }

    @Override
    public List<Application> findByApplicationSystem(String asId) {
        return null;
    }

    @Override
    public List<Application> findByApplicationSystemAndApplicationOption(String asId, String aoId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<Application> findByApplicationOption(List<String> aoIds) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean checkIfExistsBySocialSecurityNumber(String asId, String ssn) {
        return false;
    }

    @Override
    public ApplicationSearchResultDTO findByApplicantName(String term, ApplicationQueryParameters applicationQueryParameters) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ApplicationSearchResultDTO findByApplicantSsn(String term, ApplicationQueryParameters applicationQueryParameters) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ApplicationSearchResultDTO findByOid(String term, ApplicationQueryParameters applicationQueryParameters) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ApplicationSearchResultDTO findByApplicationOid(String term, ApplicationQueryParameters applicationQueryParameters) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ApplicationSearchResultDTO findByUserOid(String term, ApplicationQueryParameters applicationQueryParameters) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ApplicationSearchResultDTO findAllFiltered(ApplicationQueryParameters applicationQueryParameters) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ApplicationState tallennaVaihe(final ApplicationState state) {
        Application application = find(new Application(state.getApplication().getApplicationSystemId(), state.getApplication().getUser())).get(0);
        application.addVaiheenVastaukset(state.getPhaseId(), state.getApplication().getVastauksetMerged());
        hakemukset.add(application);
        return state;
    }

    @Override
    public ApplicationSearchResultDTO findByApplicantDob(String term, ApplicationQueryParameters applicationQueryParameters) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setHakuPermissionService(HakuPermissionService hakuPermissionService) {
        this.hakuPermissionService = hakuPermissionService;
    }

    @Override
    public void updateKeyValue(String oid, String key, String value) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
