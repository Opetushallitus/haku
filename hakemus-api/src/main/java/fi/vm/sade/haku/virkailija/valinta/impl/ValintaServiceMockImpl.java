package fi.vm.sade.haku.virkailija.valinta.impl;

import com.google.gson.*;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.lomake.exception.IllegalStateException;
import fi.vm.sade.haku.virkailija.valinta.ValintaService;
import fi.vm.sade.haku.virkailija.valinta.ValintaServiceCallFailedException;
import fi.vm.sade.haku.virkailija.valinta.dto.HakemusDTO;
import fi.vm.sade.haku.virkailija.valinta.dto.HakijaDTO;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Profile(value = {"dev", "it"})
public class ValintaServiceMockImpl implements ValintaService {

    @Override
    public HakemusDTO getHakemus(String asOid, String applicationOid) {
        String response = "\n" +
                "\n" +
                "{\n" +
                "  \"hakuoid\": \"1.2.246.562.5.2013080813081926341927\",\n" +
                "  \"hakemusoid\": \"1.2.246.562.11.00000588700\",\n" +
                "  \"hakukohteet\": [\n" +
                "    {\n" +
                "      \"hakuoid\": \"1.2.246.562.5.2013080813081926341927\",\n" +
                "      \"tarjoajaoid\": \"1.2.246.562.10.2014011510225267315409\",\n" +
                "      \"hakukohdeoid\": \"1.2.246.562.14.2013093009592081139737\",\n" +
                "      \"valinnanvaihe\": [\n" +
                "        {\n" +
                "          \"jarjestysnumero\": 0,\n" +
                "          \"valinnanvaiheoid\": \"1396419423812-5713350404607086894\",\n" +
                "          \"nimi\": null,\n" +
                "          \"createdAt\": 1396611492980,\n" +
                "          \"valintatapajono\": [\n" +
                "            {\n" +
                "              \"valintatapajonooid\": \"13964194238265718024165912404037\",\n" +
                "              \"nimi\": \"Harkinnanvaraisten käsittelyvaiheen valintatapajono\",\n" +
                "              \"prioriteetti\": 0,\n" +
                "              \"aloituspaikat\": 0,\n" +
                "              \"siirretaanSijoitteluun\": false,\n" +
                "              \"tasasijasaanto\": \"ARVONTA\",\n" +
                "              \"eiVarasijatayttoa\": null,\n" +
                "              \"jonosijat\": [\n" +
                "                {\n" +
                "                  \"jonosija\": 1,\n" +
                "                  \"hakemusOid\": \"1.2.246.562.11.00000588700\",\n" +
                "                  \"hakijaOid\": null,\n" +
                "                  \"jarjestyskriteerit\": [\n" +
                "                    {\n" +
                "                      \"arvo\": null,\n" +
                "                      \"tila\": \"HYVAKSYTTAVISSA\",\n" +
                "                      \"kuvaus\": null,\n" +
                "                      \"prioriteetti\": 0,\n" +
                "                      \"nimi\": \"Ulkomailla suoritettu koulutus tai oppivelvollisuuden suorittaminen keskeytynyt\"\n" +
                "                    }\n" +
                "                  ],\n" +
                "                  \"prioriteetti\": 4,\n" +
                "                  \"sukunimi\": \"Poikkeava\",\n" +
                "                  \"etunimi\": \"Pasi Petteri\",\n" +
                "                  \"harkinnanvarainen\": false,\n" +
                "                  \"tuloksenTila\": \"HYVAKSYTTAVISSA\",\n" +
                "                  \"historiat\": null,\n" +
                "                  \"syotetytArvot\": [],\n" +
                "                  \"funktioTulokset\": [],\n" +
                "                  \"muokattu\": false\n" +
                "                }\n" +
                "              ],\n" +
                "              \"oid\": \"13964194238265718024165912404037\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"valintakokeet\": []\n" +
                "        },\n" +
                "        {\n" +
                "          \"jarjestysnumero\": 2,\n" +
                "          \"valinnanvaiheoid\": \"1396419423797-911502247801462617\",\n" +
                "          \"nimi\": null,\n" +
                "          \"createdAt\": 1396611493339,\n" +
                "          \"valintatapajono\": [\n" +
                "            {\n" +
                "              \"valintatapajonooid\": \"1396419423942-4372334193728754043\",\n" +
                "              \"nimi\": \"Varsinaisen valinnanvaiheen valintatapajono\",\n" +
                "              \"prioriteetti\": 0,\n" +
                "              \"aloituspaikat\": 5,\n" +
                "              \"siirretaanSijoitteluun\": true,\n" +
                "              \"tasasijasaanto\": \"ARVONTA\",\n" +
                "              \"eiVarasijatayttoa\": null,\n" +
                "              \"jonosijat\": [\n" +
                "                {\n" +
                "                  \"jonosija\": 1,\n" +
                "                  \"hakemusOid\": \"1.2.246.562.11.00000588700\",\n" +
                "                  \"hakijaOid\": null,\n" +
                "                  \"jarjestyskriteerit\": [\n" +
                "                    {\n" +
                "                      \"arvo\": 6,\n" +
                "                      \"tila\": \"HYVAKSYTTAVISSA\",\n" +
                "                      \"kuvaus\": null,\n" +
                "                      \"prioriteetti\": 0,\n" +
                "                      \"nimi\": \"paasykoe_tunniste + hylkäysperusteet (*)\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"arvo\": 2,\n" +
                "                      \"tila\": \"HYVAKSYTTAVISSA\",\n" +
                "                      \"kuvaus\": null,\n" +
                "                      \"prioriteetti\": 1,\n" +
                "                      \"nimi\": \"Hakutoivejärjestystasapistetilanne, 2 aste, pk ja yo\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"arvo\": 6,\n" +
                "                      \"tila\": \"HYVAKSYTTAVISSA\",\n" +
                "                      \"kuvaus\": null,\n" +
                "                      \"prioriteetti\": 2,\n" +
                "                      \"nimi\": \"paasykoe_tunniste\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"arvo\": 13,\n" +
                "                      \"tila\": \"HYVAKSYTTAVISSA\",\n" +
                "                      \"kuvaus\": null,\n" +
                "                      \"prioriteetti\": 3,\n" +
                "                      \"nimi\": \"Yleinen koulumenestys pisteytysmalli, PK\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"arvo\": 7,\n" +
                "                      \"tila\": \"HYVAKSYTTAVISSA\",\n" +
                "                      \"kuvaus\": null,\n" +
                "                      \"prioriteetti\": 4,\n" +
                "                      \"nimi\": \"Painotettavat arvosanat pisteytysmalli, PK\"\n" +
                "                    }\n" +
                "                  ],\n" +
                "                  \"prioriteetti\": 4,\n" +
                "                  \"sukunimi\": \"Poikkeava\",\n" +
                "                  \"etunimi\": \"Pasi Petteri\",\n" +
                "                  \"harkinnanvarainen\": false,\n" +
                "                  \"tuloksenTila\": \"HYVAKSYTTAVISSA\",\n" +
                "                  \"historiat\": null,\n" +
                "                  \"syotetytArvot\": [\n" +
                "                    {\n" +
                "                      \"tunniste\": \"1_2_246_562_14_2013093009592081139737_paasykoe\",\n" +
                "                      \"arvo\": \"6\",\n" +
                "                      \"laskennallinenArvo\": \"6\",\n" +
                "                      \"osallistuminen\": \"OSALLISTUI\"\n" +
                "                    }\n" +
                "                  ],\n" +
                "                  \"funktioTulokset\": [\n" +
                "                    {\n" +
                "                      \"tunniste\": \"painotetutarvosanat_pk\",\n" +
                "                      \"arvo\": \"7.0\"\n" +
                "                    }\n" +
                "                  ],\n" +
                "                  \"muokattu\": false\n" +
                "                }\n" +
                "              ],\n" +
                "              \"oid\": \"1396419423942-4372334193728754043\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"valintakokeet\": []\n" +
                "        },\n" +
                "        {\n" +
                "          \"jarjestysnumero\": 1,\n" +
                "          \"valinnanvaiheoid\": \"13964194238035621841454777128910\",\n" +
                "          \"nimi\": null,\n" +
                "          \"createdAt\": 1396611341064,\n" +
                "          \"valintatapajono\": [],\n" +
                "          \"valintakokeet\": [\n" +
                "            {\n" +
                "              \"valintakoeOid\": \"139641942392638961596659497833\",\n" +
                "              \"valintakoeTunniste\": \"kielikoe_fi\",\n" +
                "              \"nimi\": \"Kielikoe\",\n" +
                "              \"aktiivinen\": true,\n" +
                "              \"osallistuminenTulos\": {\n" +
                "                \"osallistuminen\": \"EI_OSALLISTU\",\n" +
                "                \"kuvaus\": null,\n" +
                "                \"laskentaTila\": \"HYVAKSYTTAVISSA\",\n" +
                "                \"laskentaTulos\": false\n" +
                "              },\n" +
                "              \"lahetetaankoKoekutsut\": true\n" +
                "            },\n" +
                "            {\n" +
                "              \"valintakoeOid\": \"1396419423922-4674304476227466258\",\n" +
                "              \"valintakoeTunniste\": \"1_2_246_562_14_2013093009592081139737_urheilija_lisapiste\",\n" +
                "              \"nimi\": \"Urheilijalisäpiste\",\n" +
                "              \"aktiivinen\": true,\n" +
                "              \"osallistuminenTulos\": {\n" +
                "                \"osallistuminen\": \"EI_OSALLISTU\",\n" +
                "                \"kuvaus\": null,\n" +
                "                \"laskentaTila\": \"HYVAKSYTTAVISSA\",\n" +
                "                \"laskentaTulos\": false\n" +
                "              },\n" +
                "              \"lahetetaankoKoekutsut\": false\n" +
                "            },\n" +
                "            {\n" +
                "              \"valintakoeOid\": \"1396419423929-7801058572861085693\",\n" +
                "              \"valintakoeTunniste\": \"1_2_246_562_14_2013093009592081139737_paasykoe\",\n" +
                "              \"nimi\": \"Pääsy- ja soveltuvuuskoe\",\n" +
                "              \"aktiivinen\": true,\n" +
                "              \"osallistuminenTulos\": {\n" +
                "                \"osallistuminen\": \"OSALLISTUU\",\n" +
                "                \"kuvaus\": null,\n" +
                "                \"laskentaTila\": \"HYVAKSYTTAVISSA\",\n" +
                "                \"laskentaTulos\": true\n" +
                "              },\n" +
                "              \"lahetetaankoKoekutsut\": true\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      ],\n" +
                "      \"oid\": \"1.2.246.562.14.2013093009592081139737\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"hakuoid\": \"1.2.246.562.5.2013080813081926341927\",\n" +
                "      \"tarjoajaoid\": \"1.2.246.562.10.82490543947\",\n" +
                "      \"hakukohdeoid\": \"1.2.246.562.5.97391796386\",\n" +
                "      \"valinnanvaihe\": [\n" +
                "        {\n" +
                "          \"jarjestysnumero\": 0,\n" +
                "          \"valinnanvaiheoid\": \"13964194252203329360898738598543\",\n" +
                "          \"nimi\": null,\n" +
                "          \"createdAt\": 1396611435350,\n" +
                "          \"valintatapajono\": [\n" +
                "            {\n" +
                "              \"valintatapajonooid\": \"1396419425248-849593309662818464\",\n" +
                "              \"nimi\": \"Harkinnanvaraisten käsittelyvaiheen valintatapajono\",\n" +
                "              \"prioriteetti\": 0,\n" +
                "              \"aloituspaikat\": 0,\n" +
                "              \"siirretaanSijoitteluun\": false,\n" +
                "              \"tasasijasaanto\": \"ARVONTA\",\n" +
                "              \"eiVarasijatayttoa\": null,\n" +
                "              \"jonosijat\": [\n" +
                "                {\n" +
                "                  \"jonosija\": 1,\n" +
                "                  \"hakemusOid\": \"1.2.246.562.11.00000588700\",\n" +
                "                  \"hakijaOid\": null,\n" +
                "                  \"jarjestyskriteerit\": [\n" +
                "                    {\n" +
                "                      \"arvo\": null,\n" +
                "                      \"tila\": \"HYVAKSYTTAVISSA\",\n" +
                "                      \"kuvaus\": null,\n" +
                "                      \"prioriteetti\": 0,\n" +
                "                      \"nimi\": \"Ulkomailla suoritettu koulutus tai oppivelvollisuuden suorittaminen keskeytynyt\"\n" +
                "                    }\n" +
                "                  ],\n" +
                "                  \"prioriteetti\": 3,\n" +
                "                  \"sukunimi\": \"Poikkeava\",\n" +
                "                  \"etunimi\": \"Pasi Petteri\",\n" +
                "                  \"harkinnanvarainen\": false,\n" +
                "                  \"tuloksenTila\": \"HYVAKSYTTAVISSA\",\n" +
                "                  \"historiat\": null,\n" +
                "                  \"syotetytArvot\": [],\n" +
                "                  \"funktioTulokset\": [],\n" +
                "                  \"muokattu\": false\n" +
                "                }\n" +
                "              ],\n" +
                "              \"oid\": \"1396419425248-849593309662818464\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"valintakokeet\": []\n" +
                "        },\n" +
                "        {\n" +
                "          \"jarjestysnumero\": 2,\n" +
                "          \"valinnanvaiheoid\": \"13964194251927800896258708880413\",\n" +
                "          \"nimi\": null,\n" +
                "          \"createdAt\": 1396611435536,\n" +
                "          \"valintatapajono\": [\n" +
                "            {\n" +
                "              \"valintatapajonooid\": \"1396419425427-2654074026725654006\",\n" +
                "              \"nimi\": \"Varsinaisen valinnanvaiheen valintatapajono\",\n" +
                "              \"prioriteetti\": 0,\n" +
                "              \"aloituspaikat\": 3,\n" +
                "              \"siirretaanSijoitteluun\": true,\n" +
                "              \"tasasijasaanto\": \"ARVONTA\",\n" +
                "              \"eiVarasijatayttoa\": null,\n" +
                "              \"jonosijat\": [\n" +
                "                {\n" +
                "                  \"jonosija\": 1,\n" +
                "                  \"hakemusOid\": \"1.2.246.562.11.00000588700\",\n" +
                "                  \"hakijaOid\": null,\n" +
                "                  \"jarjestyskriteerit\": [\n" +
                "                    {\n" +
                "                      \"arvo\": 7,\n" +
                "                      \"tila\": \"HYVAKSYTTAVISSA\",\n" +
                "                      \"kuvaus\": null,\n" +
                "                      \"prioriteetti\": 0,\n" +
                "                      \"nimi\": \"paasykoe_tunniste + hylkäysperusteet (*)\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"arvo\": 3,\n" +
                "                      \"tila\": \"HYVAKSYTTAVISSA\",\n" +
                "                      \"kuvaus\": null,\n" +
                "                      \"prioriteetti\": 1,\n" +
                "                      \"nimi\": \"Hakutoivejärjestystasapistetilanne, 2 aste, pk ja yo\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"arvo\": 7,\n" +
                "                      \"tila\": \"HYVAKSYTTAVISSA\",\n" +
                "                      \"kuvaus\": null,\n" +
                "                      \"prioriteetti\": 2,\n" +
                "                      \"nimi\": \"paasykoe_tunniste\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"arvo\": 13,\n" +
                "                      \"tila\": \"HYVAKSYTTAVISSA\",\n" +
                "                      \"kuvaus\": null,\n" +
                "                      \"prioriteetti\": 3,\n" +
                "                      \"nimi\": \"Yleinen koulumenestys pisteytysmalli, PK\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"arvo\": 7,\n" +
                "                      \"tila\": \"HYVAKSYTTAVISSA\",\n" +
                "                      \"kuvaus\": null,\n" +
                "                      \"prioriteetti\": 4,\n" +
                "                      \"nimi\": \"Painotettavat arvosanat pisteytysmalli, PK\"\n" +
                "                    }\n" +
                "                  ],\n" +
                "                  \"prioriteetti\": 3,\n" +
                "                  \"sukunimi\": \"Poikkeava\",\n" +
                "                  \"etunimi\": \"Pasi Petteri\",\n" +
                "                  \"harkinnanvarainen\": false,\n" +
                "                  \"tuloksenTila\": \"HYVAKSYTTAVISSA\",\n" +
                "                  \"historiat\": null,\n" +
                "                  \"syotetytArvot\": [\n" +
                "                    {\n" +
                "                      \"tunniste\": \"1_2_246_562_5_97391796386_paasykoe\",\n" +
                "                      \"arvo\": \"7\",\n" +
                "                      \"laskennallinenArvo\": \"7\",\n" +
                "                      \"osallistuminen\": \"OSALLISTUI\"\n" +
                "                    }\n" +
                "                  ],\n" +
                "                  \"funktioTulokset\": [\n" +
                "                    {\n" +
                "                      \"tunniste\": \"painotetutarvosanat_pk\",\n" +
                "                      \"arvo\": \"7.0\"\n" +
                "                    }\n" +
                "                  ],\n" +
                "                  \"muokattu\": false\n" +
                "                }\n" +
                "              ],\n" +
                "              \"oid\": \"1396419425427-2654074026725654006\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"valintakokeet\": []\n" +
                "        },\n" +
                "        {\n" +
                "          \"jarjestysnumero\": 1,\n" +
                "          \"valinnanvaiheoid\": \"13964194251957138237961965694231\",\n" +
                "          \"nimi\": null,\n" +
                "          \"createdAt\": 1396611341064,\n" +
                "          \"valintatapajono\": [],\n" +
                "          \"valintakokeet\": [\n" +
                "            {\n" +
                "              \"valintakoeOid\": \"13964194254102794981968322229178\",\n" +
                "              \"valintakoeTunniste\": \"1_2_246_562_5_97391796386_paasykoe\",\n" +
                "              \"nimi\": \"Pääsy- ja soveltuvuuskoe\",\n" +
                "              \"aktiivinen\": true,\n" +
                "              \"osallistuminenTulos\": {\n" +
                "                \"osallistuminen\": \"OSALLISTUU\",\n" +
                "                \"kuvaus\": null,\n" +
                "                \"laskentaTila\": \"HYVAKSYTTAVISSA\",\n" +
                "                \"laskentaTulos\": true\n" +
                "              },\n" +
                "              \"lahetetaankoKoekutsut\": true\n" +
                "            },\n" +
                "            {\n" +
                "              \"valintakoeOid\": \"1396419425378-6002401187199953285\",\n" +
                "              \"valintakoeTunniste\": \"1_2_246_562_5_97391796386_urheilija_lisapiste\",\n" +
                "              \"nimi\": \"Urheilijalisäpiste\",\n" +
                "              \"aktiivinen\": true,\n" +
                "              \"osallistuminenTulos\": {\n" +
                "                \"osallistuminen\": \"EI_OSALLISTU\",\n" +
                "                \"kuvaus\": null,\n" +
                "                \"laskentaTila\": \"HYVAKSYTTAVISSA\",\n" +
                "                \"laskentaTulos\": false\n" +
                "              },\n" +
                "              \"lahetetaankoKoekutsut\": false\n" +
                "            },\n" +
                "            {\n" +
                "              \"valintakoeOid\": \"1396419425403-3986332053775557549\",\n" +
                "              \"valintakoeTunniste\": \"kielikoe_fi\",\n" +
                "              \"nimi\": \"Kielikoe\",\n" +
                "              \"aktiivinen\": true,\n" +
                "              \"osallistuminenTulos\": {\n" +
                "                \"osallistuminen\": \"EI_OSALLISTU\",\n" +
                "                \"kuvaus\": null,\n" +
                "                \"laskentaTila\": \"HYVAKSYTTAVISSA\",\n" +
                "                \"laskentaTulos\": false\n" +
                "              },\n" +
                "              \"lahetetaankoKoekutsut\": true\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      ],\n" +
                "      \"oid\": \"1.2.246.562.5.97391796386\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"hakuoid\": \"1.2.246.562.5.2013080813081926341927\",\n" +
                "      \"tarjoajaoid\": \"1.2.246.562.10.82490543947\",\n" +
                "      \"hakukohdeoid\": \"1.2.246.562.5.90789644623\",\n" +
                "      \"valinnanvaihe\": [\n" +
                "        {\n" +
                "          \"jarjestysnumero\": 0,\n" +
                "          \"valinnanvaiheoid\": \"13964192921877870796332149451276\",\n" +
                "          \"nimi\": null,\n" +
                "          \"createdAt\": 1396611435727,\n" +
                "          \"valintatapajono\": [\n" +
                "            {\n" +
                "              \"valintatapajonooid\": \"13964192921971278244889467555450\",\n" +
                "              \"nimi\": \"Harkinnanvaraisten käsittelyvaiheen valintatapajono\",\n" +
                "              \"prioriteetti\": 0,\n" +
                "              \"aloituspaikat\": 0,\n" +
                "              \"siirretaanSijoitteluun\": false,\n" +
                "              \"tasasijasaanto\": \"ARVONTA\",\n" +
                "              \"eiVarasijatayttoa\": null,\n" +
                "              \"jonosijat\": [\n" +
                "                {\n" +
                "                  \"jonosija\": 1,\n" +
                "                  \"hakemusOid\": \"1.2.246.562.11.00000588700\",\n" +
                "                  \"hakijaOid\": null,\n" +
                "                  \"jarjestyskriteerit\": [\n" +
                "                    {\n" +
                "                      \"arvo\": null,\n" +
                "                      \"tila\": \"HYVAKSYTTAVISSA\",\n" +
                "                      \"kuvaus\": null,\n" +
                "                      \"prioriteetti\": 0,\n" +
                "                      \"nimi\": \"Ulkomailla suoritettu koulutus tai oppivelvollisuuden suorittaminen keskeytynyt\"\n" +
                "                    }\n" +
                "                  ],\n" +
                "                  \"prioriteetti\": 2,\n" +
                "                  \"sukunimi\": \"Poikkeava\",\n" +
                "                  \"etunimi\": \"Pasi Petteri\",\n" +
                "                  \"harkinnanvarainen\": false,\n" +
                "                  \"tuloksenTila\": \"HYVAKSYTTAVISSA\",\n" +
                "                  \"historiat\": null,\n" +
                "                  \"syotetytArvot\": [],\n" +
                "                  \"funktioTulokset\": [],\n" +
                "                  \"muokattu\": false\n" +
                "                }\n" +
                "              ],\n" +
                "              \"oid\": \"13964192921971278244889467555450\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"valintakokeet\": []\n" +
                "        },\n" +
                "        {\n" +
                "          \"jarjestysnumero\": 2,\n" +
                "          \"valinnanvaiheoid\": \"13964192921764479870173622014143\",\n" +
                "          \"nimi\": null,\n" +
                "          \"createdAt\": 1396611435897,\n" +
                "          \"valintatapajono\": [\n" +
                "            {\n" +
                "              \"valintatapajonooid\": \"13964192922798309604668909662232\",\n" +
                "              \"nimi\": \"Varsinaisen valinnanvaiheen valintatapajono\",\n" +
                "              \"prioriteetti\": 0,\n" +
                "              \"aloituspaikat\": 6,\n" +
                "              \"siirretaanSijoitteluun\": true,\n" +
                "              \"tasasijasaanto\": \"ARVONTA\",\n" +
                "              \"eiVarasijatayttoa\": null,\n" +
                "              \"jonosijat\": [\n" +
                "                {\n" +
                "                  \"jonosija\": 1,\n" +
                "                  \"hakemusOid\": \"1.2.246.562.11.00000588700\",\n" +
                "                  \"hakijaOid\": null,\n" +
                "                  \"jarjestyskriteerit\": [\n" +
                "                    {\n" +
                "                      \"arvo\": 4,\n" +
                "                      \"tila\": \"HYVAKSYTTAVISSA\",\n" +
                "                      \"kuvaus\": null,\n" +
                "                      \"prioriteetti\": 0,\n" +
                "                      \"nimi\": \"paasykoe_tunniste + hylkäysperusteet (*)\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"arvo\": 4,\n" +
                "                      \"tila\": \"HYVAKSYTTAVISSA\",\n" +
                "                      \"kuvaus\": null,\n" +
                "                      \"prioriteetti\": 1,\n" +
                "                      \"nimi\": \"Hakutoivejärjestystasapistetilanne, 2 aste, pk ja yo\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"arvo\": 4,\n" +
                "                      \"tila\": \"HYVAKSYTTAVISSA\",\n" +
                "                      \"kuvaus\": null,\n" +
                "                      \"prioriteetti\": 2,\n" +
                "                      \"nimi\": \"paasykoe_tunniste\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"arvo\": 13,\n" +
                "                      \"tila\": \"HYVAKSYTTAVISSA\",\n" +
                "                      \"kuvaus\": null,\n" +
                "                      \"prioriteetti\": 3,\n" +
                "                      \"nimi\": \"Yleinen koulumenestys pisteytysmalli, PK\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"arvo\": 7,\n" +
                "                      \"tila\": \"HYVAKSYTTAVISSA\",\n" +
                "                      \"kuvaus\": null,\n" +
                "                      \"prioriteetti\": 4,\n" +
                "                      \"nimi\": \"Painotettavat arvosanat pisteytysmalli, PK\"\n" +
                "                    }\n" +
                "                  ],\n" +
                "                  \"prioriteetti\": 2,\n" +
                "                  \"sukunimi\": \"Poikkeava\",\n" +
                "                  \"etunimi\": \"Pasi Petteri\",\n" +
                "                  \"harkinnanvarainen\": false,\n" +
                "                  \"tuloksenTila\": \"HYVAKSYTTAVISSA\",\n" +
                "                  \"historiat\": null,\n" +
                "                  \"syotetytArvot\": [\n" +
                "                    {\n" +
                "                      \"tunniste\": \"1_2_246_562_5_90789644623_paasykoe\",\n" +
                "                      \"arvo\": \"4\",\n" +
                "                      \"laskennallinenArvo\": \"4\",\n" +
                "                      \"osallistuminen\": \"OSALLISTUI\"\n" +
                "                    }\n" +
                "                  ],\n" +
                "                  \"funktioTulokset\": [\n" +
                "                    {\n" +
                "                      \"tunniste\": \"paasykoe\",\n" +
                "                      \"arvo\": \"4\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"tunniste\": \"painotetutarvosanat_pk\",\n" +
                "                      \"arvo\": \"7.0\"\n" +
                "                    }\n" +
                "                  ],\n" +
                "                  \"muokattu\": false\n" +
                "                }\n" +
                "              ],\n" +
                "              \"oid\": \"13964192922798309604668909662232\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"valintakokeet\": []\n" +
                "        },\n" +
                "        {\n" +
                "          \"jarjestysnumero\": 1,\n" +
                "          \"valinnanvaiheoid\": \"1396419292179-4846418670571721909\",\n" +
                "          \"nimi\": null,\n" +
                "          \"createdAt\": 1396611341064,\n" +
                "          \"valintatapajono\": [],\n" +
                "          \"valintakokeet\": [\n" +
                "            {\n" +
                "              \"valintakoeOid\": \"1396419292251-6005290515981955166\",\n" +
                "              \"valintakoeTunniste\": \"1_2_246_562_5_90789644623_urheilija_lisapiste\",\n" +
                "              \"nimi\": \"Urheilijalisäpiste\",\n" +
                "              \"aktiivinen\": true,\n" +
                "              \"osallistuminenTulos\": {\n" +
                "                \"osallistuminen\": \"EI_OSALLISTU\",\n" +
                "                \"kuvaus\": null,\n" +
                "                \"laskentaTila\": \"HYVAKSYTTAVISSA\",\n" +
                "                \"laskentaTulos\": false\n" +
                "              },\n" +
                "              \"lahetetaankoKoekutsut\": false\n" +
                "            },\n" +
                "            {\n" +
                "              \"valintakoeOid\": \"1396419292267-8489444187189045253\",\n" +
                "              \"valintakoeTunniste\": \"kielikoe_fi\",\n" +
                "              \"nimi\": \"Kielikoe\",\n" +
                "              \"aktiivinen\": true,\n" +
                "              \"osallistuminenTulos\": {\n" +
                "                \"osallistuminen\": \"EI_OSALLISTU\",\n" +
                "                \"kuvaus\": null,\n" +
                "                \"laskentaTila\": \"HYVAKSYTTAVISSA\",\n" +
                "                \"laskentaTulos\": false\n" +
                "              },\n" +
                "              \"lahetetaankoKoekutsut\": true\n" +
                "            },\n" +
                "            {\n" +
                "              \"valintakoeOid\": \"13964192922634725069761386059565\",\n" +
                "              \"valintakoeTunniste\": \"1_2_246_562_5_90789644623_paasykoe\",\n" +
                "              \"nimi\": \"Pääsy- ja soveltuvuuskoe\",\n" +
                "              \"aktiivinen\": true,\n" +
                "              \"osallistuminenTulos\": {\n" +
                "                \"osallistuminen\": \"OSALLISTUU\",\n" +
                "                \"kuvaus\": null,\n" +
                "                \"laskentaTila\": \"HYVAKSYTTAVISSA\",\n" +
                "                \"laskentaTulos\": true\n" +
                "              },\n" +
                "              \"lahetetaankoKoekutsut\": true\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      ],\n" +
                "      \"oid\": \"1.2.246.562.5.90789644623\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"hakuoid\": \"1.2.246.562.5.2013080813081926341927\",\n" +
                "      \"tarjoajaoid\": \"1.2.246.562.10.34178172895\",\n" +
                "      \"hakukohdeoid\": \"1.2.246.562.5.763359326210\",\n" +
                "      \"valinnanvaihe\": [\n" +
                "        {\n" +
                "          \"jarjestysnumero\": 0,\n" +
                "          \"valinnanvaiheoid\": \"13964194165169148952594065436252\",\n" +
                "          \"nimi\": null,\n" +
                "          \"createdAt\": 1396611497744,\n" +
                "          \"valintatapajono\": [\n" +
                "            {\n" +
                "              \"valintatapajonooid\": \"1396419416549-3817000207328810606\",\n" +
                "              \"nimi\": \"Harkinnanvaraisten käsittelyvaiheen valintatapajono\",\n" +
                "              \"prioriteetti\": 0,\n" +
                "              \"aloituspaikat\": 0,\n" +
                "              \"siirretaanSijoitteluun\": false,\n" +
                "              \"tasasijasaanto\": \"ARVONTA\",\n" +
                "              \"eiVarasijatayttoa\": null,\n" +
                "              \"jonosijat\": [\n" +
                "                {\n" +
                "                  \"jonosija\": 1,\n" +
                "                  \"hakemusOid\": \"1.2.246.562.11.00000588700\",\n" +
                "                  \"hakijaOid\": null,\n" +
                "                  \"jarjestyskriteerit\": [\n" +
                "                    {\n" +
                "                      \"arvo\": null,\n" +
                "                      \"tila\": \"HYVAKSYTTAVISSA\",\n" +
                "                      \"kuvaus\": null,\n" +
                "                      \"prioriteetti\": 0,\n" +
                "                      \"nimi\": \"Ulkomailla suoritettu koulutus tai oppivelvollisuuden suorittaminen keskeytynyt\"\n" +
                "                    }\n" +
                "                  ],\n" +
                "                  \"prioriteetti\": 1,\n" +
                "                  \"sukunimi\": \"Poikkeava\",\n" +
                "                  \"etunimi\": \"Pasi Petteri\",\n" +
                "                  \"harkinnanvarainen\": false,\n" +
                "                  \"tuloksenTila\": \"HYVAKSYTTAVISSA\",\n" +
                "                  \"historiat\": null,\n" +
                "                  \"syotetytArvot\": [],\n" +
                "                  \"funktioTulokset\": [],\n" +
                "                  \"muokattu\": false\n" +
                "                }\n" +
                "              ],\n" +
                "              \"oid\": \"1396419416549-3817000207328810606\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"valintakokeet\": []\n" +
                "        },\n" +
                "        {\n" +
                "          \"jarjestysnumero\": 2,\n" +
                "          \"valinnanvaiheoid\": \"1396419416493993821496587549210\",\n" +
                "          \"nimi\": null,\n" +
                "          \"createdAt\": 1396611497848,\n" +
                "          \"valintatapajono\": [\n" +
                "            {\n" +
                "              \"valintatapajonooid\": \"1396419416671-7078577739016941422\",\n" +
                "              \"nimi\": \"Varsinaisen valinnanvaiheen valintatapajono\",\n" +
                "              \"prioriteetti\": 0,\n" +
                "              \"aloituspaikat\": 15,\n" +
                "              \"siirretaanSijoitteluun\": true,\n" +
                "              \"tasasijasaanto\": \"ARVONTA\",\n" +
                "              \"eiVarasijatayttoa\": null,\n" +
                "              \"jonosijat\": [\n" +
                "                {\n" +
                "                  \"jonosija\": 1,\n" +
                "                  \"hakemusOid\": \"1.2.246.562.11.00000588700\",\n" +
                "                  \"hakijaOid\": null,\n" +
                "                  \"jarjestyskriteerit\": [\n" +
                "                    {\n" +
                "                      \"arvo\": 8,\n" +
                "                      \"tila\": \"HYVAKSYTTAVISSA\",\n" +
                "                      \"kuvaus\": null,\n" +
                "                      \"prioriteetti\": 0,\n" +
                "                      \"nimi\": \"paasykoe_tunniste + hylkäysperusteet (*)\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"arvo\": 5,\n" +
                "                      \"tila\": \"HYVAKSYTTAVISSA\",\n" +
                "                      \"kuvaus\": null,\n" +
                "                      \"prioriteetti\": 1,\n" +
                "                      \"nimi\": \"Hakutoivejärjestystasapistetilanne, 2 aste, pk ja yo\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"arvo\": 8,\n" +
                "                      \"tila\": \"HYVAKSYTTAVISSA\",\n" +
                "                      \"kuvaus\": null,\n" +
                "                      \"prioriteetti\": 2,\n" +
                "                      \"nimi\": \"paasykoe_tunniste\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"arvo\": 13,\n" +
                "                      \"tila\": \"HYVAKSYTTAVISSA\",\n" +
                "                      \"kuvaus\": null,\n" +
                "                      \"prioriteetti\": 3,\n" +
                "                      \"nimi\": \"Yleinen koulumenestys pisteytysmalli, PK\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"arvo\": 7,\n" +
                "                      \"tila\": \"HYVAKSYTTAVISSA\",\n" +
                "                      \"kuvaus\": null,\n" +
                "                      \"prioriteetti\": 4,\n" +
                "                      \"nimi\": \"Painotettavat arvosanat pisteytysmalli, PK\"\n" +
                "                    }\n" +
                "                  ],\n" +
                "                  \"prioriteetti\": 1,\n" +
                "                  \"sukunimi\": \"Poikkeava\",\n" +
                "                  \"etunimi\": \"Pasi Petteri\",\n" +
                "                  \"harkinnanvarainen\": false,\n" +
                "                  \"tuloksenTila\": \"HYVAKSYTTAVISSA\",\n" +
                "                  \"historiat\": null,\n" +
                "                  \"syotetytArvot\": [\n" +
                "                    {\n" +
                "                      \"tunniste\": \"1_2_246_562_5_763359326210_paasykoe\",\n" +
                "                      \"arvo\": \"8\",\n" +
                "                      \"laskennallinenArvo\": \"8\",\n" +
                "                      \"osallistuminen\": \"OSALLISTUI\"\n" +
                "                    }\n" +
                "                  ],\n" +
                "                  \"funktioTulokset\": [\n" +
                "                    {\n" +
                "                      \"tunniste\": \"paasykoe\",\n" +
                "                      \"arvo\": \"8\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"tunniste\": \"painotetutarvosanat_pk\",\n" +
                "                      \"arvo\": \"7.0\"\n" +
                "                    }\n" +
                "                  ],\n" +
                "                  \"muokattu\": false\n" +
                "                }\n" +
                "              ],\n" +
                "              \"oid\": \"1396419416671-7078577739016941422\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"valintakokeet\": []\n" +
                "        },\n" +
                "        {\n" +
                "          \"jarjestysnumero\": 1,\n" +
                "          \"valinnanvaiheoid\": \"1396419416512-7566696945701819408\",\n" +
                "          \"nimi\": null,\n" +
                "          \"createdAt\": 1396611341064,\n" +
                "          \"valintatapajono\": [],\n" +
                "          \"valintakokeet\": [\n" +
                "            {\n" +
                "              \"valintakoeOid\": \"1396419416641832591379681058413\",\n" +
                "              \"valintakoeTunniste\": \"1_2_246_562_5_763359326210_urheilija_lisapiste\",\n" +
                "              \"nimi\": \"Urheilijalisäpiste\",\n" +
                "              \"aktiivinen\": true,\n" +
                "              \"osallistuminenTulos\": {\n" +
                "                \"osallistuminen\": \"EI_OSALLISTU\",\n" +
                "                \"kuvaus\": null,\n" +
                "                \"laskentaTila\": \"HYVAKSYTTAVISSA\",\n" +
                "                \"laskentaTulos\": false\n" +
                "              },\n" +
                "              \"lahetetaankoKoekutsut\": false\n" +
                "            },\n" +
                "            {\n" +
                "              \"valintakoeOid\": \"1396419416656-3104755298155361941\",\n" +
                "              \"valintakoeTunniste\": \"kielikoe_fi\",\n" +
                "              \"nimi\": \"Kielikoe\",\n" +
                "              \"aktiivinen\": true,\n" +
                "              \"osallistuminenTulos\": {\n" +
                "                \"osallistuminen\": \"EI_OSALLISTU\",\n" +
                "                \"kuvaus\": null,\n" +
                "                \"laskentaTila\": \"HYVAKSYTTAVISSA\",\n" +
                "                \"laskentaTulos\": false\n" +
                "              },\n" +
                "              \"lahetetaankoKoekutsut\": true\n" +
                "            },\n" +
                "            {\n" +
                "              \"valintakoeOid\": \"1396419416647-6093634029227616240\",\n" +
                "              \"valintakoeTunniste\": \"1_2_246_562_5_763359326210_paasykoe\",\n" +
                "              \"nimi\": \"Pääsy- ja soveltuvuuskoe\",\n" +
                "              \"aktiivinen\": true,\n" +
                "              \"osallistuminenTulos\": {\n" +
                "                \"osallistuminen\": \"OSALLISTUU\",\n" +
                "                \"kuvaus\": null,\n" +
                "                \"laskentaTila\": \"HYVAKSYTTAVISSA\",\n" +
                "                \"laskentaTulos\": true\n" +
                "              },\n" +
                "              \"lahetetaankoKoekutsut\": true\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      ],\n" +
                "      \"oid\": \"1.2.246.562.5.763359326210\"\n" +
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
        HakemusDTO hakemusDTO = gson.fromJson(response, HakemusDTO.class);
        return hakemusDTO;
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
                "\"vastaanottotieto\": \"KESKEN\","+
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

    @Override
    public Map<String, String> fetchValintaData(Application application) throws ValintaServiceCallFailedException{
        Map<String, String> result = new HashMap<>();
        for(Map<String, String> r : application.getAnswers().values()) {
            result.putAll(r);
        }
        return result;
    }


}
