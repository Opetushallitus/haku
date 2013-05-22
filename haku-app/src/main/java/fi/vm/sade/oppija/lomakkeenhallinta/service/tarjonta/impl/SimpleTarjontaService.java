package fi.vm.sade.oppija.lomakkeenhallinta.service.tarjonta.impl;

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

import java.util.List;

@Service
public class SimpleTarjontaService implements TarjontaService {

    private final WebResource webResource;

    @Autowired
    public SimpleTarjontaService(@Value("${tarjonta.haku.resource.url}") final String tarjontaHakuResourceUrl) {
        //this.url = url;
        //"http://reppu.hard.ware.fi:8302/tarjonta-service/rest/haku";
        ClientConfig cc = new DefaultClientConfig();
        cc.getClasses().add(JacksonJsonProvider.class);
        Client clientWithJacksonSerializer = Client.create(cc);
        webResource = clientWithJacksonSerializer.resource(tarjontaHakuResourceUrl);
    }

    @Override
    public List<String> getApplicationSystemOids() {
        return webResource.get(new GenericType<List<String>>() {
        });
    }
}
