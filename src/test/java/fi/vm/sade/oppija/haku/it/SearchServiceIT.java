package fi.vm.sade.oppija.haku.it;

import fi.vm.sade.oppija.tarjonta.service.SearchService;
import fi.vm.sade.oppija.tarjonta.service.impl.SearchServiceSolrImpl;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class SearchServiceIT extends TomcatContainerBase {

    public final String id = String.valueOf(System.currentTimeMillis());
    public SearchService searchService;
    public HttpSolrServer httpSolrServer;

    public void setUp() throws Exception {
        httpSolrServer = new HttpSolrServer("http://localhost:" + container.getPort() + "/solr/");
        searchService = new SearchServiceSolrImpl(httpSolrServer);
        Map<String, String> values = new HashMap<String, String>();
        values.put("id", id);
        values.put("name", "Liikunnanohjaajan perustutkinto");
        SolrInputDocument solrInputDocument = newDocumentFromMap(values);
        httpSolrServer.add(solrInputDocument);
        httpSolrServer.commit();
    }


    @Test
    public void testSearch() throws Exception {
        //List<Map<String, Object>> results = searchService.search(id);
        //System.out.println(results);
    }

    private SolrInputDocument newDocumentFromMap(final Map<String, String> values) {
        final SolrInputDocument doc = new SolrInputDocument();
        for (Map.Entry<String, String> entry : values.entrySet()) {
            doc.addField(entry.getKey(), entry.getValue());
        }
        return doc;

    }


}
