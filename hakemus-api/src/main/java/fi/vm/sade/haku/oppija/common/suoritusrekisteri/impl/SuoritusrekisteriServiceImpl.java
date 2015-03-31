package fi.vm.sade.haku.oppija.common.suoritusrekisteri.impl;

import com.google.gson.*;
import fi.vm.sade.generic.rest.CachingRestClient;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.ArvosanaDTO;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.OpiskelijaDTO;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusDTO;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusrekisteriService;
import fi.vm.sade.haku.oppija.lomake.exception.ResourceNotFoundException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@Profile(value = {"default", "devluokka", "vagrant"})
public class SuoritusrekisteriServiceImpl implements SuoritusrekisteriService {

    final Logger log = LoggerFactory.getLogger(SuoritusrekisteriServiceImpl.class);

    private final static List<String> validKomos = new ArrayList<String>(7);

    static {
        validKomos.add(AMMATTISTARTTI_KOMO);
        validKomos.add(LUKIO_KOMO);
        validKomos.add(MAMU_VALMENTAVA_KOMO);
        validKomos.add(KUNTOUTTAVA_KOMO);
        validKomos.add(LISAOPETUS_KOMO);
        validKomos.add(PERUSOPETUS_KOMO);
        validKomos.add(ULKOMAINEN_KOMO);
        validKomos.add(LUKIOON_VALMISTAVA_KOMO);
    }

    @Value("${web.url.cas}")
    private String casUrl;

    @Value("${cas.service.suoritusrekisteri}")
    private String targetService;
    @Value("${haku.app.username.to.suoritusrekisteri}")
    private String clientAppUser;
    @Value("${haku.app.password.to.suoritusrekisteri}")
    private String clientAppPass;

    private static CachingRestClient cachingRestClient;

    private Gson suoritusGson = new GsonBuilder().setDateFormat("dd.MM.yyyy").create();
    private Gson opiskelijaGson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
    private Gson arvosanaGson = new GsonBuilder().setDateFormat("dd.MM.yyyy").create();

    @Override
    public List<OpiskelijaDTO> getOpiskelijatiedot(String personOid) {

        CachingRestClient cachingRestClient = getCachingRestClient();
        String response;
        String url = "/rest/v1/opiskelijat?henkilo=" + personOid;
        try {
            InputStream is = cachingRestClient.get(url);
            response = IOUtils.toString(is);
            log.debug("url: {}, response: {}", url, response);
        } catch (IOException e) {
            log.error("Fetching opiskelija failed: {}", e);
            return null;
        }

        JsonArray elements = new JsonParser().parse(response).getAsJsonArray();
        ArrayList<OpiskelijaDTO> opiskelijatiedot = new ArrayList<>(elements.size());
        for (int i = 0; i < elements.size(); i++) {
            opiskelijatiedot.add(opiskelijaGson.fromJson(elements.get(i).getAsJsonObject(), OpiskelijaDTO.class));
        }

        return opiskelijatiedot;
    }

    @Override
    public List<ArvosanaDTO> getArvosanat(String suoritusId) {
        CachingRestClient cachingRestClient = getCachingRestClient();
        String response;

        String url = "/rest/v1/arvosanat/?suoritus="+suoritusId;
        try {
            response = cachingRestClient.getAsString(url);
            log.debug("url: {}, response: {}", url, response);
        } catch (IOException e) {
            throw new ResourceNotFoundException("Fetching grades failed: ", e);
        }
        JsonArray elements = new JsonParser().parse(response).getAsJsonArray();
        List<ArvosanaDTO> arvosanat = new ArrayList<>(elements.size());
        for (JsonElement elem : elements) {
            JsonObject obj = elem.getAsJsonObject();
            ArvosanaDTO arvosana = arvosanaGson.fromJson(obj, ArvosanaDTO.class);
            arvosanat.add(arvosana);
        }
        return arvosanat;
    }

    @Override
    public Map<String, List<SuoritusDTO>> getSuoritukset(String personOid) {
        return getSuoritukset(personOid, null);
    }

    @Override
    public Map<String, List<SuoritusDTO>> getSuoritukset(String personOid, String komoOid) {
        CachingRestClient cachingRestClient = getCachingRestClient();
        String response;
        String url = "/rest/v1/suoritukset?henkilo=" + personOid;
        if (isNotBlank(komoOid)) {
            url = url + "&komo=" + komoOid;
        }
        try {
            InputStream is = cachingRestClient.get(url);
            response = IOUtils.toString(is);
            log.debug("url: {}, response: {}", url, response);
        } catch (IOException e) {
            log.error("Fetching suoritukset failed: {}", e);
            throw new ResourceNotFoundException("Fetching suoritukset failed", e);
        }

        JsonArray elements = new JsonParser().parse(response).getAsJsonArray();
        Map<String, List<SuoritusDTO>> suoritukset = new HashMap<>(elements.size());
        for (JsonElement elem : elements) {
            SuoritusDTO suoritus = suoritusGson.fromJson(elem, SuoritusDTO.class);

            if (!SuoritusDTO.TILA_VALMIS.equals(suoritus.getTila())
                    && !SuoritusDTO.TILA_KESKEN.equals(suoritus.getTila())) {
                continue;
            }
            String komo = suoritus.getKomo();
            if (!validKomos.contains(komo)) {
                continue;
            }
            List<SuoritusDTO> komonSuoritukset = suoritukset.get(komo);
            if (komonSuoritukset == null) {
                komonSuoritukset = new ArrayList<>(1);
            }
            komonSuoritukset.add(suoritus);
            suoritukset.put(komo, komonSuoritukset);
        }

        return suoritukset;
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

    protected void setCachingRestClient(CachingRestClient cachingRestClient) {
        this.cachingRestClient = cachingRestClient;
    }
}
