(function () {
    function start() {
        return logout().then(function() {
            return openPage("/haku-app/lomakkeenhallinta/1.2.246.562.5.50476818906", function() {
                return S("form#form-henkilotiedot").first().is(':visible')
            })()
        });
    }

    describe('2. asteen lomake', function () {
        var lomake = lomakeSelectors();

        describe("Täytä lomake", function() {
            beforeEach(function(done) {
                start()
                    .then(all(
                        input(lomake.sukunimi, "Testikäs"),
                        input(lomake.etunimet, "Asia Kas"),
                        input(lomake.kutsumanimi, "Asia"),
                        input(lomake.hetu, "171175-830Y")))
                    .then(wait.until(function() {return S("input#sukupuoli").length > 0;}))
                    .then(click(lomake.kaksoiskansalaisuus(false)))
                    .then(function() { expect(lomake.sukupuoli().val()).to.equal('2'); })
                    .then(all(
                        input(lomake.lahiosoite, "Testikatu 4"),
                        input(lomake.postinumero, "00100"),
                        input(lomake.kotikunta, "janakkala")))
                    .then(click(lomake.fromHenkilotiedot))
                    .then(headingVisible("Koulutustausta"))
                    .then(click(lomake.pohjakoulutus("1")))
                    .then(all(
                        input(lomake.pkPaattotodistusVuosi, "2014"),
                        input(lomake.pkKieli, "FI")))
                    .then(click(lomake.fromKoulutustausta))
                    .then(headingVisible("Hakutoiveet"))
                    .then(autocomplete(lomake.opetuspiste1, "Esp", "FAKTIA, Espoo op"))
                    .then(select(lomake.koulutus1, "Talonrakennus ja ymäristösuunnittelu, pk"))
                    .then(click(
                        lomake.harkinnanvaraisuus1(false),
                        lomake.soraTerveys1(false),
                        lomake.soraOikeudenMenetys1(false),
                        lomake.fromHakutoiveet))
                    .then(headingVisible("Arvosanat"))
                    .then(click(lomake.fromOsaaminen))
                    .then(headingVisible("Lupatiedot"))
                    .then(click(lomake.asiointikieli("suomi")))
                    .then(click(lomake.fromLisatieto))
                    .then(headingVisible("Henkilötiedot"))
                    .then(done, done);
             });

            it('Täytä hakemus alusta loppuun', function (done) {
                var expectedHenkilotiedot = {
                    "Sukunimi": "Testikäs",
                    "Etunimet": "Asia Kas",
                    "Kutsumanimi": "Asia",
                    "Kansalaisuus": "Suomi",
                    "Onko sinulla kaksoiskansalaisuutta?": "Ei",
                    "Henkilötunnus": "171175-830Y",
                    "Sukupuoli": "Nainen",
                    "Sähköpostiosoite": "",
                    "Matkapuhelinnumero": "",
                    "Asuinmaa": "Suomi",
                    "Lähiosoite": "Testikatu 4",
                    "": "00100 Helsinki",
                    "Kotikunta": "Janakkala",
                    "Äidinkieli": "Suomi",
                    "Huoltajan nimi (jos olet alle 18-vuotias)": "",
                    "Huoltajan puhelinnumero (jos olet alle 18-vuotias)": "",
                    "Huoltajan sähköpostiosoite (jos olet alle 18-vuotias)": ""
                };
                expect(readTable(S('table#henkilotiedot_teema'), true)).to.deep.equal(expectedHenkilotiedot);

                var expectedKoulutustausta = {
                    "Valitse tutkinto, jolla haet koulutukseen:": "Perusopetuksen oppimäärä",
                    "Minä vuonna sait tai saat peruskoulun päättötodistuksen?:": "2014",
                    "Millä opetuskielellä olet suorittanut perusopetuksen?:": "Suomi"
                };
                expect(readTable(S('table#koulutustausta_teema'), false)).to.deep.equal(expectedKoulutustausta);

                var expectedArvosanat = {
                };
                expect(readTable(S('table#arvosanat_teema'), false)).to.deep.equal(expectedArvosanat);

                done();
            });

            it('Peruskoulutus-ammattikoulutus-yhdistelmä pyytää lukioita', function(done) {
                Q.fcall(function() { S('#nav-koulutustausta')[0].click() })
                    .then(headingVisible("Koulutustausta"))
                    .then(input(lomake.pkPaattotodistusVuosi, "2010"))
                    .then(click(
                        lomake.ammatillinenKoulutuspaikka(false),
                        lomake.ammatillinenSuoritettu(true)))
                    .then(visibleText(lomake.suorittanutTutkinnonRule,
                        "Et voi hakea yhteishaussa ammatilliseen koulutukseen"))
                    .then(click(lomake.fromKoulutustausta))
                    .then(headingVisible("Hakutoiveet"))
                    .then(done, done);
            });

            it('Lukio-ammattikoulutus-yhdistelmä estää pääsyn hakutoiveisiin', function(done) {
                Q.fcall(function() { S('#nav-koulutustausta')[0].click() })
                    .then(headingVisible("Koulutustausta"))
                    .then(click(lomake.pohjakoulutus("9")))
                    .then(input(lomake.lukioPaattotodistusVuosi, "2010"))
                    .then(click(lomake.ammatillinenSuoritettu(true)))
                    .then(visibleText(lomake.warning,
                        "Et voi hakea yhteishaussa, koska olet jo suorittanut ammatillisen perustutkinnon"
                    ))
                    .then(input(lomake.lukionKieli, "FI"))
                    .then(assertPageChanges(click(lomake.fromKoulutustausta)))
                    .then(headingVisible("Koulutustausta"))
                    .then(done, done);
            });
        });

        describe("Sääntötestit", function() {

            beforeEach(function(done) {
                start()
                    .then(visible(lomake.sukunimi))
                    .then(postAsForm("/haku-app/lomake/1.2.246.562.5.50476818906", {
                        "Sukunimi": "Testikäs",
                        "Etunimet": "Asia Kas",
                        "Kutsumanimi": "Asia",
                        "kansalaisuus": "FIN",
                        "onkosinullakaksoiskansallisuus": "false",
                        "Henkilotunnus": "171175-830Y",
                        "sukupuoli": "2",
                        "asuinmaa": "FIN",
                        "lahiosoite": "Testikatu 4",
                        "Postinumero": "00100",
                        "kotikunta": "janakkala",
                        "aidinkieli": "FI",
                        "POHJAKOULUTUS": "1",
                        "PK_PAATTOTODISTUSVUOSI": "2014",
                        "perusopetuksen_kieli": "FI",
                        "preferencesVisible": "5",
                        "preference1-Opetuspiste": "FAKTIA, Espoo op",
                        "preference1-Opetuspiste-id": "1.2.246.562.10.89537774706",
                        "preference1-Koulutus": "Talonrakennus ja ymäristösuunnittelu, yo",
                        "preference1-Koulutus-id": "1.2.246.562.14.673437691210",
                        "preference1-Koulutus-educationDegree": "32",
                        "preference1-Koulutus-id-lang": "FI",
                        "preference1-Koulutus-id-sora": "false",
                        "preference1-Koulutus-id-athlete": "false",
                        "preference1-Koulutus-id-kaksoistutkinto": "false",
                        "preference1-Koulutus-id-vocational": "true",
                        "preference1-Koulutus-id-attachments": "false",
                        "preference1-discretionary": "false",
                        "asiointikieli": "suomi"
                    }))
                    .then(visible(lomake.sukunimi))
                    .then(done, done);
            });

            it("PK päättötodistusvuosi 2011", function(done) {
                Q.fcall(post("/haku-app/lomake/1.2.246.562.5.50476818906/koulutustausta/rules", {
                    "PK_PAATTOTODISTUSVUOSI": "2011",
                    "POHJAKOULUTUS": "1",
                    "ruleIds[]": ["paattotodistuvuosiPkRule"]
                })).then(function(data) {
                    expect(data).to.contain('<input type=\\"radio\\" name=\\"ammatillinenTutkintoSuoritettu\\"');
                }).then(done, done);
            });

            it("PK päättötodistusvuosi 2012", function(done) {
                Q.fcall(post("/haku-app/lomake/1.2.246.562.5.50476818906/koulutustausta/rules", {
                    "PK_PAATTOTODISTUSVUOSI": "2012",
                    "POHJAKOULUTUS": "1",
                    "ruleIds[]": ["paattotodistuvuosiPkRule"]
                })).then(function(data) {
                    expect(data).not.to.contain('<input type=\\"radio\\" name=\\"ammatillinenTutkintoSuoritettu\\"');
                }).then(done, done);
            });
        });

        describe("Urheilijakohteet", function() {

            beforeEach(function(done) {
                start()
                    .then(visible(lomake.sukunimi))
                    .then(postAsForm("/haku-app/lomake/1.2.246.562.5.50476818906", {
                        "Sukunimi": "Testikäs",
                        "Etunimet": "Asia Kas",
                        "Kutsumanimi": "Asia",
                        "kansalaisuus": "FIN",
                        "onkosinullakaksoiskansallisuus": "false",
                        "Henkilotunnus": "171175-830Y",
                        "sukupuoli": "2",
                        "asuinmaa": "FIN",
                        "lahiosoite": "Testikatu 4",
                        "Postinumero": "00100",
                        "kotikunta": "janakkala",
                        "aidinkieli": "FI",
                        "POHJAKOULUTUS": "1",
                        "PK_PAATTOTODISTUSVUOSI": "2014",
                        "perusopetuksen_kieli": "FI"
                    }))
                    .then(done, done)
            });

            it("Urheilevat kokit", function(done) {
                Q.fcall(visible(lomake.sukunimi))
                    .then(click(lomake.fromHenkilotiedot))
                    .then(headingVisible("Koulutustausta"))
                    .then(click(lomake.fromKoulutustausta))
                    .then(headingVisible("Hakutoiveet"))
                    .then(autocomplete(lomake.opetuspiste1, "urh", "Urheilijoiden koulu"))
                    .then(select(lomake.koulutus1, "Urheilevien kokkien koulutus"))
                    .then(click(
                        lomake.harkinnanvaraisuus1(false),
                        lomake.urheilija1(true),
                        lomake.fromHakutoiveet))
                    .then(headingVisible("Arvosanat"))
                    .then(done, done);
            });

        });
    });

})();
