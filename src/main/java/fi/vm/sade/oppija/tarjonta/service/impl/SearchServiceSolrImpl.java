package fi.vm.sade.oppija.tarjonta.service.impl;

import fi.vm.sade.oppija.tarjonta.domain.SearchParameters;
import fi.vm.sade.oppija.tarjonta.domain.SearchResult;
import fi.vm.sade.oppija.tarjonta.domain.exception.SearchException;
import fi.vm.sade.oppija.tarjonta.service.SearchService;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class SearchServiceSolrImpl implements SearchService {

    private final HttpSolrServer httpSolrServer;

    @Autowired
    public SearchServiceSolrImpl(final HttpSolrServer httpSolrServer) {
        this.httpSolrServer = httpSolrServer;
    }

    @Override
    public SearchResult search(final SearchParameters searchParameters) {
        return query(searchParameters);
    }

    @Override
    public Map<String, Object> searchById(final SearchParameters searchParameters) {
        SearchResult searchResult = query(searchParameters);
        return getItemFromResult(searchResult);
    }

    private SearchResult query(final SearchParameters searchParameters) {
        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
        try {
            SolrQuery query = createQuery(searchParameters);
            QueryResponse rsp = httpSolrServer.query(query);
            for (SolrDocument doc : rsp.getResults()) {
                results.add(doc.getFieldValueMap());
            }
        } catch (SolrServerException e) {
            throw new SearchException("Error running query", e);
        }
        return new SearchResult(results);
    }

    private SolrQuery createQuery(SearchParameters searchParameters) {
        SolrQuery query = new SolrQuery();
        query.setQuery(searchParameters.getSearchField() + ":" + searchParameters.getTerm());
        String sortOrder = searchParameters.getSortOrder();
        SolrQuery.ORDER order = "desc".equalsIgnoreCase(sortOrder) ? SolrQuery.ORDER.desc : SolrQuery.ORDER.asc;
        String sortField = searchParameters.getSortField();
        if (sortField != null) {
            query.addSortField(sortField, order);
        }
        query.setRows(searchParameters.getRows());
        query.setStart(searchParameters.getStart());
        query.setFields(searchParameters.getFields());
        return query;
    }

    private Map<String, Object> getItemFromResult(SearchResult searchResult) {
        List<Map<String, Object>> items = searchResult.getItems();
        if (items.size() == 0) {
            return Collections.<String, Object>emptyMap();
        } else if (items.size() == 1) {
            return items.get(0);
        } else {
            throw new SearchException("Multiple hits");
        }
    }
}
