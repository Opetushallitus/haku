(function () {

    describe('2. asteen hakemus', function () {
        var henkilotietoPage = ToinenAsteLomakeHenkilotietoPage();
        var koulutustaustaPage = ToinenAsteLomakeKoulutustaustaPage();
        var hakutoiveetPage = ToinenAsteLomakeHakutoiveetPage();
        var osaaminenPage = ToinenAsteLomakeOsaaminenPage();
        var lisatietoPage = ToinenAsteLomakeLisatietoPage();
        var esikatseluPage = ToinenAsteLomakeEsikatseluPage();

        function input(fn, value) {
            fn().val(value).change().blur();
        }

        function readTable($tableElement) {
            return $tableElement.find('tr').toArray().reduce(function(agg, tr) {
                var tds = tr.getElementsByTagName('td');
                if (tds.length != 2) {
                    throw new Error("Cannot read non-2-column table into map")
                }
                var key = tds[0].textContent.trim();
                var value = tds[1].textContent.trim();
                agg[key] = value;
                return agg;
            }, {});
        }

        function visible(fn) {
            return wait.until(function() { return fn().is(':visible'); })
        }

        function headingVisible(heading) {
            return visible(function() {
                return S("legend[class=h3]:contains(" + heading + ")");
            });
        }

        function fromHenkilotiedotToKoulutustausta() {
            koulutustaustaPage.fromHenkilotiedot().click();
            return Q.fcall(headingVisible("Koulutustausta"));
        };

        function fromKoulututustaustaToHakutoiveet() {
            hakutoiveetPage.fromKoulutustausta().click();
            return Q.fcall(headingVisible("Hakutoiveet"));
        };

        describe("Täytä henkilötiedot", function(done) {
            before(function(done) {
                henkilotietoPage.start()
                    .then(visible(henkilotietoPage.sukunimi))
                    .then(function() {
                        input(henkilotietoPage.sukunimi, "Testikäs");
                        input(henkilotietoPage.etunimet, "Asia Kas");
                        input(henkilotietoPage.kutsumanimi, "Asia");
                        henkilotietoPage.kaksoiskansalaisuus(false);
                        input(henkilotietoPage.hetu, "171175-830Y");
                        input(henkilotietoPage.lahiosoite, "Testikatu 4");
                        input(henkilotietoPage.postinumero, "00100");
                        input(henkilotietoPage.kotikunta, "janakkala");
                    })
                    .then(fromHenkilotiedotToKoulutustausta)
                    .then(function() {
                        koulutustaustaPage.pohjakoulutus("1");
                    })
                    .then(visible(koulutustaustaPage.pkPaattotodistusVuosi))
                    .then(function() {
                        input(koulutustaustaPage.pkPaattotodistusVuosi, "2014");
                        input(koulutustaustaPage.pkKieli, "FI");
                    })
                    .then(fromKoulututustaustaToHakutoiveet)
                    .then(function () {
                        return Q.fcall(function() {
                            hakutoiveetPage.opetuspiste1().val("Esp");
                            hakutoiveetPage.opetuspiste1().trigger("keydown");
                        }).then(visible(hakutoiveetPage.faktia)).then(function() {
                            return hakutoiveetPage.faktia().mouseover().click();
                        }).then(wait.until(function() {
                            return hakutoiveetPage.koulutus1().find('option').length > 1;
                        }))
                    })
                    .then(function() {
                        input(hakutoiveetPage.koulutus1, "Talonrakennus ja ymäristösuunnittelu, yo");
                    })
                    .then(visible(function() { return hakutoiveetPage.harkinnanvaraisuus1(false) }))
                    .then(function() {
                        hakutoiveetPage.harkinnanvaraisuus1(false).click();
                        hakutoiveetPage.soraTerveys1(false).click();
                        hakutoiveetPage.soraOikeudenMenetys1(false).click();
                        hakutoiveetPage.soraTerveys1(false).click();
                        hakutoiveetPage.soraOikeudenMenetys1(false).click();
                        osaaminenPage.fromHakutoiveet().click();
                    })
                    .then(headingVisible("Arvosanat"))
                    .then(function() {
                        lisatietoPage.fromOsaaminen().click()
                    })
                    .then(headingVisible("Lupatiedot"))
                    .then(function() {
                        lisatietoPage.asiointikieli("suomi");
                        esikatseluPage.fromLisatieto().click();
                    })
                    .then(headingVisible("Henkilötiedot"))
                    .then(done, done);
             });

            it('mahdollistaa henkilötietovaiheen täyttämisen', function (done) {
                var expected = {
                    "Sukunimi": "Testikäs",
                    "Etunimet": "Asia Kas",
                    "Kutsumanimi": "Asia",
                    "Kansalaisuus": "Suomi",
                    "Onko sinulla kaksoiskansalaisuutta?": "Ei",
                    "Henkilötunnus": "171175-830Y",
                    "Sukupuoli": "",
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
                expect(readTable(S('table:first'))).to.deep.equal(expected);
                done();
            });
        });

//        describe("Täytä koulutustausta", function(done) {
//
//            before(function(done) {
//                wait.until(function() {return page.fromHenkilotiedot(); })()
//                    .then(wait.until(function() { return page.pohjakoulutus().is(":visible")}))
//                    .then(done);
//            });
//
//            it ('mahdollistaa koulutustausta täyttämisen', function(done) {
//                expect(S("fieldset#koulutustausta.teema h3").first().html()).to.equal("Koulutustausta");
//            });
//        });


    });

    function asyncPrint(s) { return function() { console.log(s) } }

})();
