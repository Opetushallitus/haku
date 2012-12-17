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

package fi.vm.sade.oppija.lomake.dao.impl;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import fi.vm.sade.oppija.common.dao.AbstractDAOMongoImpl;
import fi.vm.sade.oppija.lomake.converter.DBObjectToFormModel;
import fi.vm.sade.oppija.lomake.converter.FormModelToDBObject;
import fi.vm.sade.oppija.lomake.converter.JsonStringToFormModel;
import fi.vm.sade.oppija.lomake.dao.FormModelDAO;
import fi.vm.sade.oppija.lomake.domain.FormModel;
import fi.vm.sade.oppija.lomake.service.FormModelHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author hannu
 */
@Service("formModelDAOMongoImpl")
public class FormModelDAOMongoImpl extends AbstractDAOMongoImpl<FormModel> implements FormModelDAO {

    private static final Logger LOG = LoggerFactory.getLogger(FormModelDAOMongoImpl.class);
    private static final String COLLECTION_FORM_MODEL = "haku";

    @Autowired
    FormModelHolder holder;

    public FormModelDAOMongoImpl() {
        super(new DBObjectToFormModel(), new FormModelToDBObject());
    }

    @PostConstruct
    public void init() {
        super.init();
        try {
            List<FormModel> formModels = find(new FormModel());
            holder.updateModel(formModels.get(0));
        } catch (Exception ignored) {
            LOG.warn("No model found ! ");
        }
    }

    @Override
    public String getCollectionName() {
        return COLLECTION_FORM_MODEL;
    }

    @Override
    public void insert(FormModel formModel) {
        dropAndInsert(toDBObject.apply(formModel));
    }

    @Override
    public void insertModelAsJsonString(final String json) {
        LOG.debug("with content " + json);

        FormModel formModel = new JsonStringToFormModel().apply(json);
        dropAndInsert(toDBObject.apply(formModel));
        holder.updateModel(formModel);
    }

    private synchronized void dropAndInsert(final DBObject dbObject) {
        final DBCollection collection = getCollection();
        collection.drop();
        collection.insert(dbObject);
    }

}
