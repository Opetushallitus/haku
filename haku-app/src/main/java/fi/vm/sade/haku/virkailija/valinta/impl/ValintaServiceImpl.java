package fi.vm.sade.haku.virkailija.valinta.impl;

import fi.vm.sade.generic.rest.CachingRestClient;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationOptionDTO;
import fi.vm.sade.haku.virkailija.valinta.ValintaService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import fi.vm.sade.valintalaskenta.domain.dto.valintakoe.HakutoiveDTO;
//import fi.vm.sade.valintalaskenta.domain.dto.valintakoe.ValintakoeOsallistuminenDTO;
//import fi.vm.sade.valintalaskenta.domain.dto.valintakoe.ValintakoeValinnanvaiheDTO;

@Service
@Profile("default")
public class ValintaServiceImpl implements ValintaService {

    @Value("${web.url.cas}")
    private String casUrl;

    @Value("${cas.service.valintalaskenta}")
    private String targetService;
    @Value("${haku.app.username.to.valintalaskenta}")
    private String clientAppUser;
    @Value("${haku.app.password.to.valintalaskenta}")
    private String clientAppPass;

    private static CachingRestClient cachingRestClient;

    @Override
    public List<ApplicationOptionDTO> getValintakoeOsallistuminen(Application application) {
        return null;
//        Map<String, String> additionalInfo = application.getAdditionalInfo();
//        List<Map<String, String>> hakukohteet = getHakukohteet(application);
//
//        ValintakoeOsallistuminenDTO osallistuminen = getOsallistuminen(application.getOid());
//        List<ApplicationOptionDTO> aoList = new ArrayList<ApplicationOptionDTO>(hakukohteet.size());
//        Map<String, HakutoiveDTO> hakutoiveMap = new HashMap<String, HakutoiveDTO>();
//        for (HakutoiveDTO hakutoive : osallistuminen.getHakutoiveet()) {
//            hakutoiveMap.put(hakutoive.getHakukohdeOid(), hakutoive);
//        }
//
//        for (Map<String, String> kohde : hakukohteet) {
//            ApplicationOptionDTO ao = new ApplicationOptionDTO();
//            String aoOid = kohde.get("koulutus-id");
//            ao.setOid(aoOid);
//            ao.setName(kohde.get("koulutus"));
//            ao.setOpetuspiste(kohde.get("opetuspiste"));
//            ao.setOpetuspisteOid(kohde.get("opetuspiste-id"));
//
//            HakutoiveDTO hakutoiveDTO = hakutoiveMap.get(aoOid);
//            for (ValintakoeValinnanvaiheDTO vaihe : hakutoiveDTO.getValinnanVaiheet()) {
//                for (ValintakoeDTO valintakoe : vaihe.getValintakokeet()) {
//                    ValintakoeDTO valintakoeDTO = new ValintakoeDTO(valintakoe);
//                    String scoreStr = additionalInfo.get(valintakoeDTO.getTunniste());
//                    BigDecimal score = null;
//                    if (isNotBlank(scoreStr)) {
//                        try {
//                            score = new BigDecimal(scoreStr);
//                        } catch (NumberFormatException nfe) {
//                            // NOP
//                        }
//                    }
//                    valintakoeDTO.setScore(score);
//                    ao.addTest(valintakoeDTO);
//                }
//            }
//            aoList.add(ao);
//        }
//        return aoList;
    }

//    private ValintakoeOsallistuminenDTO getOsallistuminen(String applicationOid) {
//        // https://itest-virkailija.oph.ware.fi/valintalaskenta-laskenta-service/resources/valintakoe/hakemus/{oid}
//
//        String response = "\n" +
//                "\n" +
//                "{\n" +
//                "  \"hakuOid\": \"1.2.246.562.5.2013080813081926341927\",\n" +
//                "  \"hakemusOid\": \"1.2.246.562.11.00000033242\",\n" +
//                "  \"hakijaOid\": \"1.2.246.562.24.23524180014\",\n" +
//                "  \"etunimi\": \"Asiakas\",\n" +
//                "  \"sukunimi\": \"Testi\",\n" +
//                "  \"createdAt\": \"2014-02-28T00:00:00Z\",\n" +
//                "  \"hakutoiveet\": [\n" +
//                "    {\n" +
//                "      \"hakukohdeOid\": \"1.2.246.562.5.97567996844\",\n" +
//                "      \"valinnanVaiheet\": [\n" +
//                "        {\n" +
//                "          \"valinnanVaiheOid\": \"13922838416905878749295919892470\",\n" +
//                "          \"valinnanVaiheJarjestysluku\": 1,\n" +
//                "          \"valintakokeet\": [\n" +
//                "            {\n" +
//                "              \"valintakoeOid\": \"13922838424444093327208871298011\",\n" +
//                "              \"valintakoeTunniste\": \"Sosiaali- ja terveysalan perustutkinto, pk, pääsykoe\",\n" +
//                "              \"nimi\": null,\n" +
//                "              \"aktiivinen\": false,\n" +
//                "              \"osallistuminenTulos\": {\n" +
//                "                \"osallistuminen\": \"OSALLISTUU\",\n" +
//                "                \"kuvaus\": null,\n" +
//                "                \"laskentaTila\": \"HYVAKSYTTAVISSA\",\n" +
//                "                \"laskentaTulos\": true\n" +
//                "              }\n" +
//                "            },\n" +
//                "            {\n" +
//                "              \"valintakoeOid\": \"1392283842455-767373613778984168\",\n" +
//                "              \"valintakoeTunniste\": \"kielikoe_fi\",\n" +
//                "              \"nimi\": null,\n" +
//                "              \"aktiivinen\": false,\n" +
//                "              \"osallistuminenTulos\": {\n" +
//                "                \"osallistuminen\": \"EI_OSALLISTU\",\n" +
//                "                \"kuvaus\": null,\n" +
//                "                \"laskentaTila\": \"HYVAKSYTTAVISSA\",\n" +
//                "                \"laskentaTulos\": false\n" +
//                "              }\n" +
//                "            },\n" +
//                "            {\n" +
//                "              \"valintakoeOid\": \"1392283842410-2180891968553649661\",\n" +
//                "              \"valintakoeTunniste\": \"1_2_246_562_5_97567996844_urheilija_lisapiste\",\n" +
//                "              \"nimi\": null,\n" +
//                "              \"aktiivinen\": false,\n" +
//                "              \"osallistuminenTulos\": {\n" +
//                "                \"osallistuminen\": \"EI_OSALLISTU\",\n" +
//                "                \"kuvaus\": null,\n" +
//                "                \"laskentaTila\": \"HYVAKSYTTAVISSA\",\n" +
//                "                \"laskentaTulos\": false\n" +
//                "              }\n" +
//                "            }\n" +
//                "          ]\n" +
//                "        }\n" +
//                "      ]\n" +
//                "    }\n" +
//                "  ]\n" +
//                "}\n" +
//                "\n";
//
//        Gson gson = new Gson();
////        ValintakoeOsallistuminenDTO osallistuminenDTO = gson.fromJson(response, ValintakoeOsallistuminenDTO.class);
////        return osallistuminenDTO;
//        return null;
//    }

    private List<Map<String, String>> getHakukohteet(Application application) {
//        Map<String, String> toiveet = application.getPhaseAnswers(OppijaConstants.PHASE_APPLICATION_OPTIONS);
        Map<String, String> toiveet = null; //application.getPhaseAnswers(OppijaConstants.PHASE_APPLICATION_OPTIONS);

        Map<Integer, Map<String, String>> kohteet = new HashMap<Integer, Map<String, String>>();
        for (Map.Entry<String, String> entry : toiveet.entrySet()) {
            String key = entry.getKey();
            Integer index = Integer.parseInt(key.substring(0, key.indexOf("-")).replaceAll("[^0-9]", ""));

            Map<String, String> kohde = parseEntry(kohteet, index, entry);
            kohteet.put(index, kohde);
        }

        ArrayList<Map<String, String>> kohteetList = new ArrayList<Map<String, String>>(kohteet.size());
        for (Map.Entry<Integer, Map<String, String>> entry : kohteet.entrySet()) {
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
