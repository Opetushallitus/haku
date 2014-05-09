package fi.vm.sade.haku.oppija.common.suoritusrekisteri.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fi.vm.sade.generic.rest.CachingRestClient;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.OpiskelijaDTO;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Profile(value = {"default", "devluokka"})
public class SuoritusrekisteriServiceImpl implements SuoritusrekisteriService {

    final Logger log = LoggerFactory.getLogger(SuoritusrekisteriServiceImpl.class);

    private final static DateFormat ISO8601 = new SimpleDateFormat("yyyyMMdd'T000000Z'");
    private final static DateFormat VALMISTUMINEN_FMT = new SimpleDateFormat("dd.MM.yyyy");

    @Value("${web.url.cas}")
    private String casUrl;

    @Value("${cas.service.suoritusrekisteri}")
    private String targetService;
    @Value("${haku.app.username.to.suoritusrekisteri}")
    private String clientAppUser;
    @Value("${haku.app.password.to.suoritusrekisteri}")
    private String clientAppPass;

    private static CachingRestClient cachingRestClient;


    @Override
    public List<OpiskelijaDTO> getOpiskelijat(String personOid) {
//        {
//            "id":"671a9c6a-3329-4761-a6e5-908da0e98898",
//            "oppilaitosOid":"1.2.246.562.10.16470831229",
//            "luokkataso":"10",
//            "luokka":"10Y",
//            "henkiloOid":"1.2.246.562.24.59031586696",
//            "alkuPaiva":"2011-07-31T21:00:00.000Z"
//        }

        CachingRestClient cachingRestClient = getCachingRestClient();
        String response;
        String date = ISO8601.format(new Date());
        try {
            InputStream is = cachingRestClient.get("/rest/v1/opiskelijat"
                    + "?henkilo=" + personOid
                    + "&paiva=" + date);
            response = IOUtils.toString(is);
            log.debug("Got response: {}", response);
        } catch (IOException e) {
            log.error("Fetching opiskelija failed: {}", e);
            return null;
        }

        JsonArray elements = new JsonParser().parse(response).getAsJsonArray();
        ArrayList<OpiskelijaDTO> opiskelijat = new ArrayList<OpiskelijaDTO>(elements.size());
        for (int i = 0; i < elements.size(); i++) {
            JsonObject elem = elements.get(i).getAsJsonObject();
            JsonElement oppilaitos = elem.get("oppilaitosOid");
            JsonElement luokkataso = elem.get("luokkataso");
            JsonElement luokka = elem.get("luokka");
            JsonElement henkiloOid = elem.get("henkiloOid");

            OpiskelijaDTO opiskelija = new OpiskelijaDTO();
            opiskelija.setOppilaitosOid(jsonElementToString(oppilaitos));
            opiskelija.setLuokkataso(jsonElementToString(luokkataso));
            opiskelija.setLuokka(jsonElementToString(luokka));
            opiskelija.setHenkiloOid(jsonElementToString(henkiloOid));
            opiskelijat.add(opiskelija);
        }

        return opiskelijat;
    }

    @Override
    public List<SuoritusDTO> getSuoritukset(String personOid) {

//        {
//            "id":"e482944f-6195-41d6-a456-3637c217096d",
//            "komoto": {
//                "oid":"komotoid",
//                "komo":"peruskoulu",
//                "tarjoaja":"1.2.246.562.10.89047714871"
//            },
//            "tila":"KESKEN",
//            "valmistuminen":"30.05.2014",
//            "henkiloOid":"1.2.246.562.24.23805003946",
//            "yksilollistaminen":"Osittain",
//            "suoritusKieli":"AM"
//        }

        CachingRestClient cachingRestClient = getCachingRestClient();
        String response;
        String date = ISO8601.format(new Date());
        try {
            InputStream is = cachingRestClient.get("/rest/v1/suoritukset"
                    + "?henkilo=" + personOid
                    + "&paiva=" + date);
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
            SuoritusDTO suoritus = suoritusJsonToDTO(elem);
            suoritukset.add(suoritus);
        }

        return suoritukset;
    }

    private SuoritusDTO suoritusJsonToDTO(JsonObject elem) {
        log.debug("suoritusJsonToDTO, json ", elem.toString());
        JsonElement id = elem.get("id");
        JsonElement tila = elem.get("tila");
        JsonElement henkiloOid = elem.get("henkiloOid");
        JsonElement suoritusKieli = elem.get("suoritusKieli");
        JsonElement komoto = elem.get("komoto");

        SuoritusDTO suoritus = new SuoritusDTO();
        suoritus.setId(jsonElementToString(id));
        suoritus.setTila(jsonElementToString(tila));
        suoritus.setHenkiloOid(jsonElementToString(henkiloOid));
        suoritus.setSuorituskieli(jsonElementToString(suoritusKieli));

        try {
            Date valmistuminen = VALMISTUMINEN_FMT.parse(jsonElementToString(elem.get("valmistuminen")));
            suoritus.setValmistuminen(valmistuminen);
        } catch (ParseException e) {
            log.info("Parsing valmistuminen date failed: " + e);
        }

        if (komoto != null && komoto.isJsonObject()) {
            JsonObject komotoObj = (JsonObject) komoto;
            String komo = jsonElementToString(komotoObj.get("komo"));

            if ("ulkomainen".equals(komo)) {
//            0: Suoritus.komoto.komo = "ulkomainen"
                suoritus.setPohjakoulutus(0);
            } else if ("lukio".equals(komo)) {
//            9: Suoritus.komoto.komo = "lukio"
                suoritus.setPohjakoulutus(9);
            } else if ("peruskoulu".equals(komo)) {
                String yksilollistaminen = jsonElementToString(elem.get("yksilollistaminen"));
//            1: Suoritus.komoto.komo = "peruskoulu", yksilollistaminen = "Ei"
//            2: Suoritus.komoto.komo = "peruskoulu", yksilollistaminen = "Osittain"
//            3: Suoritus.komoto.komo = "peruskoulu", yksilollistaminen = "Alueittain"
//            6: Suoritus.komoto.komo = "peruskoulu", yksilollistaminen = "Kokonaan"
                if ("Ei".equals(yksilollistaminen)) {
                    suoritus.setPohjakoulutus(1);
                } else if ("Osittain".equals(yksilollistaminen)) {
                    suoritus.setPohjakoulutus(2);
                } else if ("Alueittain".equals(yksilollistaminen)) {
                    suoritus.setPohjakoulutus(3);
                } else if ("Kokonaan".equals(yksilollistaminen)) {
                    suoritus.setPohjakoulutus(6);
                }
            }
        }


        log.debug("suoritusJsonToDTO, dto ", suoritus.toString());
        return suoritus;
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

    protected void setCachingRestClient(CachingRestClient cachingRestClient) {
        this.cachingRestClient = cachingRestClient;
    }
}
