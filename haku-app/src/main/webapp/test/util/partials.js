var oulunYliopisto = "Oulun yliopisto, Humanistinen tiedekunta";
var helsinginYliopisto = "Helsingin yliopisto, Humanistinen tiedekunta";
var novia = "Yrkeshögskolan Novia, Raasepori";
var novia2 = "Yrkeshögskolan Novia, Pietarsaari";
var jarvenpaanDiakoniaAMK = "Diakonia-ammattikorkeakoulu, Järvenpään toimipiste";
var helsinginDiakoniaAMK = "Diakonia-ammattikorkeakoulu, Helsingin toimipiste";
var sibelusAkatemia = "Taideyliopisto,  Sibelius-Akatemia";

var afrikkaKoulutus = "Afrikan ja Lähi-idän tutkimus, humanististen tieteiden kandidaatti ja filosofian maisteri";
var aasiaKoulutus = "Aasian tutkimus, humanististen tieteiden kandidaatti ja filosofian maisteri";
var raaseporiKoulutus = "Agrolog (YH)/Miljöplanerare (YH)/Skogsbruksingenjör (YH), dagstudier";
var ouluKoulutus = "Aate- ja oppihistoria, humanististen tieteiden kandidaatti ja filosofian maisteri";

function raasepori(n) {
    return valitseKoulutus(n, novia, raaseporiKoulutus);
}

function pietarsaari(n) {
    return valitseKoulutus(n, novia2, "Master of Culture and Arts, Entrepreneurship in Arts, part-time studies");
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

function sosionomiJarvenpaa(n) {
    return valitseKoulutus(n, jarvenpaanDiakoniaAMK, "Sosionomi (AMK), monimuotototeutus")
}

function terveydenhoitajaHelsinki(n) {
    return valitseKoulutus(n, helsinginDiakoniaAMK, "Terveydenhoitaja (AMK), monimuotototeutus")
}

function jazz2v(n) {
    return valitseKoulutus(n, sibelusAkatemia, "Jazzmusiikki, sävellys 2,5-vuotinen koulutus")
}

function jazz5v(n) {
    return valitseKoulutus(n, sibelusAkatemia, "Jazzmusiikki, sävellys 5,5-vuotinen koulutus")
}

function tyhjennaHakutoiveet(count) {
    var tyhjennaHakutoiveArray = [];
    for(var i = 1; i <= count; i++) {
        tyhjennaHakutoiveArray.push(tyhjennaHakutoive(i))
    }
    return seq.apply(this, tyhjennaHakutoiveArray);
}

function tyhjennaHakutoive(prioriteetti) {
    return seq(
        visible(lomake.opetuspiste(prioriteetti)),
        wait.until(function() {
            if(lomake.opetuspiste(prioriteetti)().val() === "") {
                return true;
            }
            return lomake.koulutus(prioriteetti)().children().length > 1;
        }),
        click(lomake.tyhjenna(prioriteetti)),
        wait.until(function() {
            return lomake.opetuspiste(prioriteetti)().val() === "" ;
        })
    );
}

function valitseKoulutus(prioriteetti, koulunNimi, koulutuksenNimi) {
    return seq(
        tyhjennaHakutoive(prioriteetti),
        autocomplete(lomake.opetuspiste(prioriteetti), koulunNimi, koulunNimi),
        select(lomake.koulutus(prioriteetti), koulutuksenNimi)
    );
}

partials = {
    henkilotiedotTestikaes: seq(
        headingVisible("Henkilötiedot"),
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
            lomake.kotikunta, "janakkala")),
    valitseKoulutus: function(prioriteetti, koulunNimi, koulutuksenNimi) {
        return seq(
            autocomplete(lomake.opetuspiste(prioriteetti), koulunNimi, koulunNimi),
            select(lomake.koulutus(prioriteetti), koulutuksenNimi)
        );
    },
    syotaAmmatillinenPohjakoulutus: function(n, vuosi, nimike, laajuus, oppilaitos, nayttotutkintona) {
        return seq(
            input(
                lomake.pohjakoulutusAmVuosi(n), vuosi),
            select(
                lomake.pohjakoulutusAmNimike(n), nimike),
            input(
                lomake.pohjakoulutusAmLaajuus(n), laajuus),
            select(
                lomake.pohjakoulutusAmOppilaitos(n), oppilaitos),
            click(
                lomake.pohjakoulutusAmNayttotutkintona(n, nayttotutkintona)
            ));
    },
    syotaAmmatillinenKeskiarvo: function(suffix, keskiarvo, tutkinto) {
        return seq(
            input(
                lomake.keskiarvo(suffix), keskiarvo,
                lomake.keskiarvoTutkinto(suffix), tutkinto));
    }
};
