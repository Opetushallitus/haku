var oulunYliopisto = "Oulun yliopisto, Humanistinen tiedekunta";
var helsinginYliopisto = "Helsingin yliopisto, Humanistinen tiedekunta";
var novia = "Yrkeshögskolan Novia, Raasepori";
var novia2 = "Yrkeshögskolan Novia, Pietarsaari";
var jarvenpaanDiakoniaAMK = "Diakonia-ammattikorkeakoulu, Järvenpään toimipiste";
var helsinginDiakoniaAMK = "Diakonia-ammattikorkeakoulu, Helsingin toimipiste";
var sibelusAkatemia = "Taideyliopisto,  Sibelius-Akatemia";
var oulunYliopisto = "Oulun yliopisto, Humanistinen tiedekunta";
var faktia = "FAKTIA, Espoo op";
var kiipula = "Kiipulan ammattiopisto, Kiipulan toimipaikka";
var aalto = "Aalto-yliopisto, Insinööritieteiden korkeakoulu";

var faktiaPkKoulutus = "Talonrakennus ja ymäristösuunnittelu, pk";
var kiipulaErKoulutus = "Metsäalan perustutkinto, er";

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

function aaltoTekniikanKandiJaDi(n) {
    return valitseKoulutus(n, aalto, "Rakennettu ympäristö, tekniikan kandidaatti ja diplomi-insinööri, DIA-yhteisvalinta")
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
            lomake.Sähköposti, "foo-" + getRandomInt(1, 999999) + "@example.com",
            lomake.asuinmaa, 'FIN',
            lomake.lahiosoite, "Testikatu 4",
            lomake.postinumero, "00100",
            lomake.kotikunta, "janakkala")),
    valitseKoulutusVetovalikosta: function(prioriteetti, koulunNimi, koulutuksenNimi) {
        return seq(
            select(lomake.opetuspisteDropdown(prioriteetti), koulunNimi),
            select(lomake.koulutus(prioriteetti), koulutuksenNimi)
        );
    },
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
    syotaUlkomainenYoPohjakoulutus: function(vuosi, tutkinto, maa, muumaa) {
        var common = seq(
            input(lomake.pohjakoulutusYoUlkomainenVuosi, vuosi),
            select(lomake.pohjakoulutusYoUlkomainenTutkinto, tutkinto),
            select(lomake.pohjakoulutusYoUlkomainenMaa, maa));
        if (maa === 'XXX') {
            return seq(common, input(lomake.pohjakoulutusYoUlkomainenMaaMuu, muumaa));
        }
        return common;
    },
    syotaUlkomainenKKPohjakoulutus: function(n, taso, pvm, tutkinto, korkeakoulu, maa, muumaa) {
        var common = seq(
            select(lomake.pohjakoulutusKKUlkTaso(n), taso),
            input(lomake.pohjakoulutusKKUlkPvm(n), pvm),
            input(lomake.pohjakoulutusKKUlkTutkinto(n), tutkinto),
            input(lomake.pohjakoulutusKKUlkOppilaitos(n), korkeakoulu),
            select(lomake.pohjakoulutusKKUlkMaa(n), maa));
        if (maa === 'XXX') {
            return seq(common, input(lomake.pohjakoulutusKKUlkMaaMuu(n), muumaa));
        }
        return common;
    },
    syotaUlkomainenPohjakoulutus: function(n, vuosi, tutkinto, oppilaitos, maa, muumaa) {
        var common = seq(
            input(lomake.pohjakoulutusUlkVuosi(n), vuosi),
            input(lomake.pohjakoulutusUlkTutkinto(n), tutkinto),
            input(lomake.pohjakoulutusUlkOppilaitos(n), oppilaitos),
            select(lomake.pohjakoulutusUlkSuoritusmaa(n), maa));
        if (maa === 'XXX') {
            return seq(common, input(lomake.pohjakoulutusUlkSuoritusmaaMuu(n), muumaa));
        }
        return common;
    },
    syotaAmmatillinenKeskiarvo: function(suffix, keskiarvo, asteikko, tutkinto) {
        return seq(
            select(lomake.asteikko(suffix), asteikko),
            input(
                lomake.keskiarvo(suffix), keskiarvo,
                lomake.keskiarvoTutkinto(suffix), tutkinto));
    }
};
