package fi.vm.sade.haku.virkailija.valinta.impl;

import com.google.gson.*;
import fi.vm.sade.generic.rest.CachingRestClient;
import fi.vm.sade.haku.virkailija.valinta.ValintaService;
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

    private static CachingRestClient cachingRestClientValinta;
    private static CachingRestClient cachingRestClientSijoittelu;


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
//            FieldNamingStrategy fieldNamingStrategy = new FieldNamingStrategy() {
//                @Override
//                public String translateName(Field f) {
//                    if (f.getName().equals("valintatapajonooid")) {
//                        return "oid";
//                    }
//                    return f.getName();
//                }
//            };
//            builder.setFieldNamingStrategy(fieldNamingStrategy);
            Gson gson = builder.create();
            return gson.fromJson(client.getAsString(url), HakemusDTO.class);
        } catch (IOException e) {
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
        } catch (IOException e) {
            log.error("GET {} failed: ", url, e);
        } catch (NullPointerException npe) {
            log.warn("GET {} failed: ", url, npe);
        }
        return new HakijaDTO();
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

}
