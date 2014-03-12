package fi.vm.sade.haku.virkailija.valinta.impl;

import com.google.gson.*;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationOptionDTO;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ValintakoeDTO;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import fi.vm.sade.haku.virkailija.valinta.ValintaService;
import fi.vm.sade.sijoittelu.tulos.dto.raportointi.HakijaDTO;
import fi.vm.sade.valintalaskenta.domain.dto.valintakoe.HakutoiveDTO;
import fi.vm.sade.valintalaskenta.domain.dto.valintakoe.ValintakoeOsallistuminenDTO;
import fi.vm.sade.valintalaskenta.domain.dto.valintakoe.ValintakoeValinnanvaiheDTO;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@Profile(value = {"dev", "it"})
//@Profile("it")
public class ValintaServiceMockImpl implements ValintaService {

    @Override
    public List<ApplicationOptionDTO> getValintakoeOsallistuminen(Application application) {
        Map<String, String> additionalInfo = application.getAdditionalInfo();
        List<Map<String, String>> hakukohteet = getHakukohteet(application);

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
    public HakijaDTO getHakija(String asOid, String application) {
        String response = "\n" +
                "\n" +
                "{\n" +
                "  \"hakemusOid\": \"1.2.246.562.11.00000005610\",\n" +
                "  \"etunimi\": \"Neea V\",\n" +
                "  \"sukunimi\": \"Ylävuori\",\n" +
                "  \"hakutoiveet\": [\n" +
                "    {\n" +
                "      \"hakutoive\": 2,\n" +
                "      \"hakukohdeOid\": \"1.2.246.562.5.85532589612\",\n" +
                "      \"tarjoajaOid\": \"1.2.246.562.10.60222091211\",\n" +
                "      \"pistetiedot\": [\n" +
                "        {\n" +
                "          \"tunniste\": \"kielikoe_fi\",\n" +
                "          \"arvo\": null,\n" +
                "          \"laskennallinenArvo\": \"false\",\n" +
                "          \"osallistuminen\": \"MERKITSEMATTA\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"tunniste\": \"1_2_246_562_5_85532589612_urheilija_lisapiste\",\n" +
                "          \"arvo\": null,\n" +
                "          \"laskennallinenArvo\": \"0.0\",\n" +
                "          \"osallistuminen\": \"MERKITSEMATTA\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"tunniste\": \"Eläintenhoidon koulutusohjelma, pk (Maatalousalan perustutkinto), pääsykoe\",\n" +
                "          \"arvo\": \"10\",\n" +
                "          \"laskennallinenArvo\": \"10\",\n" +
                "          \"osallistuminen\": \"OSALLISTUI\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"hakutoiveenValintatapajonot\": [\n" +
                "        {\n" +
                "          \"valintatapajonoPrioriteetti\": 2,\n" +
                "          \"valintatapajonoOid\": \"1392297586537-8212850468668966009\",\n" +
                "          \"valintatapajonoNimi\": \"Varsinaisen valinnanvaiheen valintatapajono\",\n" +
                "          \"jonosija\": 16,\n" +
                "          \"paasyJaSoveltuvuusKokeenTulos\": null,\n" +
                "          \"varasijanNumero\": null,\n" +
                "          \"tila\": \"HYVAKSYTTY\",\n" +
                "          \"vastaanottotieto\": \"VASTAANOTTANUT_POISSAOLEVA\",\n" +
                "          \"hyvaksyttyHarkinnanvaraisesti\": false,\n" +
                "          \"tasasijaJonosija\": 1,\n" +
                "          \"pisteet\": 18,\n" +
                "          \"alinHyvaksyttyPistemaara\": 18,\n" +
                "          \"hakeneet\": 209,\n" +
                "          \"hyvaksytty\": 1,\n" +
                "          \"varalla\": 0\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}\n" +
                "\n";

        Gson gson = new Gson();
        HakijaDTO hakijaDTO = gson.fromJson(response, HakijaDTO.class);
        return hakijaDTO;
    }

    private ValintakoeOsallistuminenDTO getOsallistuminen(String applicationOid) {
        String response = "\n" +
                "\n" +
                "{\n" +
                "  \"hakuOid\": \"1.2.246.562.5.2013080813081926341927\",\n" +
                "  \"hakemusOid\": \"1.2.246.562.11.00000033242\",\n" +
                "  \"hakijaOid\": \"1.2.246.562.24.23524180014\",\n" +
                "  \"etunimi\": \"Asiakas\",\n" +
                "  \"sukunimi\": \"Testi\",\n" +
                "  \"createdAt\": 1394109497901,\n" +
                "  \"hakutoiveet\": [\n" +
                "    {\n" +
                "      \"hakukohdeOid\": \"1.2.246.562.5.97567996844\",\n" +
                "      \"valinnanVaiheet\": [\n" +
                "        {\n" +
                "          \"valinnanVaiheOid\": \"1394108382067-3464927667073476446\",\n" +
                "          \"valinnanVaiheJarjestysluku\": 1,\n" +
                "          \"valintakokeet\": [\n" +
                "            {\n" +
                "              \"valintakoeOid\": \"1394108382210-8388953053505017947\",\n" +
                "              \"valintakoeTunniste\": \"Sosiaali- ja terveysalan perustutkinto, pk, pääsykoe\",\n" +
                "              \"nimi\": \"Sosiaali- ja terveysalan perustutkinto, pk, pääsykoe\",\n" +
                "              \"aktiivinen\": false,\n" +
                "              \"osallistuminenTulos\": {\n" +
                "                \"osallistuminen\": \"OSALLISTUU\",\n" +
                "                \"kuvaus\": null,\n" +
                "                \"laskentaTila\": \"HYVAKSYTTAVISSA\",\n" +
                "                \"laskentaTulos\": true\n" +
                "              }\n" +
                "            },\n" +
                "            {\n" +
                "              \"valintakoeOid\": \"1394108382216-3431392441284793574\",\n" +
                "              \"valintakoeTunniste\": \"1_2_246_562_5_97567996844_urheilija_lisapiste\",\n" +
                "              \"nimi\": \"Urheilijalisäpiste\",\n" +
                "              \"aktiivinen\": false,\n" +
                "              \"osallistuminenTulos\": {\n" +
                "                \"osallistuminen\": \"EI_OSALLISTU\",\n" +
                "                \"kuvaus\": null,\n" +
                "                \"laskentaTila\": \"HYVAKSYTTAVISSA\",\n" +
                "                \"laskentaTulos\": false\n" +
                "              }\n" +
                "            },\n" +
                "            {\n" +
                "              \"valintakoeOid\": \"1394108382206-7586292685555339694\",\n" +
                "              \"valintakoeTunniste\": \"kielikoe_fi\",\n" +
                "              \"nimi\": \"Kielikoe\",\n" +
                "              \"aktiivinen\": false,\n" +
                "              \"osallistuminenTulos\": {\n" +
                "                \"osallistuminen\": \"EI_OSALLISTU\",\n" +
                "                \"kuvaus\": null,\n" +
                "                \"laskentaTila\": \"HYVAKSYTTAVISSA\",\n" +
                "                \"laskentaTulos\": false\n" +
                "              }\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}\n" +
                "\n";
        GsonBuilder builder = new GsonBuilder();

        builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return new Date(json.getAsJsonPrimitive().getAsLong());
            }
        });

        Gson gson = builder.create();

        ValintakoeOsallistuminenDTO osallistuminenDTO = gson.fromJson(response, ValintakoeOsallistuminenDTO.class);
        return osallistuminenDTO;
    }

    private List<Map<String, String>> getHakukohteet(Application application) {
        Map<String, String> toiveet = application.getPhaseAnswers(OppijaConstants.PHASE_APPLICATION_OPTIONS);

        Map<Integer, Map<String, String>> kohteet = new HashMap<Integer, Map<String, String>>();
        for (Map.Entry<String, String> entry : toiveet.entrySet()) {
            String key = entry.getKey();
            if (key.indexOf("-") > 0) {
                Integer index = Integer.parseInt(key.substring(0, key.indexOf("-")).replaceAll("[^0-9]", ""));

                Map<String, String> kohde = parseEntry(kohteet, index, entry);
                kohteet.put(index, kohde);
            }
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

}
