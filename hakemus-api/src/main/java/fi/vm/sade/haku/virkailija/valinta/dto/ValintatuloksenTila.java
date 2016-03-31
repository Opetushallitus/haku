package fi.vm.sade.haku.virkailija.valinta.dto;

public enum ValintatuloksenTila {
    VASTAANOTTANUT,
    EI_VASTAANOTETTU_MAARA_AIKANA,  // Hakija ei ole ilmoittanut paikkaa vastaanotetuksi maaraaikana ja on nain ollen hylatty
    PERUNUT,                        // Hakija ei ota paikkaa vastaan
    PERUUTETTU,                     // Hakijan tila on peruutettu
    EHDOLLISESTI_VASTAANOTTANUT,    // Ehdollisesti vastaanottanut
    VASTAANOTTANUT_SITOVASTI,       // Sitovasti vastaanottanut, kk-tila
    KESKEN
}
