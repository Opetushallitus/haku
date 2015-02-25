describe('GroupConfiguration', function () {
    before(seqDone(
        login('master', 'master'),
        setupGroupConfiguration("1.2.246.562.29.173465377510", "1.2.246.562.28.20907706740", "hakukohde_priorisoiva"),
        setupGroupConfiguration("1.2.246.562.29.173465377510", "1.2.246.562.28.20907706741", "hakukohde_priorisoiva"),
        openPage("/haku-app/lomakkeenhallinta/1.2.246.562.29.173465377510", function() {
            return S("form#form-henkilotiedot").first().is(':visible')
        })
    ));

    describe("hakukohteiden prioriteetin validointi", function() {
        beforeEach(seqDone(
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
        ));

        function priorityErrorTemplate(a, b) {
            return "Hakukohde " + a + " tulee olla korkeammalla prioriteetilla kuin hakukohteen " + b + ". Muuta prioriteettijärjestystä.";
        }

        var afrikkaKoulutus = "Afrikan ja Lähi-idän tutkimus, humanististen tieteiden kandidaatti ja filosofian maisteri";
        var aasiaKoulutus = "Aasian tutkimus, humanististen tieteiden kandidaatti ja filosofian maisteri";
        var raaseporiKoulutus = "Agrolog (YH)/Miljöplanerare (YH)/Skogsbruksingenjör (YH), dagstudier";
        var ouluKoulutus = "Aate- ja oppihistoria, humanististen tieteiden kandidaatti ja filosofian maisteri";
        var expectedError = priorityErrorTemplate(raaseporiKoulutus, afrikkaKoulutus);

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
            visibleText(lomake.koulutusError(1), expectedError),
            visibleText(lomake.koulutusError(2), expectedError)
        ));

        it('väärän järjestyksen voi korjata alas-nuolella', seqDone(
            syotaJarjestyksessa(
                afrikka,
                raasepori),
            pageChange(lomake.fromHakutoiveet),
            visibleText(lomake.koulutusError(1), expectedError),
            visibleText(lomake.koulutusError(2), expectedError),
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
            visibleText(lomake.koulutusError(1), expectedError),
            visibleText(lomake.koulutusError(2), expectedError),
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
