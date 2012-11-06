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

package fi.vm.sade.oppija.haku.dao.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import fi.vm.sade.oppija.haku.dao.ApplicationDAO;
import fi.vm.sade.oppija.haku.domain.Hakemus;
import fi.vm.sade.oppija.haku.domain.HakemusId;
import fi.vm.sade.oppija.haku.domain.User;
import fi.vm.sade.oppija.haku.service.TimeStampModifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Hannu Lyytikainen
 */
@Service("applicationDAOMongoImpl")
public class ApplicationDAOMongoImpl extends AbstractDAOMongoImpl implements ApplicationDAO {

    public static final String HAKEMUS_ID = "hakemusId";
    public static final String USER_ID = "userid";
    public static final String VAIHE_ID = "vaiheId";
    public static final String HAKEMUS_DATA = "hakemusData";
    public static final String HAKEMUS = "hakemus";

    @Override
    public void update(Hakemus hakemus) {
        DBObject query = new BasicDBObject();
        query.put(HAKEMUS_ID, hakemus.getHakemusId().asKey());

        new TimeStampModifier(hakemus.getValues()).updateModified();
        DBObject newApplication = new BasicDBObject();
        newApplication.put(USER_ID, hakemus.getUser().getUserName());
        newApplication.put(HAKEMUS_ID, hakemus.getHakemusId().asKey());
        newApplication.put(VAIHE_ID, hakemus.getHakemusId().getCategoryId());

        Hakemus existing = find(hakemus.getHakemusId(), hakemus.getUser());
        existing.getValues().putAll(hakemus.getValues());
        newApplication.put(HAKEMUS_DATA, existing.getValues());

        getCollection().update(query, newApplication, true, false);

    }

    @Override
    protected String getCollectionName() {
        return HAKEMUS;
    }

    @Override
    public Hakemus find(HakemusId hakemusId, User user) {
        DBObject dbObject = new BasicDBObject();
        dbObject.put(HAKEMUS_ID, hakemusId.asKey());
        dbObject.put(USER_ID, user.getUserName());
        final DBObject one = getCollection().findOne(dbObject);
        Map<String, String> map = new HashMap<String, String>();
        new TimeStampModifier(map).updateCreated();
        if (one != null) {
            map = (Map<String, String>) one.toMap().get(HAKEMUS_DATA);
        }
        return new Hakemus(hakemusId, map, user);
    }

    @Override
    public List<Hakemus> findAll(User user) {
        List<Hakemus> list = new ArrayList<Hakemus>();
        DBObject dbObject = new BasicDBObject();
        dbObject.put(USER_ID, user.getUserName());
        final DBCursor dbObjects = getCollection().find(dbObject);
        for (DBObject object : dbObjects) {
            final Map map = object.toMap();

            final String hakemusIdString = map.get(HAKEMUS_ID).toString();
            final String vaihe = map.get(VAIHE_ID).toString();
            final HakemusId hakemusId = HakemusId.fromKey(hakemusIdString, vaihe);
            list.add(new Hakemus(hakemusId, (Map<String, String>) map.get(HAKEMUS_DATA), user));
        }
        return list;
    }
}
