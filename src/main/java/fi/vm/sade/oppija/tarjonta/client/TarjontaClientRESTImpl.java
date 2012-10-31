package fi.vm.sade.oppija.tarjonta.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.xml.transform.Source;

/**
 * @author hannu
 */
@Component
public class TarjontaClientRESTImpl implements TarjontaClient {

    private @Value("${tarjonta.data.url}") String tarjontaUrl;

    private RestTemplate restTemplate;

    private TarjontaClientRESTImpl() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public String retrieveTarjontaAsString() {
        return restTemplate.getForObject(tarjontaUrl, String.class);
    }

    @Override
    public Source retrieveTarjontaAsSource() {
        return restTemplate.getForObject(tarjontaUrl, Source.class);
    }
}
