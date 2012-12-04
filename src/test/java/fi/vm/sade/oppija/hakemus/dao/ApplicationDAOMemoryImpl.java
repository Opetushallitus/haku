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
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.lomake.domain.FormId;
import fi.vm.sade.oppija.lomake.domain.User;
import fi.vm.sade.oppija.lomake.validation.ApplicationState;
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
    private final List<Application> hakemukset = new ArrayList<Application>();

    public Application find(final FormId formId, final User user) {
        Collection<Application> kayttajanHakemukset = Collections2.filter(hakemukset, new Predicate<Application>() {
            @Override
            public boolean apply(final Application hakemus) {
                return hakemus.getUser().equals(user) && hakemus.getFormId().equals(formId);
            }
        });
        Application application;
        if (kayttajanHakemukset.isEmpty()) {
            application = new Application(formId, user);
            hakemukset.add(application);
        } else {
            application = kayttajanHakemukset.iterator().next();
        }
        return application;
    }

    @Override
    public List<Application> findAll(final User user) {
        Collection<Application> kayttajanHakemukset = Collections2.filter(hakemukset, new Predicate<Application>() {
            @Override
            public boolean apply(final Application hakemus) {
                return hakemus.getUser().equals(user);
            }
        });
        ArrayList<Application> hakemukset = new ArrayList<Application>();
        hakemukset.addAll(kayttajanHakemukset);
        return hakemukset;
    }

    @Override
    public Application find(String oid) {
        throw new UnsupportedOperationException("not supported");
    }

    @Override
    public String laitaVireille(FormId hakulomakeId, User user) {
        return "1";
    }

    @Override
    public ApplicationState tallennaVaihe(final ApplicationState state) {
        Application application = find(state.getHakemus().getFormId(), state.getHakemus().getUser());
        application.addVaiheenVastaukset(state.getVaiheId(), state.getHakemus().getVastauksetMerged());
        hakemukset.add(application);
        return state;
    }

}
