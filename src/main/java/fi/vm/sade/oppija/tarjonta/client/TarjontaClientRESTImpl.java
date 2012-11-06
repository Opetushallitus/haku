package fi.vm.sade.oppija.tarjonta.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.xml.transform.Source;
import java.net.URI;

/**
 * @author hannu
 */
@Component
public class TarjontaClientRESTImpl implements TarjontaClient {


    private RestTemplate restTemplate;

    private TarjontaClientRESTImpl() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public String retrieveTarjontaAsString(URI tarjontaUrl) {
        return restTemplate.getForObject(tarjontaUrl, String.class);
    }

    @Override
    public Source retrieveTarjontaAsSource(URI tarjontaUrl) {
        return restTemplate.getForObject(tarjontaUrl, Source.class);
    }
}
