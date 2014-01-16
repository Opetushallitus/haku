package fi.vm.sade.haku.oppija.common.suoritusrekisteri.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fi.vm.sade.generic.rest.CachingRestClient;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusDTO;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusrekisteriService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Profile("default")
public class SuoritusrekisteriServiceImpl implements SuoritusrekisteriService {

    final Logger log = LoggerFactory.getLogger(SuoritusrekisteriServiceImpl.class);

    @Value("${web.url.cas}")
    private String casUrl;

    @Value("${cas.service.suoritusrekisteri}")
    private String targetService;
    @Value("${haku.app.username.to.usermanagement}")
    private String clientAppUser;
    @Value("${haku.app.password.to.usermanagement}")
    private String clientAppPass;

    private static CachingRestClient cachingRestClient;

    @Override
    public List<SuoritusDTO> getSuoritukset(String personOid, String hakuvuosi, String hakukausi) {
        CachingRestClient cachingRestClient = getCachingRestClient();
        String response;
        try {
            InputStream is = cachingRestClient.get("/rest/v1/suoritukset"
                    +"?henkiloOid="+personOid
                    +"&arvioituValmistumisvuosi="+hakuvuosi
                    +"&arvioituValmistumiskausi="+hakukausi);
            response = IOUtils.toString(is);
            log.debug("Got response: {}", response);
        } catch (IOException e) {
            log.error("Fetching koulu failed: {}", e);
            return null;
        }
        JsonArray elements = new JsonParser().parse(response).getAsJsonArray();
        ArrayList<SuoritusDTO> suoritukset = new ArrayList<SuoritusDTO>(elements.size());
        for (int i = 0; i < elements.size(); i++) {
            JsonObject elem = elements.get(i).getAsJsonObject();
            JsonElement oppilaitos = elem.get("oppilaitosOid");
            JsonElement arvioituValmistuminen = elem.get("arvioituValmistuminen");
            JsonElement tila = elem.get("tila");
            JsonElement luokkataso = elem.get("luokkataso");
            JsonElement luokka = elem.get("luokka");
            JsonElement henkiloOid = elem.get("henkiloOid");

            SuoritusDTO suoritus = new SuoritusDTO();
            suoritus.setOppilaitosOid(jsonElementToString(oppilaitos));
            suoritus.setArvioituValmistuminen(arvioituValmistuminen != null && !arvioituValmistuminen.isJsonNull()
                    ? new Date(elem.get("arvioituValmistuminen").getAsLong()) : null);
            suoritus.setTila(jsonElementToString(tila));
            suoritus.setLuokkataso(jsonElementToString(luokkataso));
            suoritus.setLuokka(jsonElementToString(luokka));
            suoritus.setHenkiloOid(jsonElementToString(henkiloOid));
            suoritukset.add(suoritus);
        }

        return suoritukset;
    }

    private String jsonElementToString(JsonElement elem) {
        if (elem == null || elem.isJsonNull()) {
            return null;
        }
        return elem.getAsString();
    }

    private synchronized CachingRestClient getCachingRestClient() {
        if (cachingRestClient == null) {
            cachingRestClient = new CachingRestClient();
            cachingRestClient.setWebCasUrl(casUrl);
            cachingRestClient.setCasService(targetService);
            cachingRestClient.setUsername(clientAppUser);
            cachingRestClient.setPassword(clientAppPass);
        }
        return cachingRestClient;
    }
}
