package fi.vm.sade.oppija.lomakkeenhallinta.service.tarjonta.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import fi.vm.sade.oppija.lomakkeenhallinta.service.tarjonta.TarjontaService;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class SimpleTarjontaService implements TarjontaService {

    private final WebResource webResource;

    @Autowired
    public SimpleTarjontaService(@Value("${tarjonta.haku.resource.url}") final String tarjontaHakuResourceUrl) {
        ClientConfig cc = new DefaultClientConfig();
        cc.getClasses().add(JacksonJsonProvider.class);
        Client clientWithJacksonSerializer = Client.create(cc);
        webResource = clientWithJacksonSerializer.resource(tarjontaHakuResourceUrl);
    }

    @Override
    public Map<String, Map<String, String>> getApplicationSystemOidsAndNames() {
        Map<String, Map<String, String>> oidsAndNames = new HashMap<String, Map<String, String>>();

        List<Map<String, String>> oids = webResource.accept(MediaType.APPLICATION_JSON + ";charset=UTF-8").get(new GenericType<List<Map<String, String>>>() {
        });

        for (Map<String, String> map : oids) {
            String oid = map.get("oid");
            WebResource asWebResource = webResource.path(oid);
            Map<String, Object> as = asWebResource.get(new GenericType<Map<String, Object>>() {
            });
            Map<String, String> names = (Map<String, String>) as.get("nimi");
            Map<String, String> namesTransformed = Maps.newHashMap();
            Iterator<Map.Entry<String, String>> i = names.entrySet().iterator();

            while (i.hasNext()) {
                Map.Entry<String, String> entry = i.next();
                String key = entry.getKey().split("_")[1];
                String value = Strings.isNullOrEmpty(entry.getValue()) ? "[" + key + "]" : entry.getValue();
                namesTransformed.put(key, value);
            }
            oidsAndNames.put(oid, namesTransformed);
        }
        return oidsAndNames;
    }
}
