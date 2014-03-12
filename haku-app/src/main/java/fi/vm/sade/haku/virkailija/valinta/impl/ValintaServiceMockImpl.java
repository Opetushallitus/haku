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
        String response = "{"+
                "\"hakemusOid\": \"1.2.246.562.11.00000005610\","+
                "\"etunimi\": \"Neea V\","+
                "\"sukunimi\": \"Ylävuori\","+
                "\"hakutoiveet\": ["+
                "{"+
                "\"hakutoive\": 2,"+
                "\"hakukohdeOid\": \"1.2.246.562.5.85532589612\","+
                "\"tarjoajaOid\": \"1.2.246.562.10.60222091211\","+
                "\"pistetiedot\": ["+
                "{"+
                "\"tunniste\": \"kielikoe_fi\","+
                "\"arvo\": null,"+
                "\"laskennallinenArvo\": \"false\","+
                "\"osallistuminen\": \"MERKITSEMATTA\""+
                "},"+
                "{"+
                "\"tunniste\": \"1_2_246_562_5_85532589612_urheilija_lisapiste\","+
                "\"arvo\": null,"+
                "\"laskennallinenArvo\": \"0.0\","+
                "\"osallistuminen\": \"MERKITSEMATTA\""+
                "},"+
                "{"+
                "\"tunniste\": \"Eläintenhoidon koulutusohjelma, pk (Maatalousalan perustutkinto), pääsykoe\","+
                "\"arvo\": \"10\","+
                "\"laskennallinenArvo\": \"10\","+
                "\"osallistuminen\": \"OSALLISTUI\""+
                "}"+
                "],"+
                "\"hakutoiveenValintatapajonot\": ["+
                "{"+
                "\"valintatapajonoPrioriteetti\": 2,"+
                "\"valintatapajonoOid\": \"1392297586537-8212850468668966009\","+
                "\"valintatapajonoNimi\": \"Varsinaisen valinnanvaiheen valintatapajono\","+
                "\"jonosija\": 16,"+
                "\"paasyJaSoveltuvuusKokeenTulos\": null,"+
                "\"varasijanNumero\": null,"+
                "\"tila\": \"HYVAKSYTTY\","+
                "\"tilanKuvaukset\": {},"+
                "\"vastaanottotieto\": \"ILMOITETTU\","+
                "\"hyvaksyttyHarkinnanvaraisesti\": false,"+
                "\"tasasijaJonosija\": 1,"+
                "\"pisteet\": 18,"+
                "\"alinHyvaksyttyPistemaara\": 18,"+
                "\"hakeneet\": 209,"+
                "\"hyvaksytty\": 1,"+
                "\"varalla\": 0"+
                "}"+
                "]"+
                "}"+
                "]"+
                "}"+
                "";

        Gson gson = new Gson();
        HakijaDTO hakijaDTO = gson.fromJson(response, HakijaDTO.class);
        return hakijaDTO;
    }

    private ValintakoeOsallistuminenDTO getOsallistuminen(String applicationOid) {

        String response = "\n" +
                "\n" +
                "{\n" +
                "  \"hakuOid\": \"1.2.246.562.5.2013080813081926341927\",\n" +
                "  \"hakemusOid\": \"1.2.246.562.11.00000005610\",\n" +
                "  \"hakijaOid\": \"1.2.246.562.24.85139828495\",\n" +
                "  \"etunimi\": \"Neea V\",\n" +
                "  \"sukunimi\": \"Ylävuori\",\n" +
                "  \"createdAt\": 1394457863526,\n" +
                "  \"hakutoiveet\": [\n" +
                "    {\n" +
                "      \"hakukohdeOid\": \"1.2.246.562.5.27721348947\",\n" +
                "      \"valinnanVaiheet\": [\n" +
                "        {\n" +
                "          \"valinnanVaiheOid\": \"13935724862225830800449735537001\",\n" +
                "          \"valinnanVaiheJarjestysluku\": 1,\n" +
                "          \"valintakokeet\": [\n" +
                "            {\n" +
                "              \"valintakoeOid\": \"1393572486402-781819533309627936\",\n" +
                "              \"valintakoeTunniste\": \"kielikoe_fi\",\n" +
                "              \"nimi\": \"Kielikoe\",\n" +
                "              \"aktiivinen\": false,\n" +
                "              \"osallistuminenTulos\": {\n" +
                "                \"osallistuminen\": \"EI_OSALLISTU\",\n" +
                "                \"kuvaus\": null,\n" +
                "                \"laskentaTila\": \"HYVAKSYTTAVISSA\",\n" +
                "                \"laskentaTulos\": false\n" +
                "              }\n" +
                "            },\n" +
                "            {\n" +
                "              \"valintakoeOid\": \"1393572486407-6573882988271963639\",\n" +
                "              \"valintakoeTunniste\": \"1_2_246_562_5_27721348947_urheilija_lisapiste\",\n" +
                "              \"nimi\": \"Urheilijalisäpiste\",\n" +
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
                "    },\n" +
                "    {\n" +
                "      \"hakukohdeOid\": \"1.2.246.562.5.85532589612\",\n" +
                "      \"valinnanVaiheet\": [\n" +
                "        {\n" +
                "          \"valinnanVaiheOid\": \"1393571255074-7307039926365334377\",\n" +
                "          \"valinnanVaiheJarjestysluku\": 1,\n" +
                "          \"valintakokeet\": [\n" +
                "            {\n" +
                "              \"valintakoeOid\": \"1393571255124-1391351671714830435\",\n" +
                "              \"valintakoeTunniste\": \"kielikoe_fi\",\n" +
                "              \"nimi\": \"Kielikoe\",\n" +
                "              \"aktiivinen\": false,\n" +
                "              \"osallistuminenTulos\": {\n" +
                "                \"osallistuminen\": \"EI_OSALLISTU\",\n" +
                "                \"kuvaus\": null,\n" +
                "                \"laskentaTila\": \"HYVAKSYTTAVISSA\",\n" +
                "                \"laskentaTulos\": false\n" +
                "              }\n" +
                "            },\n" +
                "            {\n" +
                "              \"valintakoeOid\": \"1393571255129-2346891365985056247\",\n" +
                "              \"valintakoeTunniste\": \"Eläintenhoidon koulutusohjelma, pk (Maatalousalan perustutkinto), pääsykoe\",\n" +
                "              \"nimi\": \"Eläintenhoidon koulutusohjelma, pk (Maatalousalan perustutkinto), pääsykoe\",\n" +
                "              \"aktiivinen\": false,\n" +
                "              \"osallistuminenTulos\": {\n" +
                "                \"osallistuminen\": \"OSALLISTUU\",\n" +
                "                \"kuvaus\": null,\n" +
                "                \"laskentaTila\": \"HYVAKSYTTAVISSA\",\n" +
                "                \"laskentaTulos\": true\n" +
                "              }\n" +
                "            },\n" +
                "            {\n" +
                "              \"valintakoeOid\": \"1393571255127-4738960705361812063\",\n" +
                "              \"valintakoeTunniste\": \"1_2_246_562_5_85532589612_urheilija_lisapiste\",\n" +
                "              \"nimi\": \"Urheilijalisäpiste\",\n" +
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
                "    },\n" +
                "    {\n" +
                "      \"hakukohdeOid\": \"1.2.246.562.5.259898287910\",\n" +
                "      \"valinnanVaiheet\": [\n" +
                "        {\n" +
                "          \"valinnanVaiheOid\": \"13935726727442102404043665568624\",\n" +
                "          \"valinnanVaiheJarjestysluku\": 1,\n" +
                "          \"valintakokeet\": [\n" +
                "            {\n" +
                "              \"valintakoeOid\": \"1393572672977792870099537023149\",\n" +
                "              \"valintakoeTunniste\": \"kielikoe_fi\",\n" +
                "              \"nimi\": \"Kielikoe\",\n" +
                "              \"aktiivinen\": false,\n" +
                "              \"osallistuminenTulos\": {\n" +
                "                \"osallistuminen\": \"EI_OSALLISTU\",\n" +
                "                \"kuvaus\": null,\n" +
                "                \"laskentaTila\": \"HYVAKSYTTAVISSA\",\n" +
                "                \"laskentaTulos\": false\n" +
                "              }\n" +
                "            },\n" +
                "            {\n" +
                "              \"valintakoeOid\": \"1393572672971-3745941139794590770\",\n" +
                "              \"valintakoeTunniste\": \"1_2_246_562_5_259898287910_urheilija_lisapiste\",\n" +
                "              \"nimi\": \"Urheilijalisäpiste\",\n" +
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
                "    },\n" +
                "    {\n" +
                "      \"hakukohdeOid\": \"1.2.246.562.5.37175137688\",\n" +
                "      \"valinnanVaiheet\": [\n" +
                "        {\n" +
                "          \"valinnanVaiheOid\": \"13935705656376300801256576588350\",\n" +
                "          \"valinnanVaiheJarjestysluku\": 1,\n" +
                "          \"valintakokeet\": [\n" +
                "            {\n" +
                "              \"valintakoeOid\": \"1393570565721-4424479167527568504\",\n" +
                "              \"valintakoeTunniste\": \"1_2_246_562_5_37175137688_paasykoe\",\n" +
                "              \"nimi\": \"Pääsykoe\",\n" +
                "              \"aktiivinen\": false,\n" +
                "              \"osallistuminenTulos\": {\n" +
                "                \"osallistuminen\": \"OSALLISTUU\",\n" +
                "                \"kuvaus\": null,\n" +
                "                \"laskentaTila\": \"HYVAKSYTTAVISSA\",\n" +
                "                \"laskentaTulos\": true\n" +
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

        //gson = new Gson();

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
