(function () {

    describe('2. asteen lomake', function () {
        var henkilotietoPage = ToinenAsteLomakeHenkilotietoPage();
        var koulutustaustaPage = ToinenAsteLomakeKoulutustaustaPage();
        var hakutoiveetPage = ToinenAsteLomakeHakutoiveetPage();
        var osaaminenPage = ToinenAsteLomakeOsaaminenPage();
        var lisatietoPage = ToinenAsteLomakeLisatietoPage();
        var esikatseluPage = ToinenAsteLomakeEsikatseluPage();

        function input(fn, value) {
            return visible(fn)().then(function() {
                return fn().val(value).change().blur();
            })
        }

        function readTable($tableElement, allowWrongDimensions) {
            return $tableElement.find('tr').filter(function(i) {
                    return allowWrongDimensions || testFrame().jQuery("td", this).length === 2
                }).toArray().reduce(function(agg, tr) {
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
            return wait.until(function() {
                return fn().is(':visible');
            })
        }

        function click() {
            var fns = arguments;
            return function() {
                return Q.all(
                    Object.keys(fns).map(function(i) {
                        var fn = fns[i];
                        return visible(fn)().then(function() {
                            fn().click();
                        })
                    })
                );
            }
        }

        function visibleText(fn, text) {
            return wait.until(function() {
                return fn().is(':visible') && fn().text().trim().indexOf(text) !== -1;
            })
        }

        function headingVisible(heading) {
            return visible(function() {
                return S("legend[class=h3]:contains(" + heading + ")");
            });
        }

        describe("Täytä lomake", function(done) {
            before(function(done) {
                henkilotietoPage.start()
                    .then(visible(henkilotietoPage.sukunimi))
                    .then(function() {
                        return Q.all([
                            input(henkilotietoPage.sukunimi, "Testikäs"),
                            input(henkilotietoPage.etunimet, "Asia Kas"),
                            input(henkilotietoPage.kutsumanimi, "Asia"),
                            input(henkilotietoPage.hetu, "171175-830Y"),
                            Q.fcall(henkilotietoPage.kaksoiskansalaisuus(false))
                        ])
                    })
                    .then(wait.until(function() {return S("input#sukupuoli").length > 0;}))
                    .then(function() {
                        expect(henkilotietoPage.sukupuoli().val()).to.equal('2');
                        return Q.all([
                            input(henkilotietoPage.lahiosoite, "Testikatu 4"),
                            input(henkilotietoPage.postinumero, "00100"),
                            input(henkilotietoPage.kotikunta, "janakkala")
                        ])
                    })
                    .then(click(koulutustaustaPage.fromHenkilotiedot))
                    .then(headingVisible("Koulutustausta"))
                    .then(click(koulutustaustaPage.pohjakoulutus("1")))
                    .then(visible(koulutustaustaPage.pkPaattotodistusVuosi))
                    .then(function() {
                        return Q.all([
                            input(koulutustaustaPage.pkPaattotodistusVuosi, "2014"),
                            input(koulutustaustaPage.pkKieli, "FI")
                        ]);
                    })
                    .then(click(hakutoiveetPage.fromKoulutustausta))
                    .then(headingVisible("Hakutoiveet"))
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
                    .then(visible(hakutoiveetPage.harkinnanvaraisuus1(false)))
                    .then(click(
                        hakutoiveetPage.harkinnanvaraisuus1(false),
                        hakutoiveetPage.soraTerveys1(false),
                        hakutoiveetPage.soraOikeudenMenetys1(false),
                        osaaminenPage.fromHakutoiveet))
                    .then(headingVisible("Arvosanat"))
                    .then(click(lisatietoPage.fromOsaaminen))
                    .then(headingVisible("Lupatiedot"))
                    .then(function() {
                        lisatietoPage.asiointikieli("suomi");
                        esikatseluPage.fromLisatieto().click();
                    })
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

            it('Peruskoulutus-ammattikoulutus-ydistelmä pyytää lukioita', function(done) {
                Q.fcall(function() { S('#nav-koulutustausta')[0].click() })
                    .then(headingVisible("Koulutustausta"))
                    .then(input(koulutustaustaPage.pkPaattotodistusVuosi, "2010"))
                    .then(visible(koulutustaustaPage.ammatillinenKoulutuspaikka(false)))
                    .then(click(
                        koulutustaustaPage.ammatillinenKoulutuspaikka(false),
                        koulutustaustaPage.ammatillinenSuoritettu(true)))
                    .then(visibleText(koulutustaustaPage.suorittanutTutkinnonRule,
                        "Et voi hakea yhteishaussa ammatilliseen koulutukseen"))
                    .then(click(hakutoiveetPage.fromKoulutustausta))
                    .then(headingVisible("Hakutoiveet"))
                    .then(done, done);
            });

            it('Lukio-ammattikoulutus-ydistelmä estää pääsyn hakutoiveisiin', function(done) {
                Q.fcall(function() { S('#nav-koulutustausta')[0].click() })
                    .then(headingVisible("Koulutustausta"))
                    .then(visible(koulutustaustaPage.pkPaattotodistusVuosi))
                    .then(click(koulutustaustaPage.pohjakoulutus("9")))
                    .then(input(koulutustaustaPage.lukioPaattotodistusVuosi, "2010"))
                    .then(click(koulutustaustaPage.ammatillinenSuoritettu(true)))
                    .then(visibleText(koulutustaustaPage.warning,
                        "Et voi hakea yhteishaussa, koska olet jo suorittanut ammatillisen perustutkinnon"
                    ))
                    .then(input(koulutustaustaPage.lukionKieli, "FI"))
                    .then(click(hakutoiveetPage.fromKoulutustausta))
                    .then(headingVisible("Koulutustausta"))
                    .then(done, done);
            });
        });

        describe("Sääntötestit", function(done) {

            beforeEach(function(done) {
                henkilotietoPage.start()
                    .then(visible(henkilotietoPage.sukunimi))
                    .then($.post("/haku-app/lomake/1.2.246.562.5.50476818906",
                        {
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
                            "preference1-Opetuspiste-id": "1.2.246.562.10.89537774706&",
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
                    .then(visible(henkilotietoPage.sukunimi))
                    .then(done, done);
            });

            it("PK päättötodistusvuosi 2011", function(done) {
                $.post("/haku-app/lomake/1.2.246.562.5.50476818906/koulutustausta/rules",
                    {
                        "PK_PAATTOTODISTUSVUOSI": "2011",
                        "POHJAKOULUTUS": "1",
                        "ruleIds[]": ["paattotodistuvuosiPkRule"]
                    }, function(data, status) {
                        expect(data).to.contain('<input type=\\"radio\\" name=\\"ammatillinenTutkintoSuoritettu\\"');
                        done();
                    });
            });

            it("PK päättötodistusvuosi 2012", function(done) {
                $.post("/haku-app/lomake/1.2.246.562.5.50476818906/koulutustausta/rules",
                    {
                        "PK_PAATTOTODISTUSVUOSI": "2012",
                        "POHJAKOULUTUS": "1",
                        "ruleIds[]": ["paattotodistuvuosiPkRule"]
                    }, function(data, status) {
                        expect(data).not.to.contain('<input type=\\"radio\\" name=\\"ammatillinenTutkintoSuoritettu\\"');
                        done();
                    });
            });

        })

    });

    function asyncPrint(s) { return function() { console.log(s) } }

})();
