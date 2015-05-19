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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@Profile(value = {"default", "devluokka", "vagrant"})
public class SuoritusrekisteriServiceImpl implements SuoritusrekisteriService {

    private final Logger log = LoggerFactory.getLogger(SuoritusrekisteriServiceImpl.class);
    private final static String ISO_DATE_FMT_STR = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
    private final static DateFormat ISO_DATE_FMT = new SimpleDateFormat(ISO_DATE_FMT_STR);

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
        validKomos.add(YO_TUTKINTO_KOMO);
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
    private Gson opiskelijaGson = new GsonBuilder().setDateFormat(ISO_DATE_FMT_STR).create();
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
        return getSuoritukset(personOid, null, null);
    }

    @Override
    public Map<String, List<SuoritusDTO>> getSuoritukset(String personOid, String komoOid) {
        return getSuoritukset(personOid, komoOid, null);
    }

    @Override
    public List<String> getChanges(String komoOid, Date since) {
        CachingRestClient cachingRestClient = getCachingRestClient();
        String response;
        String url = buildSuoritusUrl(null, komoOid, since);
        try {
            InputStream is = cachingRestClient.get(url);
            response = IOUtils.toString(is);
        } catch (IOException e) {
            log.error("Fetching suoritukset failed: {}", e);
            throw new ResourceNotFoundException("Fetching suoritukset failed", e);
        }

        JsonArray elements = new JsonParser().parse(response).getAsJsonArray();
        Set<String> changedPersons = new LinkedHashSet<>(elements.size());
        for (JsonElement elem : elements) {
            SuoritusDTO suoritus = suoritusGson.fromJson(elem, SuoritusDTO.class);
            changedPersons.add(suoritus.getHenkiloOid());
        }
        List<String> changes = new ArrayList<>(changedPersons.size());
        changes.addAll(changedPersons);
        return changes;
    }

    @Override
    public Map<String, List<SuoritusDTO>> getSuoritukset(String personOid, String komoOid, Date since) {
        CachingRestClient cachingRestClient = getCachingRestClient();
        String response;
        String url = buildSuoritusUrl(personOid, komoOid, since);
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
                    && !SuoritusDTO.TILA_KESKEN.equals(suoritus.getTila())
                    && !SuoritusDTO.TILA_KESKEYTYNYT.equals(suoritus.getTila())) {
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

    private String buildSuoritusUrl(String personOid, String komoOid, Date since) {
        StringBuilder urlBuilder = new StringBuilder("/rest/v1/suoritukset");
        boolean firstParam = true;
        firstParam = appendUrlParam(urlBuilder, firstParam, "henkilo", personOid);
        firstParam = appendUrlParam(urlBuilder, firstParam, "komo", komoOid);
        if (since != null) {
            appendUrlParam(urlBuilder, firstParam, "muokattuJalkeen", ISO_DATE_FMT.format(since));
        }
        return urlBuilder.toString();
    }

    private boolean appendUrlParam(StringBuilder urlBuilder, boolean firstParam, String param, String value) {
        if (isNotBlank(value)) {
            try {
                urlBuilder.append(firstParam ? "?" : "&")
                        .append(param).append("=").append(URLEncoder.encode(value, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                log.error("UTF-8 is not a supported encoding ", e);
                throw new RuntimeException(e);
            }
            firstParam = false;
        }
        return firstParam;
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
