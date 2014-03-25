package fi.vm.sade.haku.virkailija.valinta.impl;

import com.google.gson.*;
import fi.vm.sade.generic.rest.CachingRestClient;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationOptionDTO;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ValintakoeDTO;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import fi.vm.sade.haku.virkailija.valinta.ValintaService;
import fi.vm.sade.sijoittelu.tulos.dto.raportointi.HakijaDTO;
import fi.vm.sade.valintalaskenta.domain.dto.valintakoe.HakutoiveDTO;
import fi.vm.sade.valintalaskenta.domain.dto.valintakoe.ValintakoeOsallistuminenDTO;
import fi.vm.sade.valintalaskenta.domain.dto.valintakoe.ValintakoeValinnanvaiheDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@Profile(value = {"default", "devluokka"})
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
    public List<ApplicationOptionDTO> getValintakoeOsallistuminen(Application application) {
        Map<String, String> additionalInfo = application.getAdditionalInfo();
        List<Map<String, String>> hakukohteet = getHakukohteet(application);

        log.debug("Getting valintakoeosallistuminen for aos:");
        if (log.isDebugEnabled()) {
            int i = 0;
            for (Map<String, String> hakukohdeMap : hakukohteet) {
                ++i;
                for (Map.Entry<String, String> entry : hakukohdeMap.entrySet()) {
                    log.debug("AO {} '{}' -> '{}'", String.valueOf(i), entry.getKey(), entry.getValue());
                }
            }
        }

        ValintakoeOsallistuminenDTO osallistuminen = getOsallistuminen(application.getOid());
        List<ApplicationOptionDTO> aoList = new ArrayList<ApplicationOptionDTO>(hakukohteet.size());
        Map<String, HakutoiveDTO> hakutoiveMap = new HashMap<String, HakutoiveDTO>();
        for (HakutoiveDTO hakutoive : osallistuminen.getHakutoiveet()) {
            hakutoiveMap.put(hakutoive.getHakukohdeOid(), hakutoive);
        }

        for (Map<String, String> kohde : hakukohteet) {
            ApplicationOptionDTO ao = new ApplicationOptionDTO();
            String aoOid = kohde.get("koulutus-id");
            ao.setOid(aoOid);
            ao.setName(kohde.get("koulutus"));
            ao.setOpetuspiste(kohde.get("opetuspiste"));
            ao.setOpetuspisteOid(kohde.get("opetuspiste-id"));

            if (hakutoiveMap.containsKey(aoOid)) {
                HakutoiveDTO hakutoiveDTO = hakutoiveMap.get(aoOid);
                for (ValintakoeValinnanvaiheDTO vaihe : hakutoiveDTO.getValinnanVaiheet()) {
                    for (fi.vm.sade.valintalaskenta.domain.dto.valintakoe.ValintakoeDTO valintakoe : vaihe.getValintakokeet()) {
                        ValintakoeDTO valintakoeDTO = new ValintakoeDTO(valintakoe);
                        String scoreStr = additionalInfo.get(valintakoeDTO.getTunniste());
                        BigDecimal score = null;
                        if (isNotBlank(scoreStr)) {
                            try {
                                score = new BigDecimal(scoreStr);
                            } catch (NumberFormatException nfe) {
                                // NOP
                            }
                        }
                        valintakoeDTO.setScore(score);
                        ao.addTest(valintakoeDTO);
                    }
                }
            }
            aoList.add(ao);
        }
        return aoList;
    }

    @Override
    public HakijaDTO getHakija(String asOid, String applicationOid) {

        String url = "/resources/sijoittelu/" + asOid + "/sijoitteluajo/latest/hakemus/" + applicationOid;

        log.debug("Getting application from sijoittelu, url: {}", url);
        String response = null;
        try {
            response = getCachingRestClientSijoittelu().getAsString(url);
            log.debug("Got response: {}", response);
        } catch (IOException e) {
            e.printStackTrace();
            return new HakijaDTO();
        } catch (NullPointerException npe) {
            // Nothing found with asOid/applicationOid
            log.warn("Got NPE with asOid: {} appOid: {}", asOid, applicationOid);
            return new HakijaDTO();
        }

        GsonBuilder builder = new GsonBuilder().serializeNulls();
        Gson gson = builder.create();

        HakijaDTO hakijaDTO = gson.fromJson(response, HakijaDTO.class);
        if (hakijaDTO == null) {
            log.debug("hakijaDTO == null");
            hakijaDTO = new HakijaDTO();
        }
        return hakijaDTO;

    }

    private ValintakoeOsallistuminenDTO getOsallistuminen(String applicationOid) {

        String url = "/resources/valintakoe/hakemus/" + applicationOid;
        log.debug("Getting valintakoeosallistuminen, url: {}", url);
        String response = null;
        try {
            response = getCachingRestClientValinta().getAsString(url);
            log.debug("Got response: {}", response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return new Date(json.getAsJsonPrimitive().getAsLong());
            }
        });
        Gson gson = builder.create();
        ValintakoeOsallistuminenDTO osallistuminenDTO = gson.fromJson(response, ValintakoeOsallistuminenDTO.class);
        if (osallistuminenDTO == null) {
            osallistuminenDTO = new ValintakoeOsallistuminenDTO();
        }
        return osallistuminenDTO;
    }

    private List<Map<String, String>> getHakukohteet(Application application) {
        Map<String, String> toiveet = application.getPhaseAnswers(OppijaConstants.PHASE_APPLICATION_OPTIONS);

        Map<Integer, Map<String, String>> kohteet = new HashMap<Integer, Map<String, String>>();
        for (Map.Entry<String, String> entry : toiveet.entrySet()) {
            String key = entry.getKey();
            log.debug("Getting applicationOptions, key: {}", key);
            if (key.indexOf("-") > 0) {
                Integer index = Integer.parseInt(key.substring(0, key.indexOf("-")).replaceAll("[^0-9]", ""));

                Map<String, String> kohde = parseEntry(kohteet, index, entry);
                kohteet.put(index, kohde);
            }
        }

        List<Integer> toRemove = new ArrayList<Integer>();
        for (Map.Entry<Integer, Map<String, String>> entry : kohteet.entrySet()) {
            Map<String, String> kohde = entry.getValue();
            log.debug("Checking kohde {}", entry.getKey());
            if (log.isDebugEnabled()) {
                for (Map.Entry<String, String> e : kohde.entrySet()) {
                    log.debug("kohde '{}' -> '{}'", e.getKey(), e.getValue());
                }
            }
            if (!kohde.containsKey("koulutus-id") || isEmpty(kohde.get("koulutus-id"))) {
                log.debug("Removing kohde {}", entry.getKey());
                toRemove.add(entry.getKey());
            }
        }
        for (Integer i : toRemove) {
            kohteet.remove(i);
        }

        ArrayList<Map<String, String>> kohteetList = new ArrayList<Map<String, String>>(kohteet.size());
        for (Map.Entry<Integer, Map<String, String>> entry : kohteet.entrySet()) {
            log.debug("Adding kohde '{}'", entry.getKey().intValue());
            kohteetList.add(entry.getKey().intValue() - 1, entry.getValue());
        }
        return kohteetList;
    }

    private Map<String, String> parseEntry(Map<Integer, Map<String, String>> kohteet, Integer index,
                                           Map.Entry<String, String> entry) {
        String key = entry.getKey();
        String value = entry.getValue();

        Map<String, String> kohde = null;
        if (kohteet.containsKey(index)) {
            kohde = kohteet.get(index);
        } else {
            kohde = new HashMap<String, String>(4);
        }

        if (key.endsWith("-Opetuspiste")) {
            kohde.put("opetuspiste", value);
        } else if (key.endsWith("-Opetuspiste-id")) {
            kohde.put("opetuspiste-id", value);
        } else if (key.endsWith("-Koulutus-id")) {
            kohde.put("koulutus-id", value);
        } else if (key.endsWith("-Koulutus")) {
            kohde.put("koulutus", value);
        }

        return kohde;
    }

    private synchronized CachingRestClient getCachingRestClientValinta() {
        if (cachingRestClientValinta == null) {
            cachingRestClientValinta = new CachingRestClient();
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
            cachingRestClientSijoittelu = new CachingRestClient();
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

    protected void setCachingRestClientValinta(CachingRestClient cachingRestClientValinta) {
        this.cachingRestClientValinta = cachingRestClientValinta;
    }
    protected void setCachingRestClientSijoittelu(CachingRestClient cachingRestClientSijoittelu) {
        this.cachingRestClientSijoittelu = cachingRestClientSijoittelu;
    }
}
