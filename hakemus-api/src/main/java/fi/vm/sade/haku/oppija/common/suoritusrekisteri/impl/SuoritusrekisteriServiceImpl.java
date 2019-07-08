package fi.vm.sade.haku.oppija.common.suoritusrekisteri.impl;

import com.google.gson.*;
import fi.vm.sade.javautils.legacy_caching_rest_client.CachingRestClient;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.OpiskelijaDTO;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusDTO;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusrekisteriService;
import fi.vm.sade.haku.oppija.lomake.exception.ResourceNotFoundException;
import fi.vm.sade.properties.OphProperties;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
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
    private OphProperties urlConfiguration;

    @Autowired
    public SuoritusrekisteriServiceImpl(OphProperties urlConfiguration) {
        this.urlConfiguration = urlConfiguration;
    }

    @Override
    public List<OpiskelijaDTO> getOpiskelijatiedot(String personOid) {

        String response;
        try {
            InputStream is = getCachingRestClient().get(urlConfiguration.url("suoritusrekisteri.opiskelijatByPersonOid", personOid));
            response = IOUtils.toString(is);
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
    public Map<String, List<SuoritusDTO>> getSuoritukset(String personOid, String komoOid) {
        String response;
        try {
            InputStream is = getCachingRestClient().get(buildSuoritusUrl(personOid, komoOid, null));
            response = IOUtils.toString(is);
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

    @Override
    public List<SuoritusDTO> getSuorituksetAsList(String personOid) {
        String response;
        try {
            InputStream is = getCachingRestClient().get(buildSuoritusUrl(personOid, "", null));
            response = IOUtils.toString(is);
        } catch (IOException e) {
            log.error("Fetching suoritukset failed: {}", e);
            throw new ResourceNotFoundException("Fetching suoritukset failed", e);
        }

        JsonArray elements = new JsonParser().parse(response).getAsJsonArray();
        List<SuoritusDTO> suor = new ArrayList<>(1);
        for (JsonElement elem : elements) {
            SuoritusDTO suoritus = suoritusGson.fromJson(elem, SuoritusDTO.class);

            if (!SuoritusDTO.TILA_VALMIS.equals(suoritus.getTila())
                    && !SuoritusDTO.TILA_KESKEN.equals(suoritus.getTila())
                    && !SuoritusDTO.TILA_KESKEYTYNYT.equals(suoritus.getTila())) {
                continue;
            }
            suor.add(suoritus);
        }

        return suor;
    }

    @Override
    public List<String> getChanges(String komoOid, Date since) {
        String response;
        try {
            InputStream is = getCachingRestClient().get(buildSuoritusUrl(null, komoOid, since));
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

    private String buildSuoritusUrl(String personOid, String komoOid, Date since) {
        Map params = new HashMap();
        appendUrlParam(params, "henkilo", personOid);
        appendUrlParam(params, "komo", komoOid);
        if (since != null) {
            appendUrlParam(params, "muokattuJalkeen", ISO_DATE_FMT.format(since));
        }
        return urlConfiguration.url("suoritusrekisteri.suoritus.search", params);
    }

    private void appendUrlParam(Map params, String param, String value) {
        if (isNotBlank(value)) {
            params.put(param, value);
        }
    }

    private synchronized CachingRestClient getCachingRestClient() {
        if (cachingRestClient == null) {
            String callerId = "1.2.246.562.10.00000000001.haku.hakemus-api";
            cachingRestClient = new CachingRestClient(callerId);
            cachingRestClient.setWebCasUrl(urlConfiguration.url("cas.url"));
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
