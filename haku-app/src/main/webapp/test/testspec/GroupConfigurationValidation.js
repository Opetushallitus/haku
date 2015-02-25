describe('GroupConfiguration', function () {
    before(seqDone(
        login('master', 'master'),
        setupGroupConfiguration("1.2.246.562.29.173465377510", "1.2.246.562.28.20907706740", "hakukohde_priorisoiva"),
        openPage("/haku-app/lomakkeenhallinta/1.2.246.562.29.173465377510", function() {
            return S("form#form-henkilotiedot").first().is(':visible')
        })
    ));

    describe("hakukohteiden prioriteetin validointi", function() {
        var lomake = lomakeSelectors();

        beforeEach(seqDone(
            logout,
            openPage("/haku-app/lomake/1.2.246.562.29.173465377510/henkilotiedot", function() {
                return S("form#form-henkilotiedot").first().is(':visible')
            }),
            henkilotiedotTestikaes(lomake),
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
        var expectedError = priorityErrorTemplate(raaseporiKoulutus, afrikkaKoulutus);

        function raasepori(n) {
            return seq(
                autocomplete(lomake.opetuspiste(n), "yrk", "Yrkeshögskolan Novia, Raasepori"),
                select(lomake.koulutus(n), raaseporiKoulutus)
            )
        }

        function afrikka(n) {
            return seq(
                autocomplete(lomake.opetuspiste(n), "hel", "Helsingin yliopisto, Humanistinen tiedekunta"),
                select(lomake.koulutus(n), afrikkaKoulutus)
            )
        }

        function aasia(n) {
            return seq(
                autocomplete(lomake.opetuspiste(n), "hel", "Helsingin yliopisto, Humanistinen tiedekunta"),
                select(lomake.koulutus(n), aasiaKoulutus)
            )
        }

        it('oikea järjestys jatkaa seuraavaan vaiheeseen', seqDone(
            raasepori(1),
            afrikka(2),
            pageChange(lomake.fromHakutoiveet),
            headingVisible("Osaaminen")
        ));

        it('priorisoimattoman pitää olla priorisoitujen jälkeen', seqDone(
            aasia(1),
            raasepori(2),
            afrikka(3),
            pageChange(lomake.fromHakutoiveet),
            visibleText(lomake.koulutusError(1), priorityErrorTemplate(afrikkaKoulutus, aasiaKoulutus)),
            visibleText(lomake.koulutusError(2), priorityErrorTemplate(raaseporiKoulutus, aasiaKoulutus)),
            visibleText(lomake.koulutusError(3), priorityErrorTemplate(afrikkaKoulutus, aasiaKoulutus))
        ));

        it('väärä järjestys tuottaa virheet', seqDone(
            afrikka(1),
            raasepori(2),
            pageChange(lomake.fromHakutoiveet),
            visibleText(lomake.koulutusError(1), expectedError),
            visibleText(lomake.koulutusError(2), expectedError)
        ));

        it('väärän järjestyksen voi korjata alas-nuolella', seqDone(
            afrikka(1),
            raasepori(2),
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
            afrikka(1),
            raasepori(2),
            pageChange(lomake.fromHakutoiveet),
            visibleText(lomake.koulutusError(1), expectedError),
            visibleText(lomake.koulutusError(2), expectedError),
            click(lomake.nuoliYlos(2)),
            notExists(lomake.koulutusError(1)),
            notExists(lomake.koulutusError(2)),
            pageChange(lomake.fromHakutoiveet),
            headingVisible("Osaaminen")
        ));
    });
});
