package fi.vm.sade.haku.virkailija.valinta.dto;

public enum HakemuksenTila {

    HYLATTY, // hakija ei voi koskaan tulla valituksi kohteeseen

    VARALLA, // Hakija voi tulla kohteeseen valituksi (jossain vaiheessa)

    PERUUNTUNUT, // Hakija on tullut valituksi parempaan paikkaan (korkeampi
    // hakutoive)

    HYVAKSYTTY, // Hakija voi ottaa paikan vastaan

    VARASIJALTA_HYVAKSYTTY, //Hakija voi ottaa paikan vastaan (alunperin varasijalla)

    HARKINNANVARAISESTI_HYVAKSYTTY,

    PERUNUT, // Hakija ei ole vastaanottanut paikkaa. Hakija ei voi tulla enää
    // valituksi matalamman prioriteetin kohteissa

    PERUUTETTU; // Virkailija on perunut paikan. Sama toiminnallisuuks kuil
    // HYLATTY
}
