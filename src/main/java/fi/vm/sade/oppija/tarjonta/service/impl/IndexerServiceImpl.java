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

package fi.vm.sade.oppija.tarjonta.service.impl;


import fi.vm.sade.oppija.tarjonta.client.SolrClient;
import fi.vm.sade.oppija.tarjonta.client.TarjontaClient;
import fi.vm.sade.oppija.tarjonta.service.IndexService;
import fi.vm.sade.oppija.tarjonta.service.generator.DummyDataGenerator;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.Collection;

@Service
public class IndexerServiceImpl implements IndexService {

    public static final Logger LOGGER = LoggerFactory.getLogger(IndexerServiceImpl.class);

    private final HttpSolrServer httpSolrServer;

    private final TarjontaClient tarjontaClient;
    private final SolrClient client;

    @Autowired
    public IndexerServiceImpl(HttpSolrServer httpSolrServer, TarjontaClient tarjontaClient, SolrClient client) {
        this.httpSolrServer = httpSolrServer;
        this.tarjontaClient = tarjontaClient;
        this.client = client;
    }

    @Override
    public String update(final URI uri) {

        final Source source = tarjontaClient.retrieveTarjontaAsSource(uri);
        try {
            final ByteArrayOutputStream result = transform(source);
            return client.update(result);
        } catch (Exception e) {
            LOGGER.error("Error", e);
            return "Indeksin päivitys epäonnistui ";
        }
    }

    private ByteArrayOutputStream transform(Source source) throws Exception {
        final ClassPathResource classPathResource = new ClassPathResource("xml/xslt/tarjonta.xsl");
        StreamSource streamSource = new StreamSource(classPathResource.getInputStream());
        streamSource.setSystemId(classPathResource.getFile());
        Transformer transformer = TransformerFactory.newInstance().newTransformer(streamSource);
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final StreamResult outputTarget = new StreamResult(outputStream);
        transformer.transform(source, outputTarget);
        return outputStream;
    }

    @Override
    public boolean generate() {
        Collection<SolrInputDocument> documents = DummyDataGenerator.generate();
        try {
            httpSolrServer.add(documents);
            httpSolrServer.commit();
        } catch (Exception e) {
            LOGGER.error("Indeksin päivitys epäonnistui");
            return false;
        }
        return true;
    }

    public boolean drop() {
        boolean dropped = false;
        try {
            httpSolrServer.deleteByQuery("*:*");
            dropped = true;
        } catch (Exception e) {
            LOGGER.error("drop failed", e);
        }
        return dropped;
    }


}
