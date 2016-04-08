package fi.vm.sade.haku.virkailija.valinta.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import fi.vm.sade.generic.rest.CachingRestClient;
import fi.vm.sade.haku.oppija.configuration.UrlConfiguration;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.virkailija.valinta.MapJsonAdapter;
import fi.vm.sade.haku.virkailija.valinta.ValintaService;
import fi.vm.sade.haku.virkailija.valinta.ValintaServiceCallFailedException;
import fi.vm.sade.haku.virkailija.valinta.dto.HakemusDTO;
import fi.vm.sade.haku.virkailija.valinta.dto.HakijaDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Profile(value = {"default", "devluokka", "vagrant"})
public class ValintaServiceImpl implements ValintaService {

    private static final Logger log = LoggerFactory.getLogger(ValintaServiceImpl.class);

    private String casUrl;

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

    @Value("${valinta-tulos-service.url}")
    private String targetServiceValintatulosService;

    private static CachingRestClient cachingRestClientValinta;
    private static CachingRestClient cachingRestClientKooste;
    private static CachingRestClient cachingRestClientValintaTulosService;

    public ValintaServiceImpl(UrlConfiguration urlConfiguration) {
        casUrl = urlConfiguration.url("cas.url");
    }

    @Override
    public HakemusDTO getHakemus(String asOid, String applicationOid) {
        String url = String.format("/resources/hakemus/%s/%s", asOid, applicationOid);
        CachingRestClient client = getCachingRestClientValinta();

        try {
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            });
            Gson gson = builder.create();
            return gson.fromJson(client.getAsString(url), HakemusDTO.class);
        } catch (Exception e) {
            log.error("GET {} failed: ", url, e);
            return new HakemusDTO();
        }
    }

    @Override
    public HakijaDTO getHakija(String asOid, String applicationOid) {
        String url = String.format("/haku/%s/sijoitteluajo/latest/hakemus/%s", asOid, applicationOid);
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
        String applicationOid = application.getOid();
        String url = String.format("/resources/proxy/suoritukset/suorituksetByOpiskelijaOid/hakuOid/%s/opiskeljaOid/%s/hakemusOid/%s",
                asId, personOid, applicationOid);
        CachingRestClient client = getCachingRestClientKooste();
        Map<String, String> valintadata = new HashMap<>();
        try {
            Gson gson = new GsonBuilder().registerTypeAdapter(HashMap.class, new MapJsonAdapter()).create();
            valintadata = gson.<Map<String, String>>fromJson(client.getAsString(url), valintadata.getClass());
        } catch (Exception e) {
            log.error("GET {} failed: ", url, e);
            throw new ValintaServiceCallFailedException(e);
        }
        return valintadata;

    }

    private synchronized CachingRestClient getCachingRestClientKooste() {
        if (cachingRestClientKooste == null) {
            cachingRestClientKooste = new CachingRestClient(4000).setClientSubSystemCode("haku.hakemus-api");
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
            cachingRestClientValinta = new CachingRestClient(4000).setClientSubSystemCode("haku.hakemus-api");
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
            cachingRestClientValintaTulosService = new CachingRestClient(4000).setClientSubSystemCode("haku.hakemus-api");
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
