describe('GroupConfiguration', function () {
    var prefill = seqDone(
        logout,
        openPage("/haku-app/lomake/1.2.246.562.29.173465377510/henkilotiedot", function() {
            return S("form#form-henkilotiedot").first().is(':visible')
        }),
        henkilotiedotTestikaes(),
        input(lomake.koulusivistyskieli, "FI"),
        click(
            lomake.fromHenkilotiedot,
            lomake.pohjakoulutusMuu
        ),
        input(
            lomake.pohjakoulutusMuuVuosi, "2014",
            lomake.pohjakoulutusMuuKuvaus, "FOOBAR"
        ),
        click(
            lomake.suoritusoikeusTaiAiempiTutkinto(false),
            lomake.fromKoulutustausta
        )
    );

    var afrikkaKoulutus = "Afrikan ja Lähi-idän tutkimus, humanististen tieteiden kandidaatti ja filosofian maisteri";
    var aasiaKoulutus = "Aasian tutkimus, humanististen tieteiden kandidaatti ja filosofian maisteri";
    var raaseporiKoulutus = "Agrolog (YH)/Miljöplanerare (YH)/Skogsbruksingenjör (YH), dagstudier";
    var ouluKoulutus = "Aate- ja oppihistoria, humanististen tieteiden kandidaatti ja filosofian maisteri";

    function raasepori(n) {
        return valitseKoulutus(n, "Yrkeshögskolan Novia, Raasepori", raaseporiKoulutus);
    }

    function afrikka(n) {
        return valitseKoulutus(n, "Helsingin yliopisto, Humanistinen tiedekunta", afrikkaKoulutus);
    }

    function aasia(n) {
        return valitseKoulutus(n, "Helsingin yliopisto, Humanistinen tiedekunta", aasiaKoulutus);
    }

    function oulu(n) {
        return valitseKoulutus(n, "Oulun yliopisto, Humanistinen tiedekunta", ouluKoulutus)
    }

    function syotaJarjestyksessa() {
        return seq.apply(this, Array.prototype.slice.call(arguments).map(function(f, i) {
            return f(i + 1);
        }));
    }

    function tarkistaVirheetJarjestyksessa() {
        return seq.apply(this, Array.prototype.slice.call(arguments).map(function(e, i) {
            if (e === null) {
                return notExists(lomake.koulutusError(i + 1));
            }
            return visibleText(lomake.koulutusError(i + 1),  e)
        }));
    }

    var rajaavuusError = "Liian monta hakukohdetta valittu samasta ryhmästä.";

    function priorityErrorTemplate(a, b) {
        return "Hakukohde " + a + " tulee olla korkeammalla prioriteetilla kuin hakukohteen " + b + ". Muuta hakukohteiden ensisijaisuusjärjestystä.";
    }

    function installGroupConfigurations(configs) {
        return seq(
            login('master', 'master'),
            seq.apply(this, Array.prototype.slice.call(arguments).map(function(configParams) {
                return setupGroupConfiguration.apply(this, configParams);
            })),
            openPage("/haku-app/lomakkeenhallinta/1.2.246.562.29.173465377510", function() {
                return S("form#form-henkilotiedot").first().is(':visible')
            })
        )
    }

    describe("hakukohteiden rajaavuuden validointi", function() {
        before(seqDone(installGroupConfigurations(
            ["1.2.246.562.29.173465377510", "1.2.246.562.28.20907706742", "hakukohde_rajaava", {maximumNumberOf: 1}]
        )));

        after(seqDone(
            login('master', 'master'),
            teardownGroupConfiguration("1.2.246.562.29.173465377510", "1.2.246.562.28.20907706742", "hakukohde_rajaava")
        ));

        beforeEach(prefill);

        // RAJAAVUUS, max 1: aasia, afrikka
        it('tasamäärä jatkaa seuraavaan vaiheeseen', seqDone(
            syotaJarjestyksessa(
                aasia,
                raasepori),
            pageChange(lomake.fromHakutoiveet),
            headingVisible("Osaaminen")
        ));

        it('yli maksimimäärä tuottaa virheen', seqDone(
            syotaJarjestyksessa(
                aasia,
                afrikka),
            pageChange(lomake.fromHakutoiveet),
            tarkistaVirheetJarjestyksessa(
                rajaavuusError,
                rajaavuusError)
        ));

        it('maksimimäärän ylittävän kohteen poisto poistaa virheen', seqDone(
            syotaJarjestyksessa(
                aasia,
                afrikka),
            pageChange(lomake.fromHakutoiveet),
            tarkistaVirheetJarjestyksessa(
                rajaavuusError,
                rajaavuusError),
            click(lomake.tyhjenna(2)),
            pageChange(lomake.fromHakutoiveet),
            headingVisible("Osaaminen")
        ));

        it('rajaamaton jatkaa seuraavaan vaiheeseen', seqDone(
            syotaJarjestyksessa(raasepori),
            pageChange(lomake.fromHakutoiveet),
            headingVisible("Osaaminen")
        ));
    });

    describe("hakukohteiden lomittaisen rajaavuuden validointi", function() {
        before(seqDone(installGroupConfigurations(
            ["1.2.246.562.29.173465377510", "1.2.246.562.28.00000000001", "hakukohde_rajaava", {maximumNumberOf: 1}],
            ["1.2.246.562.29.173465377510", "1.2.246.562.28.00000000002", "hakukohde_rajaava", {maximumNumberOf: 1}],
            ["1.2.246.562.29.173465377510", "1.2.246.562.28.00000000003", "hakukohde_rajaava", {maximumNumberOf: 1}]
        )));

        after(seqDone(
            login('master', 'master'),
            teardownGroupConfiguration("1.2.246.562.29.173465377510", "1.2.246.562.28.00000000001", "hakukohde_rajaava"),
            teardownGroupConfiguration("1.2.246.562.29.173465377510", "1.2.246.562.28.00000000002", "hakukohde_rajaava"),
            teardownGroupConfiguration("1.2.246.562.29.173465377510", "1.2.246.562.28.00000000003", "hakukohde_rajaava")
        ));

        beforeEach(prefill);

        // RAJAAVUUS *01, max 1: aasia,     afrikka
        // RAJAAVUUS *02, max 1: raasepori, afrikka
        // RAJAAVUUS *03, max 1: raasepori, oulu
        it('tasamäärä ei-lomittaisista ryhmistä jatkaa seuraavaan vaiheeseen', seqDone(
            syotaJarjestyksessa(
                aasia,
                raasepori),
            pageChange(lomake.fromHakutoiveet),
            headingVisible("Osaaminen")
        ));

        it('yli maksimimäärä lomittaisesta ryhmästä tuottaa virheen kaikkiin', seqDone(
            syotaJarjestyksessa(
                aasia,
                afrikka,
                raasepori),
            pageChange(lomake.fromHakutoiveet),
            tarkistaVirheetJarjestyksessa(
                rajaavuusError,
                rajaavuusError,
                rajaavuusError)
        ));

        it('yli maksimimäärä yhdestä ryhmästä tuottaa virheen vain ryhmäläisille', seqDone(
            syotaJarjestyksessa(
                aasia,
                afrikka,
                oulu),
            pageChange(lomake.fromHakutoiveet),
            tarkistaVirheetJarjestyksessa(
                rajaavuusError,
                rajaavuusError)
        ));
    });

    describe("hakukohteiden prioriteetin validointi", function() {
        before(seqDone(installGroupConfigurations(
            ["1.2.246.562.29.173465377510", "1.2.246.562.28.20907706740", "hakukohde_priorisoiva"],
            ["1.2.246.562.29.173465377510", "1.2.246.562.28.20907706741", "hakukohde_priorisoiva"]
        )));

        after(seqDone(
            login('master', 'master'),
            teardownGroupConfiguration("1.2.246.562.29.173465377510", "1.2.246.562.28.20907706740", "hakukohde_priorisoiva"),
            teardownGroupConfiguration("1.2.246.562.29.173465377510", "1.2.246.562.28.20907706741", "hakukohde_priorisoiva")
        ));

        beforeEach(prefill);

        it('oikea järjestys jatkaa seuraavaan vaiheeseen', seqDone(
            syotaJarjestyksessa(
                raasepori,
                afrikka),
            pageChange(lomake.fromHakutoiveet),
            headingVisible("Osaaminen")
        ));

        it('priorisoimattoman pitää olla priorisoitujen jälkeen', seqDone(
            syotaJarjestyksessa(
                aasia,
                raasepori,
                afrikka),
            pageChange(lomake.fromHakutoiveet),
            tarkistaVirheetJarjestyksessa(
                priorityErrorTemplate(afrikkaKoulutus, aasiaKoulutus),
                priorityErrorTemplate(raaseporiKoulutus, aasiaKoulutus),
                priorityErrorTemplate(afrikkaKoulutus, aasiaKoulutus))
        ));

        it('väärä järjestys tuottaa virheet', seqDone(
            syotaJarjestyksessa(
                afrikka,
                raasepori),
            pageChange(lomake.fromHakutoiveet),
            tarkistaVirheetJarjestyksessa(
                priorityErrorTemplate(raaseporiKoulutus, afrikkaKoulutus),
                priorityErrorTemplate(raaseporiKoulutus, afrikkaKoulutus))
        ));

        it('väärän järjestyksen voi korjata alas-nuolella', seqDone(
            syotaJarjestyksessa(
                afrikka,
                raasepori),
            pageChange(lomake.fromHakutoiveet),
            tarkistaVirheetJarjestyksessa(
                priorityErrorTemplate(raaseporiKoulutus, afrikkaKoulutus),
                priorityErrorTemplate(raaseporiKoulutus, afrikkaKoulutus)),
            click(lomake.nuoliAlas(1)),
            tarkistaVirheetJarjestyksessa(
                null,
                null),
            pageChange(lomake.fromHakutoiveet),
            headingVisible("Osaaminen")
        ));

        it('väärän järjestyksen voi korjata ylös-nuolella', seqDone(
            syotaJarjestyksessa(
                afrikka,
                raasepori),
            pageChange(lomake.fromHakutoiveet),
            tarkistaVirheetJarjestyksessa(
                priorityErrorTemplate(raaseporiKoulutus, afrikkaKoulutus),
                priorityErrorTemplate(raaseporiKoulutus, afrikkaKoulutus)),
            click(lomake.nuoliYlos(2)),
            tarkistaVirheetJarjestyksessa(
                null,
                null),
            pageChange(lomake.fromHakutoiveet),
            headingVisible("Osaaminen")
        ));

        // PRIORITEETIT  *40   *41
        // oulu          null  2
        // raasepori     1     null
        // aasia         null  1
        // afrikka       2     ----
        it('virheellinen ensimmäinen ryhmä', seqDone(
            syotaJarjestyksessa(
                oulu,
                raasepori,
                afrikka),
            pageChange(lomake.fromHakutoiveet),
            tarkistaVirheetJarjestyksessa(
                priorityErrorTemplate(afrikkaKoulutus, ouluKoulutus),
                priorityErrorTemplate(raaseporiKoulutus, ouluKoulutus),
                priorityErrorTemplate(afrikkaKoulutus, ouluKoulutus))
        ));

        it('virheellinen toinen ryhmä', seqDone(
            syotaJarjestyksessa(
                raasepori,
                afrikka,
                aasia),
            pageChange(lomake.fromHakutoiveet),
            tarkistaVirheetJarjestyksessa(
                priorityErrorTemplate(aasiaKoulutus, raaseporiKoulutus),
                null,
                priorityErrorTemplate(aasiaKoulutus, raaseporiKoulutus))
        ));

        it('virheelliset molemmat ryhmät', seqDone(
            syotaJarjestyksessa(
                afrikka,
                raasepori,
                aasia),
            pageChange(lomake.fromHakutoiveet),
            tarkistaVirheetJarjestyksessa(
                priorityErrorTemplate(raaseporiKoulutus, afrikkaKoulutus),
                priorityErrorTemplate(aasiaKoulutus, raaseporiKoulutus),
                priorityErrorTemplate(aasiaKoulutus, raaseporiKoulutus))
        ));

        it('oikeat molemmat ryhmät', seqDone(
            syotaJarjestyksessa(
                afrikka,
                aasia,
                oulu),
            pageChange(lomake.fromHakutoiveet),
            headingVisible("Osaaminen")
        ));
    });

    describe("hakukohteiden rajaavuuden ja priorisoinnin validointi", function() {
        before(seqDone(installGroupConfigurations(
            ["1.2.246.562.29.173465377510", "1.2.246.562.28.20907706742", "hakukohde_rajaava", {maximumNumberOf: 1}],
            ["1.2.246.562.29.173465377510", "1.2.246.562.28.20907706740", "hakukohde_priorisoiva"]
        )));

        after(seqDone(
            login('master', 'master'),
            teardownGroupConfiguration("1.2.246.562.29.173465377510", "1.2.246.562.28.20907706742", "hakukohde_rajaava"),
            teardownGroupConfiguration("1.2.246.562.29.173465377510", "1.2.246.562.28.20907706740", "hakukohde_priorisoiva")
        ));

        beforeEach(prefill);

        // PRIORITEETIT  *40
        // raasepori     1
        // afrikka       2
        // oulu          null
        // aasia         null
        // RAJAAVUUS, max 1: aasia, afrikka
        it('virheelliset rajaus ja priorisointi', seqDone(
            syotaJarjestyksessa(
                afrikka,
                raasepori,
                aasia),
            pageChange(lomake.fromHakutoiveet),
            tarkistaVirheetJarjestyksessa(
                priorityErrorTemplate(raaseporiKoulutus, afrikkaKoulutus),
                priorityErrorTemplate(raaseporiKoulutus, afrikkaKoulutus),
                rajaavuusError)
        ));
        it('virheellinen rajaus', seqDone(
            syotaJarjestyksessa(
                raasepori,
                afrikka,
                aasia),
            pageChange(lomake.fromHakutoiveet),
            tarkistaVirheetJarjestyksessa(
                null,
                rajaavuusError,
                rajaavuusError)
        ));

        it('virheellinen priorisointi', seqDone(
            syotaJarjestyksessa(
                afrikka,
                raasepori,
                oulu),
            pageChange(lomake.fromHakutoiveet),
            tarkistaVirheetJarjestyksessa(
                priorityErrorTemplate(raaseporiKoulutus, afrikkaKoulutus),
                priorityErrorTemplate(raaseporiKoulutus, afrikkaKoulutus),
                null)
        ));

        it('oikea rajaus ja priorisointi', seqDone(
            syotaJarjestyksessa(
                raasepori,
                aasia),
            pageChange(lomake.fromHakutoiveet),
            headingVisible("Osaaminen")
        ));
    });
});
