describe('KK-hakemus', function () {
    function answerForQuestion(name) {
        return S('td:has(a[name=' + name + '])').next().html()
    }
    var hakemusId
    function hakemusPath() {
        return "/haku-app/virkailija/hakemus/" + hakemusId
    }
    function previewPagePath() {
        return hakemusPath() + "/print/view"
    }

    before(seqDone(
        logout,
        installGroupConfigurations(
            ["1.2.246.562.29.173465377510", "1.2.246.562.28.00000000011", "hakukohde_liiteosoite", {
                useFirstAoAddress: "false",
                deadline: "1425912995920",
                addressRecipient: "Liiteosoitteiden vastaanottaja",
                addressStreet: "Katuosoite 12",
                addressPostalCode: "12345",
                addressPostOffice: "TESTIOSOITE"
            }],
            ["1.2.246.562.29.173465377510", "1.2.246.562.28.90373737623", "hakukohde_liiteosoite", {
                useFirstAoAddress: "true",
                deadline: "1525912995920"
            }]
        ),
        function() {
            return openPage("/haku-app/lomakkeenhallinta/1.2.246.562.29.173465377510", function() {
                return S("form#form-henkilotiedot").first().is(':visible')
            })()
        }
    ));

    describe("virkailijan näkymä", function() {
        before(seqDone(
            login('officer', 'officer'),
            openPage("/haku-app/virkailija/hakemus", function() {
                return testFrame().document.getElementById('loginForm') !== null;
            }),
            function() {
                function input(name) {
                    return testFrame().document.getElementsByName(name)[0];
                }
                input("j_username").value = "officer";
                input("j_password").value = "officer";
                input("login").click();
            },
            click(virkailija.createApplicationButton),
            input(virkailija.selectHaku, "1.2.246.562.29.173465377510"),
            click(virkailija.submitConfirm),
            exists(virkailija.hakemusOid),
            function() {
                hakemusId = virkailija.hakemusOid().text();
            }
        ));

        describe("syötä hakemus alkutilanne", function() {
            it('avautuu', function () {
            });
        });

        describe("kahden ammatillisen pohjakoulutuksen lisäys", function() {
            before(seqDone(
                click(
                    virkailija.editKoulutusTaustaButton,
                    virkailija.addAmmatillinenCheckbox),
                input(
                    virkailija.ammatillinenSuoritusVuosi, "2000",
                    virkailija.ammatillinenTutkintonimike, "Hitsaajan perustutkinto",
                    virkailija.ammatillinenTutkinnonLaajuus, "120",
                    virkailija.ammatillinenOppilaitos, "Ammattikoulu X"),
                click(
                    virkailija.kkTutkintoSuoritettu(false),
                    virkailija.addAvoinCheckbox),
                input(
                    virkailija.avoinAla(), "Avoin ala 1",
                    virkailija.avoinKokonaisuus(), "Avoin kokonaisuus 1",
                    virkailija.avoinLaajuus(), "Avoin laajuus 1",
                    virkailija.avoinKorkeakoulu(), "Avoin Korkeakoulu 1"),
                click(virkailija.addSecondAvoinLink),
                input(
                    virkailija.avoinAla(2), "Avoin ala 2",
                    virkailija.avoinKokonaisuus(2), "Avoin kokonaisuus 2",
                    virkailija.avoinLaajuus(2), "Avoin laajuus 2",
                    virkailija.avoinKorkeakoulu(2), "Avoin Korkeakoulu 2"),
                click(virkailija.saveKoulutusTaustaButton),
                visible(virkailija.editKoulutusTaustaButton)
            ));

            describe("lisäämisen jälkeen", function() {
                it("vastaukset näkyvät", function () {
                    expect(answerForQuestion('pohjakoulutus_am_vuosi')).to.equal('2000');
                    expect(answerForQuestion('pohjakoulutus_avoin_ala')).to.equal('Avoin ala 1');
                    expect(answerForQuestion('pohjakoulutus_avoin_ala2')).to.equal('Avoin ala 2');
                });
            });

            describe("hakutoiveiden lisäys", function() {

                var avoinKkTodistuskopio = 'Todistuskopio tai ote avoimen korkeakoulun opintosuorituksista';
                var amTodistuskopio = 'Todistuskopio ammatillisesta perustutkinnosta';

                describe("lisättäessä neljä toivetta, jotka eivät kuulu liiteosoiteryhmiin", function() {
                    before(seqDone(
                        click(virkailija.editHakutoiveetButton),
                        tyhjennaHakutoiveet(5),
                        jazz5v(1),
                        raasepori(2),
                        jazz2v(3),
                        pietarsaari(4),
                        click(virkailija.saveHakutoiveetButton),
                        visible(virkailija.notes)
                    ));

                    describe("lisäämisen jälkeen", function() {
                        it("toiveet näkyvät", function () {
                            expect(answerForQuestion('preference1')).to.equal(sibelusAkatemia);
                            expect(answerForQuestion('preference2')).to.equal(novia);
                            expect(answerForQuestion('preference3')).to.equal(sibelusAkatemia);
                            expect(answerForQuestion('preference4')).to.equal(novia2);
                        });
                    });

                    describe("esikatselussa", function() {
                        before(seqDone(
                            openPage(previewPagePath, function() {
                                return S("#applicationAttachments").first().is(':visible');
                            })
                        ));
                        it('pyydetään yhteensä 5 liitettä', function () {
                            expect(virkailija.previewLiitteet()).to.have.length(5);
                        });

                        it('pyydetään sibelius akatemiaan molemman kohteet hakukohdekohtaiset liitteet erikseen, mutta avoimen kk:n että ammatillisen pohjakoulutuksen liitteet vain kerran', function () {
                            expect(virkailija.previewLiitteet().has("td:eq(0):contains(" + sibelusAkatemia + ")").find("td:first:contains(" + avoinKkTodistuskopio + ")").length).to.equal(1);
                            expect(virkailija.previewLiitteet().has("td:eq(0):contains(" + sibelusAkatemia + ")").find("td:first:contains(" + amTodistuskopio + ")").length).to.equal(1);
                            expect(virkailija.previewLiitteet().has("td:eq(1):contains(Sibelius-Akatemian hakijapalvelut)").find("td:first:contains(Ennakkotehtävät)").length).to.equal(2);
                        });

                        it('pyydetään noviaan vain Raaseporin hakutoimistoon (koska samam kuin pietarsaarella) pelkkä avoimen kk:n liite (ei ammatillista, koska ei ole yliopisto)', function () {
                            expect(virkailija.previewLiitteet().has("td:eq(0):contains(" + novia + ")").find("td:first:contains(" + avoinKkTodistuskopio + ")").length).to.equal(1);
                        });
                    });
                });

                describe("lisättäessä kaksi toivetta, jotka kuuluvat eri liiteosoiteryhmiin", function() {
                    before(seqDone(
                        openPage(hakemusPath, function() {
                            return visible(virkailija.notes)();
                        }),
                        click(virkailija.editHakutoiveetButton),
                        tyhjennaHakutoiveet(5),
                        afrikka(1),
                        sosionomiJarvenpaa(2),
                        click(virkailija.saveHakutoiveetButton),
                        visible(virkailija.notes)
                    ));

                    describe("lisäämisen jälkeen", function() {
                        it("toiveet näkyvät", function () {
                            expect(answerForQuestion('preference1')).to.equal(helsinginYliopisto);
                            expect(answerForQuestion('preference2')).to.equal(jarvenpaanDiakoniaAMK);
                        });
                    });

                    describe("esikatselussa", function() {
                        before(seqDone(
                            openPage(previewPagePath, function() {
                                return S("#applicationAttachments").first().is(':visible');
                            })
                        ));
                        it('pyydetään yhteensä 3 liitettä', function () {
                            expect(virkailija.previewLiitteet()).to.have.length(3);
                        });
                        it('pyydetään yliopistoon kaksi liitettä', function () {
                            expect(virkailija.previewLiitteet().has("td:eq(1):contains(Liiteosoitteiden vastaanottaja)").find("td:first:contains(" + avoinKkTodistuskopio + ")").length).to.equal(1);
                            expect(virkailija.previewLiitteet().has("td:eq(1):contains(Liiteosoitteiden vastaanottaja)").find("td:first:contains(" + amTodistuskopio + ")").length).to.equal(1);
                        });
                        it('pyydetään AMK:hon vain yksi (ei ammattillista) kaksi liitettä', function () {
                            expect(virkailija.previewLiitteet().has("td:eq(1):contains(" + jarvenpaanDiakoniaAMK + ")").find("td:first:contains(" + avoinKkTodistuskopio + ")").length).to.equal(1);
                        });
                        it('pyydetään helsingin yliopiston liitteet liiteryhmän osoitteeseen', function () {
                            expect(virkailija.previewLiitteet().find("td:eq(1):contains(Liiteosoitteiden vastaanottaja)").length).to.equal(2);
                        });
                        it('pyydetään helsingin yliopiston liiteryhmän liitteet configuroituun pvm mennessä', function () {
                            expect(virkailija.previewLiitteet().has("td:eq(1):contains(Liiteosoitteiden vastaanottaja)").find("td:eq(2):contains(09.03.2015 16:56)").length).to.equal(2);
                        });
                        it('pyydetään diakonia AMK liiteryhmän liitteet toimipisteen osoitteeseen', function () {
                            expect(virkailija.previewLiitteet().find("td:eq(1):contains(Järvenpääntie 640)").length).to.equal(1);
                        });
                        it('pyydetään diakonia AMK liiteryhmän liitteet configuroituun pvm mennessä', function () {
                            expect(virkailija.previewLiitteet().has("td:eq(1):contains(" + jarvenpaanDiakoniaAMK + ")").find("td:eq(2):contains(10.05.2018 03:43)").length).to.equal(1);
                        });
                    });
                });

                describe("lisättäessä kolme toivetta, jotka kuuluvat samaan osoitteellisen liiteosoiteryhmään", function() {
                    before(seqDone(
                        openPage(hakemusPath, function() {
                            return visible(virkailija.notes)();
                        }),
                        click(virkailija.editHakutoiveetButton),
                        afrikka(1),
                        aasia(2),
                        oulu(3),
                        click(virkailija.saveHakutoiveetButton),
                        visible(virkailija.notes)
                    ));

                    describe("lisäämisen jälkeen", function() {
                        it("toiveet näkyvät", function () {
                            expect(answerForQuestion('preference1')).to.equal(helsinginYliopisto);
                            expect(answerForQuestion('preference2')).to.equal(helsinginYliopisto);
                            expect(answerForQuestion('preference3')).to.equal(oulunYliopisto);
                        });
                    });

                    describe("esikatselussa", function() {
                        before(seqDone(
                            openPage(previewPagePath, function() {
                                return S("#applicationAttachments").first().is(':visible');
                            })
                        ));
                        it('pyydetään sekä avoimen kk:n että ammatillisen koulutuksen todistusten liittet ainoastaan liiteryhmän osoitteeseen', function () {
                            expect(virkailija.previewLiitteet()).to.have.length(2);
                            expect(virkailija.previewLiitteet().has("td:eq(1):contains(Liiteosoitteiden vastaanottaja)").find("td:first:contains(" + avoinKkTodistuskopio + ")").length).to.equal(1);
                            expect(virkailija.previewLiitteet().has("td:eq(1):contains(Liiteosoitteiden vastaanottaja)").find("td:first:contains(" + amTodistuskopio + ")").length).to.equal(1);
                        });
                    });
                });

                describe("lisättäessä kaksi toivetta, jotka kuuluvat samaan ei osoiteelliseen liiteosoiteryhmään", function() {
                    before(seqDone(
                        openPage(hakemusPath, function() {
                            return visible(virkailija.notes)();
                        }),
                        click(virkailija.editHakutoiveetButton),
                        tyhjennaHakutoiveet(5),
                        terveydenhoitajaHelsinki(1),
                        sosionomiJarvenpaa(2),
                        click(virkailija.saveHakutoiveetButton),
                        visible(virkailija.notes)
                    ));

                    describe("lisäämisen jälkeen", function() {
                        it("toiveet näkyvät", function () {
                            expect(answerForQuestion('preference1')).to.equal(helsinginDiakoniaAMK);
                            expect(answerForQuestion('preference2')).to.equal(jarvenpaanDiakoniaAMK);
                        });
                    });

                    describe("esikatselussa", function() {
                        before(seqDone(
                            openPage(previewPagePath, function() {
                                return S("#applicationAttachments").first().is(':visible');
                            })
                        ));
                        it('pyydetään sekä avoimen kk:n todistuksen liite ainoastaan liiteryhmän ensimmäisen hakukohteen osoitteeseen', function () {
                            expect(virkailija.previewLiitteet()).to.have.length(1);
                            expect(virkailija.previewLiitteet().has("td:eq(1):contains(" + helsinginDiakoniaAMK + ")").find("td:first:contains(" + avoinKkTodistuskopio + ")").length).to.equal(1);
                            expect(virkailija.previewLiitteet().find("td:eq(1):contains(HELSINKI)").length).to.equal(1);
                        });
                    });
                });
            });
        });
    });

    describe('täyttö', function() {
        beforeEach(seqDone(
            logout,
            openPage("/haku-app/lomakkeenhallinta/1.2.246.562.29.95390561488", function() {
                return S("form#form-henkilotiedot").first().is(':visible')
            })));

        it('tukee useampaa ammatillista peruskoulutusta', seqDone(
            partials.henkilotiedotTestikaes,
            input(lomake.koulusivistyskieli, "FI"),
            click(lomake.fromHenkilotiedot),
            headingVisible("Koulutustausta"),
            click(lomake.pohjakoulutusYo),
            input(lomake.pohjakoulutusYoVuosi, "2000"),
            click(lomake.pohjakoulutusYoAmmatillinen),
            input(
                lomake.pohjakoulutusYoAmmatillinenVuosi, "2000",
                lomake.pohjakoulutusYoAmmatillinenNimike, "Nimike oli",
                lomake.pohjakoulutusYoAmmatillinenLaajuus, "1000"),
            click(
                lomake.pohjakoulutusAm,
                lomake.lisaaUusiAmmatillinenPohjakoulutus(2)),
            partials.syotaAmmatillinenPohjakoulutus(1, "2000", "Ammatillinen 1", "1000", "Joku koulu se oli", false),
            partials.syotaAmmatillinenPohjakoulutus(2, "2001", "Ammatillinen 2", "1000", "Joku toinen koulu se oli", false),
            click(
                lomake.suoritusoikeusTaiAiempiTutkinto(false),
                lomake.fromKoulutustausta),
            partials.valitseKoulutus(1, "Metropolia AMK, Espoo, Vanha maantie (Leppävaara)", "Insinööri (AMK), maanmittaustekniikka, päivätoteutus"),
            click(lomake.fromHakutoiveet),
            input(lomake.lukioPaattotodistusKeskiarvo, "1,00"),
            partials.syotaAmmatillinenKeskiarvo('_yo_ammatillinen', '2,00', "Lukio + ammatillinen"),
            partials.syotaAmmatillinenKeskiarvo(1, '2,50', "Ammatillinen 1 keskiarvo"),
            partials.syotaAmmatillinenKeskiarvo(2, '3,00', "Ammatillinen 2 keskiarvo"),
            click(
                lomake.fromOsaaminen,
                lomake.asiointikieli("suomi")),
            pageChange(lomake.fromLisatieto),
            function() {
                var expected = [
                    ["Lukion päättötodistuksen keskiarvo", "1,00"],
                    ["Ammatillisen tutkinnon keskiarvo", "2,00"],
                    ["Ammatillisen tutkinnon arvosana-asteikko", "1-3"],
                    [
                        "Jos olet suorittanut useampia ammatillisia tutkintoja, kirjoita tähän, mitä antamasi keskiarvo koskee.",
                        "Lukio + ammatillinen"
                    ],
                    ["Ammatillisen tutkinnon keskiarvo", "2,50"],
                    ["Ammatillisen tutkinnon arvosana-asteikko", "1-3"],
                    [
                        "Jos olet suorittanut useampia ammatillisia tutkintoja, kirjoita tähän, mitä antamasi keskiarvo koskee.",
                        "Ammatillinen 1 keskiarvo"
                    ],
                    ["Ammatillisen tutkinnon keskiarvo", "3,00"],
                    ["Ammatillisen tutkinnon arvosana-asteikko", "1-3"],
                    [
                        "Jos olet suorittanut useampia ammatillisia tutkintoja, kirjoita tähän, mitä antamasi keskiarvo koskee.",
                        "Ammatillinen 2 keskiarvo"
                    ]
                ];
                expect(readTable(S('#osaaminenteema'))).to.deep.equal(expected);
            }
        ));
    })
});
