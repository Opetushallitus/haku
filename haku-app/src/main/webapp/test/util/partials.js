var oulunYliopisto = "Oulun yliopisto, Humanistinen tiedekunta";
var helsinginYliopisto = "Helsingin yliopisto, Humanistinen tiedekunta";
var novia = "Yrkeshögskolan Novia, Raasepori";
var jarvenpaanDiakoniaAMk = "Diakonia-ammattikorkeakoulu, Järvenpään toimipiste"

var afrikkaKoulutus = "Afrikan ja Lähi-idän tutkimus, humanististen tieteiden kandidaatti ja filosofian maisteri";
var aasiaKoulutus = "Aasian tutkimus, humanististen tieteiden kandidaatti ja filosofian maisteri";
var raaseporiKoulutus = "Agrolog (YH)/Miljöplanerare (YH)/Skogsbruksingenjör (YH), dagstudier";
var ouluKoulutus = "Aate- ja oppihistoria, humanististen tieteiden kandidaatti ja filosofian maisteri";
var diakoniaKoulutus = "Sosionomi (AMK), monimuotototeutus"

function raasepori(n) {
    return valitseKoulutus(n, novia, raaseporiKoulutus);
}

function afrikka(n) {
    return valitseKoulutus(n, helsinginYliopisto, afrikkaKoulutus);
}

function aasia(n) {
    return valitseKoulutus(n, helsinginYliopisto, aasiaKoulutus);
}

function oulu(n) {
    return valitseKoulutus(n, oulunYliopisto, ouluKoulutus)
}

function diakonia(n) {
    return valitseKoulutus(n, jarvenpaanDiakoniaAMk, diakoniaKoulutus)
}

function henkilotiedotTestikaes() {
    return seq(
        input(
            lomake.sukunimi, "Testikäs",
            lomake.etunimet, "Asia Kas",
            lomake.kutsumanimi, "Asia",
            lomake.hetu, "171175-830Y"),
        wait.until(function() {
            return S("input#sukupuoli").length > 0;
        }),
        click(lomake.kaksoiskansalaisuus(false)),
        function() {
            expect(lomake.sukupuoli().val()).to.equal('2');
        },
        input(
            lomake.asuinmaa, 'FIN',
            lomake.lahiosoite, "Testikatu 4",
            lomake.postinumero, "00100",
            lomake.kotikunta, "janakkala"));
}

function valitseKoulutus(prioriteetti, koulunNimi, koulutuksenNimi) {
    return seq(
        autocomplete(lomake.opetuspiste(prioriteetti), koulunNimi, koulunNimi),
        select(lomake.koulutus(prioriteetti), koulutuksenNimi)
    );
}
