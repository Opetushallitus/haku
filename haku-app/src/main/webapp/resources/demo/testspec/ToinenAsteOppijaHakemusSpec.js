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

        function input(fn, value) {
            return visible(fn)().then(function() {
                return fn().val(value).change().blur();
            })
        }

        function select(fn, value) {
            return function() {
                return visible(fn)()
                    .then(wait.until(function() {
                        var matches = fn().find('option[value="' + value + '"]').length;
                        if (matches > 1) {
                            throw new Error('Value "' + value + '" matches ' + matches + ' <option>s from <select> ' + fn().selector)
                        }
                        return matches === 1;
                    })).then(function() { input(fn, value) })
            }
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
                var clickSequence = Object.keys(fns).map(function(i) {
                    return function() {
                        var fn = fns[i];
                        return visible(fn)().then(function() {
                            fn().click();
                        })
                    }
                });
                return clickSequence.reduce(Q.when, Q());
            }
        }

        function visibleText(fn, text) {
            return wait.until(function() {
                return fn().is(':visible') && fn().text().trim().indexOf(text) !== -1;
            })
        }

        function hasClass(fn, className) {
            return visible(fn)().then(function() {
                return fn().hasClass(className);
            });
        }

        function autocomplete(fn, input, text) {
            var pickFn = lomake.autocomplete(text);
            return function() {
                return Q.fcall(function() {
                    fn().val(input);
                    fn().trigger("keydown");
                }).then(visible(pickFn)).then(function() {
                    return pickFn().mouseover()
                }).then(function() {
                    return Q.delay(100)
                }).then(hasClass(pickFn, 'ui-state-hover')).then(click(pickFn))
            };
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

        function fromHakutoiveetToOsaaminen() {
            osaaminenPage.fromHakutoiveet().click();
            return Q.fcall(headingVisible("Arvosanat"));
        };

        describe("Täytä lomake", function(done) {
            beforeEach(function(done) {
                start()
                    .then(function() {
                        return Q.all([
                            input(lomake.sukunimi, "Testikäs"),
                            input(lomake.etunimet, "Asia Kas"),
                            input(lomake.kutsumanimi, "Asia"),
                            input(lomake.hetu, "171175-830Y")
                        ])
                    })
                    .then(wait.until(function() {return S("input#sukupuoli").length > 0;}))
                    .then(click(lomake.kaksoiskansalaisuus(false)))
                    .then(function() {
                        expect(lomake.sukupuoli().val()).to.equal('2');
                        return Q.all([
                            input(lomake.lahiosoite, "Testikatu 4"),
                            input(lomake.postinumero, "00100"),
                            input(lomake.kotikunta, "janakkala")
                        ])
                    })
                    .then(click(lomake.fromHenkilotiedot))
                    .then(headingVisible("Koulutustausta"))
                    .then(click(lomake.pohjakoulutus("1")))
                    .then(function() {
                        return Q.all([
                            input(lomake.pkPaattotodistusVuosi, "2014"),
                            input(lomake.pkKieli, "FI")
                        ]);
                    })
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
                    .then(click(lomake.fromKoulutustausta))
                    .then(headingVisible("Koulutustausta"))
                    .then(done, done);
            });
        });

        describe("Sääntötestit", function(done) {

            beforeEach(function(done) {
                start()
                    .then(visible(lomake.sukunimi))
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
        });

        describe("Urheilijakohteet", function(done) {

            before(function(done) {
                start()
                    .then(visible(lomake.sukunimi))
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
                            "perusopetuksen_kieli": "FI"
                        }, function() {done()}));
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
