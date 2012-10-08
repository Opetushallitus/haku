package fi.vm.sade.oppija.tarjonta.service.impl;

import fi.vm.sade.oppija.tarjonta.domain.SearchParameters;
import fi.vm.sade.oppija.tarjonta.domain.SearchResult;
import fi.vm.sade.oppija.tarjonta.domain.exception.SearchException;
import fi.vm.sade.oppija.tarjonta.service.SearchService;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

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

    @Override
    public Collection<String> getUniqValuesByField(String field) {
        SolrQuery query = new SolrQuery();
        query.setFacet(true);
        query.addFacetField(field);
        Set<String> uniqNames = new HashSet<String>();
        try {
            QueryResponse rsp = createQuery(query);
            List<FacetField> facetFields = rsp.getFacetFields();
            for (FacetField facetField : facetFields) {
                List<FacetField.Count> values = facetField.getValues();
                for (FacetField.Count value : values) {
                    uniqNames.add(value.getName());
                }
            }
        } catch (SolrServerException e) {
            throw new SearchException("Error running query", e);
        }
        return uniqNames;

    }

    protected QueryResponse createQuery(SolrQuery query) throws SolrServerException {
        return httpSolrServer.query(query);
    }

    private SearchResult query(final SearchParameters searchParameters) {
        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
        try {
            SolrQuery query = createQuery(searchParameters);
            QueryResponse rsp = createQuery(query);
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
        StringBuilder queryStr = new StringBuilder();
        for (Map.Entry<String, Map<String, String>> entry : searchParameters.getFilters().entrySet()) {
            Map<String, String> values = entry.getValue();
            for (String value : values.keySet()) {
                queryStr.append("+");
                queryStr.append(entry.getKey()).append(":").append(value);
                queryStr.append(" ");
            }
        }
        query.setQuery(queryStr.toString());
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
