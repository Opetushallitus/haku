package fi.vm.sade.haku.virkailija.valinta.impl;

import com.google.gson.*;
import fi.vm.sade.generic.rest.CachingRestClient;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.virkailija.valinta.MapJsonAdapter;
import fi.vm.sade.haku.virkailija.valinta.ValintaService;
import fi.vm.sade.haku.virkailija.valinta.ValintaServiceCallFailedException;
import fi.vm.sade.haku.virkailija.valinta.dto.HakemusDTO;
import fi.vm.sade.haku.virkailija.valinta.dto.HakijaDTO;
import fi.vm.sade.properties.OphProperties;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Profile(value = {"default", "devluokka", "vagrant"})
public class ValintaServiceImpl implements ValintaService {

    private static final Logger log = LoggerFactory.getLogger(ValintaServiceImpl.class);
    private final OphProperties urlConfiguration;

    private String casUrl;

    @Value("${valinta-default.timeout.millis:4000}")
    private int defaultValintaHttpRequestTimeoutMilliseconds;

    @Value("${cas.service.valintalaskenta-service}")
    private String targetServiceValinta;
    @Value("${haku.app.username.to.valintalaskenta}")
    private String clientAppUserValinta;
    @Value("${haku.app.password.to.valintalaskenta}")
    private String clientAppPassValinta;

    @Value("${cas.service.valintalaskentakoostepalvelu}")
    private String targetServiceKooste;
    @Value("${haku.app.username.to.valintalaskentakoostepalvelu}")
    private String clientAppUserKooste;
    @Value("${haku.app.password.to.valintalaskentakoostepalvelu}")
    private String clientAppPassKooste;

    @Value("${cas.service.valinta-tulos-service}")
    private String targetServiceValintatulosService;
    @Value("${valinta-tulos-service.timeout.millis:15000}")
    private int valintaTulosServiceRequestTimeoutMilliseconds;

    private static CachingRestClient cachingRestClientValinta;
    private static CachingRestClient cachingRestClientKooste;
    private static CachingRestClient cachingRestClientValintaTulosService;

    @Autowired
    public ValintaServiceImpl(OphProperties urlConfiguration) {
        this.urlConfiguration=urlConfiguration;
        casUrl = urlConfiguration.url("cas.url");
    }

    @Override
    public HakemusDTO getHakemus(String asOid, String applicationOid) {
        String url = urlConfiguration.url("valintalaskenta-laskenta-service.hakemus", asOid, applicationOid);

        try {
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            });
            Gson gson = builder.create();
            return gson.fromJson(getCachingRestClientValinta().getAsString(url), HakemusDTO.class);
        } catch (Exception e) {
            log.error("GET {} failed: ", url, e);
            return new HakemusDTO();
        }
    }

    @Override
    public HakijaDTO getHakija(String asOid, String applicationOid) {
        String url = urlConfiguration.url("valinta-tulos-service.hakija", asOid, applicationOid);
        CachingRestClient client = getCachingRestClientValintaTulosService();

        try {
            return client.get(url, HakijaDTO.class);
        } catch (Exception e) {
            log.error(String.format("GET %s with parameters hakuOid / hakemusOid %s / %s failed: ", url, asOid, applicationOid), e);
        }
        return new HakijaDTO();
    }

    @Override
    public Map<String, String> fetchValintaData(Application application) throws ValintaServiceCallFailedException {
        String asId = application.getApplicationSystemId();
        String personOid = application.getPersonOid();
        String url = urlConfiguration.url("valintalaskentakoostepalvelu.valintadata", asId, personOid);
        CachingRestClient client = getCachingRestClientKooste();
        Map<String, String> valintadata = new HashMap<>();
        try {
            Gson gson = new GsonBuilder().registerTypeAdapter(HashMap.class, new MapJsonAdapter()).create();
            HttpResponse response = client.post(url, "application/json", new Gson().toJson(application));
            HttpEntity responseEntity = response.getEntity();
            InputStream stream = responseEntity.getContent();
            String json = IOUtils.toString(stream);
            IOUtils.closeQuietly(stream);
            valintadata = gson.fromJson(json, valintadata.getClass());
        } catch (Exception e) {
            log.error("POST {} failed: ", url, e);
            throw new ValintaServiceCallFailedException(e);
        }
        return valintadata;

    }

    private synchronized CachingRestClient getCachingRestClientKooste() {
        if (cachingRestClientKooste == null) {
            cachingRestClientKooste = new CachingRestClient(defaultValintaHttpRequestTimeoutMilliseconds).setClientSubSystemCode("haku.hakemus-api");
            cachingRestClientKooste.setWebCasUrl(casUrl);
            cachingRestClientKooste.setCasService(targetServiceKooste);
            cachingRestClientKooste.setUsername(clientAppUserKooste);
            cachingRestClientKooste.setPassword(clientAppPassKooste);
            log.debug("cachingRestClientKooste "
                            + "carUrl: " + casUrl
                            + " casService: " + targetServiceKooste
                            + " username: " + clientAppUserKooste
                            + " password: " + clientAppPassKooste
            );
        }
        return cachingRestClientKooste;
    }

    private synchronized CachingRestClient getCachingRestClientValinta() {
        if (cachingRestClientValinta == null) {
            cachingRestClientValinta = new CachingRestClient(defaultValintaHttpRequestTimeoutMilliseconds).setClientSubSystemCode("haku.hakemus-api");
            cachingRestClientValinta.setWebCasUrl(casUrl);
            cachingRestClientValinta.setCasService(targetServiceValinta);
            cachingRestClientValinta.setUsername(clientAppUserValinta);
            cachingRestClientValinta.setPassword(clientAppPassValinta);
            log.debug("getCachingRestClientValinta "
                            + "carUrl: " + casUrl
                            + " casService: " + targetServiceValinta
                            + " username: " + clientAppUserValinta
                            + " password: " + clientAppPassValinta
            );
        }
        return cachingRestClientValinta;
    }

    /**
     * No CAS credentials, because from here we can use the private valinta-tulos-service API which does not
     * need CAS and the public CASsed API of valinta-tulos-service would need the CAS ticket to be passed in
     * differently than the others.
     */
    private synchronized CachingRestClient getCachingRestClientValintaTulosService() {
        if (cachingRestClientValintaTulosService == null) {
            cachingRestClientValintaTulosService = new CachingRestClient(valintaTulosServiceRequestTimeoutMilliseconds).
                setClientSubSystemCode("haku.hakemus-api");
            cachingRestClientValintaTulosService.setCasService(targetServiceValintatulosService);

            log.debug("getcachingRestClientValintaTulosService "
                    +" casService: "+cachingRestClientValintaTulosService.getCasService()
            );
        }
        return cachingRestClientValintaTulosService;
    }

    public void setCachingRestClientValinta(CachingRestClient cachingRestClientValinta) {
        ValintaServiceImpl.cachingRestClientValinta = cachingRestClientValinta;
    }
    public void setCachingRestClientKooste(CachingRestClient cachingRestClientKooste) {
        ValintaServiceImpl.cachingRestClientKooste = cachingRestClientKooste;
    }
    public void setCachingRestClientValintaTulosService(CachingRestClient cachingRestClientValintaTulosService) {
        ValintaServiceImpl.cachingRestClientValintaTulosService = cachingRestClientValintaTulosService;
    }
}
