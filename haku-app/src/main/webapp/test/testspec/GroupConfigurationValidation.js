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

    describe("hakukohteiden rajaavuuden validointi", function() {
        before(seqDone(
            login('master', 'master'),
            setupGroupConfiguration("1.2.246.562.29.173465377510", "1.2.246.562.28.20907706742", "hakukohde_rajaava", {maximumNumberOf: 1}),
            openPage("/haku-app/lomakkeenhallinta/1.2.246.562.29.173465377510", function() {
                return S("form#form-henkilotiedot").first().is(':visible')
            })
        ));

        after(seqDone(
            login('master', 'master'),
            teardownGroupConfiguration("1.2.246.562.29.173465377510", "1.2.246.562.28.20907706742", "hakukohde_rajaava")
        ));

        beforeEach(prefill);

        var rajaavuusError = "Liian monta hakukohdetta valittu samasta ryhmästä.";

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
            visibleText(lomake.koulutusError(1), rajaavuusError),
            visibleText(lomake.koulutusError(2), rajaavuusError)
        ));

        it('rajaamaton jatkaa seuraavaan vaiheeseen', seqDone(
            syotaJarjestyksessa(raasepori),
            pageChange(lomake.fromHakutoiveet),
            headingVisible("Osaaminen")
        ));
    });

    describe("hakukohteiden prioriteetin validointi", function() {
        before(seqDone(
            login('master', 'master'),
            setupGroupConfiguration("1.2.246.562.29.173465377510", "1.2.246.562.28.20907706740", "hakukohde_priorisoiva"),
            setupGroupConfiguration("1.2.246.562.29.173465377510", "1.2.246.562.28.20907706741", "hakukohde_priorisoiva"),
            openPage("/haku-app/lomakkeenhallinta/1.2.246.562.29.173465377510", function() {
                return S("form#form-henkilotiedot").first().is(':visible')
            })
        ));

        after(seqDone(
            login('master', 'master'),
            teardownGroupConfiguration("1.2.246.562.29.173465377510", "1.2.246.562.28.20907706740", "hakukohde_priorisoiva"),
            teardownGroupConfiguration("1.2.246.562.29.173465377510", "1.2.246.562.28.20907706741", "hakukohde_priorisoiva")
        ));

        beforeEach(prefill);

        function priorityErrorTemplate(a, b) {
            return "Hakukohde " + a + " tulee olla korkeammalla prioriteetilla kuin hakukohteen " + b + ". Muuta prioriteettijärjestystä.";
        }

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
            visibleText(lomake.koulutusError(1), priorityErrorTemplate(afrikkaKoulutus, aasiaKoulutus)),
            visibleText(lomake.koulutusError(2), priorityErrorTemplate(raaseporiKoulutus, aasiaKoulutus)),
            visibleText(lomake.koulutusError(3), priorityErrorTemplate(afrikkaKoulutus, aasiaKoulutus))
        ));

        it('väärä järjestys tuottaa virheet', seqDone(
            syotaJarjestyksessa(
                afrikka,
                raasepori),
            pageChange(lomake.fromHakutoiveet),
            visibleText(lomake.koulutusError(1), priorityErrorTemplate(raaseporiKoulutus, afrikkaKoulutus)),
            visibleText(lomake.koulutusError(2), priorityErrorTemplate(raaseporiKoulutus, afrikkaKoulutus))
        ));

        it('väärän järjestyksen voi korjata alas-nuolella', seqDone(
            syotaJarjestyksessa(
                afrikka,
                raasepori),
            pageChange(lomake.fromHakutoiveet),
            visibleText(lomake.koulutusError(1), priorityErrorTemplate(raaseporiKoulutus, afrikkaKoulutus)),
            visibleText(lomake.koulutusError(2), priorityErrorTemplate(raaseporiKoulutus, afrikkaKoulutus)),
            click(lomake.nuoliAlas(1)),
            notExists(lomake.koulutusError(1)),
            notExists(lomake.koulutusError(2)),
            pageChange(lomake.fromHakutoiveet),
            headingVisible("Osaaminen")
        ));

        it('väärän järjestyksen voi korjata ylös-nuolella', seqDone(
            syotaJarjestyksessa(
                afrikka,
                raasepori),
            pageChange(lomake.fromHakutoiveet),
            visibleText(lomake.koulutusError(1), priorityErrorTemplate(raaseporiKoulutus, afrikkaKoulutus)),
            visibleText(lomake.koulutusError(2), priorityErrorTemplate(raaseporiKoulutus, afrikkaKoulutus)),
            click(lomake.nuoliYlos(2)),
            notExists(lomake.koulutusError(1)),
            notExists(lomake.koulutusError(2)),
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
            visibleText(lomake.koulutusError(1), priorityErrorTemplate(afrikkaKoulutus, ouluKoulutus)),
            visibleText(lomake.koulutusError(2), priorityErrorTemplate(raaseporiKoulutus, ouluKoulutus)),
            visibleText(lomake.koulutusError(3), priorityErrorTemplate(afrikkaKoulutus, ouluKoulutus))
        ));

        it('virheellinen toinen ryhmä', seqDone(
            syotaJarjestyksessa(
                raasepori,
                afrikka,
                aasia),
            pageChange(lomake.fromHakutoiveet),
            visibleText(lomake.koulutusError(1), priorityErrorTemplate(aasiaKoulutus, raaseporiKoulutus)),
            visibleText(lomake.koulutusError(3), priorityErrorTemplate(aasiaKoulutus, raaseporiKoulutus))
        ));

        it('virheelliset molemmat ryhmät', seqDone(
            syotaJarjestyksessa(
                afrikka,
                raasepori,
                aasia),
            pageChange(lomake.fromHakutoiveet),
            visibleText(lomake.koulutusError(1), priorityErrorTemplate(raaseporiKoulutus, afrikkaKoulutus)),
            visibleText(lomake.koulutusError(2), priorityErrorTemplate(aasiaKoulutus, raaseporiKoulutus)),
            visibleText(lomake.koulutusError(3), priorityErrorTemplate(aasiaKoulutus, raaseporiKoulutus))
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
});
