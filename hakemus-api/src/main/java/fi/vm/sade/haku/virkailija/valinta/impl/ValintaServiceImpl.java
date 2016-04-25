package fi.vm.sade.haku.virkailija.valinta.impl;

import com.google.gson.*;
import fi.vm.sade.generic.rest.CachingRestClient;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.lomake.exception.ResourceNotFoundException;
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

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Profile(value = {"default", "devluokka", "vagrant"})
public class ValintaServiceImpl implements ValintaService {

    private static final Logger log = LoggerFactory.getLogger(ValintaServiceImpl.class);

    @Value("${web.url.cas}")
    private String casUrl;

    @Value("${cas.service.valintalaskenta-service}")
    private String targetServiceValinta;
    @Value("${haku.app.username.to.valintalaskenta}")
    private String clientAppUserValinta;
    @Value("${haku.app.password.to.valintalaskenta}")
    private String clientAppPassValinta;

    @Value("${cas.service.sijoittelu-service}")
    private String targetServiceSijoittelu;
    @Value("${haku.app.username.to.sijoittelu}")
    private String clientAppUserSijoittelu;
    @Value("${haku.app.password.to.sijoittelu}")
    private String clientAppPassSijoittelu;

    @Value("${cas.service.valintalaskentakoostepalvelu}")
    private String targetServiceKooste;
    @Value("${haku.app.username.to.valintalaskentakoostepalvelu}")
    private String clientAppUserKooste;
    @Value("${haku.app.password.to.valintalaskentakoostepalvelu}")
    private String clientAppPassKooste;

    private static CachingRestClient cachingRestClientValinta;
    private static CachingRestClient cachingRestClientSijoittelu;
    private static CachingRestClient cachingRestClientKooste;

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
        String url = String.format("/resources/sijoittelu/%s/sijoitteluajo/latest/hakemus/%s", asOid, applicationOid);
        CachingRestClient client = getCachingRestClientSijoittelu();

        try {
            return new Gson().fromJson(client.getAsString(url), HakijaDTO.class);
        } catch (Exception e) {
            log.error("GET {} failed: ", url, e);
        }
        return new HakijaDTO();
    }

    @Override
    public Map<String, String> fetchValintaData(Application application) throws ValintaServiceCallFailedException {
        String asId = application.getApplicationSystemId();
        String personOid = application.getPersonOid();
        String applicationOid = application.getOid();
        String url = String.format("/resources/proxy/suoritukset/suorituksetByOpiskelijaOid/hakuOid/%s/opiskeljaOid",
                asId, personOid, applicationOid);
        CachingRestClient client = getCachingRestClientKooste();
        Map<String, String> valintadata = new HashMap<>();
        try {
            Gson gson = new GsonBuilder().registerTypeAdapter(HashMap.class, new MapJsonAdapter()).create();
            valintadata = gson.fromJson(client.postForLocation(url, new Gson().toJson(application)), valintadata.getClass());
        } catch (Exception e) {
            log.error("POST {} failed: ", url, e);
            throw new ValintaServiceCallFailedException(e);
        }
        return valintadata;

    }

    private synchronized CachingRestClient getCachingRestClientKooste() {
        if (cachingRestClientKooste == null) {
            cachingRestClientKooste = new CachingRestClient(4000);
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
            cachingRestClientValinta = new CachingRestClient(4000);
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

    private synchronized CachingRestClient getCachingRestClientSijoittelu() {
        if (cachingRestClientSijoittelu == null) {
            cachingRestClientSijoittelu = new CachingRestClient(4000);
            cachingRestClientSijoittelu.setWebCasUrl(casUrl);
            cachingRestClientSijoittelu.setCasService(targetServiceSijoittelu);
            cachingRestClientSijoittelu.setUsername(clientAppUserSijoittelu);
            cachingRestClientSijoittelu.setPassword(clientAppPassSijoittelu);

            log.debug("getCachingRestClientSijoittelu "
                    +"carUrl: "+casUrl
                    +" casService: "+targetServiceSijoittelu
                    +" username: "+clientAppUserSijoittelu
                    +" password: "+clientAppPassSijoittelu
            );
        }
        return cachingRestClientSijoittelu;
    }

    public void setCachingRestClientValinta(CachingRestClient cachingRestClientValinta) {
        this.cachingRestClientValinta = cachingRestClientValinta;
    }
    public void setCachingRestClientSijoittelu(CachingRestClient cachingRestClientSijoittelu) {
        this.cachingRestClientSijoittelu = cachingRestClientSijoittelu;
    }
    public void setCachingRestClientKooste(CachingRestClient cachingRestClientKooste) {
        this.cachingRestClientKooste = cachingRestClientKooste;
    }

}
