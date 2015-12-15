describe('Erityisoppilaitosten lomake', function () {
    var hakuOid = "1.2.246.562.20.807716131410"
    before(seqDone(
        logout,
        function() {
            return openPage("/haku-app/lomakkeenhallinta/" + hakuOid, function() {
                return S("form#form-henkilotiedot").first().is(':visible')
            })()
        }
    ));

    var valitseFaktiaJaKiipula = seq(
        partials.valitseKoulutusVetovalikosta(1, faktia, faktiaPkKoulutus),
        click(
            lomake.soraTerveys(1, false),
            lomake.soraOikeudenMenetys(1, false)
        ),
        partials.valitseKoulutusVetovalikosta(2, kiipula, kiipulaErKoulutus),
        click(
            lomake.soraTerveys(2, false),
            lomake.soraOikeudenMenetys(2, false)
        )
    )

    describe("virkailijan näkymä", function() {
        var hakemusId
        function hakemusPath() {
            return "/haku-app/virkailija/hakemus/" + hakemusId
        }
        function previewPagePath() {
            return hakemusPath() + "/print/view"
        }
        before(seqDone(
            login('officer', 'officer'),
            click(virkailija.createApplicationButton),
            input(virkailija.selectHaku, hakuOid),
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


        describe("yksilöllistetyn pohjakoulutuksen lisäys", function() {
            before(seqDone(
                click(
                    virkailija.editKoulutusTaustaButton(hakuOid),
                    virkailija.addYksilollistettyCheckbox),
                input(
                    lomake.pkPaattotodistusVuosi, "2015",
                    lomake.pkKieli, "FI"
                ),
                click(virkailija.saveKoulutusTaustaButton),
                visible(virkailija.editKoulutusTaustaButton(hakuOid))
            ));

            describe("lisäämisen jälkeen", function() {
                it("vastaukset näkyvät", function () {
                    expect(answerForQuestion('PK_PAATTOTODISTUSVUOSI')).to.equal('2015');
                    expect(answerForQuestion('perusopetuksen_kieli')).to.equal('Suomi');
                });
            });

            describe("hakutoiveiden lisäys", function() {

                describe("lisättäessä kaksi hakutoivetta, joilla eri pohjatietovaatimukset", function() {
                    before(seqDone(
                        click(virkailija.editHakutoiveetButton(hakuOid)),
                        valitseFaktiaJaKiipula,
                        click(virkailija.saveHakutoiveetButton),
                        visible(virkailija.notes)
                    ));

                    describe("lisäämisen jälkeen", function() {
                        it("toiveet näkyvät", function () {
                            expect(answerForQuestion('preference1')).to.equal(faktia);
                            expect(answerForQuestion('preference2')).to.equal(kiipula);
                        });
                    });
                });
            });
        });
    });

    describe("hakemuksen tuonti oppijana", function() {
        before(seqDone(
            logout,
            postAsForm("/haku-app/lomake/" + hakuOid, {
                "preference1-Opetuspiste-id": "1.2.246.562.10.89537774706", // FAKTIA, Espoo op
                "preference1-Koulutus-id": "1.2.246.562.14.673437691210", // Talonrakennus ja ymäristösuunnittelu, pk
                "preference1_sora_terveys": "false",
                "preference1_sora_oikeudenMenetys": "false",
                "preference2-Opetuspiste-id": "1.2.246.562.10.19001332592", // Kiipulan ammattiopisto, Kiipulan toimipaikka
                "preference2-Koulutus-id": "1.2.246.562.14.2013120511174558582514", // Metsäalan perustutkinto, er
                "preference2_sora_terveys": "true",
                "preference2_sora_oikeudenMenetys": "true"

            }),
            partials.henkilotiedotTestikaes,
            pageChange(lomake.fromHenkilotiedot),
            click(lomake.pohjakoulutus("1")),
            input(
                lomake.pkPaattotodistusVuosi, ("" + new Date().getFullYear()),
                lomake.pkKieli, "FI"
            ),
            pageChange(lomake.fromKoulutustausta)
        ));

        it("sisältää ennakkotäytetyt hakukohteet", seqDone(
            function() {
                expect(lomake.opetuspisteDropdown(1)().val()).to.equal("FAKTIA, Espoo op");
                expect(lomake.koulutus(1)().val()).to.equal("Talonrakennus ja ymäristösuunnittelu, pk");
                expect(lomake.soraTerveys(1, false)().is(":checked"), true);
                expect(lomake.soraOikeudenMenetys(1, false)().is(":checked"), true);
                expect(lomake.soraTerveys(1, true)().is(":checked"), false);
                expect(lomake.soraOikeudenMenetys(1, true)().is(":checked"), false);
                expect(lomake.opetuspisteDropdown(2)().val()).to.equal("Kiipulan ammattiopisto, Kiipulan toimipaikka");
                expect(lomake.koulutus(2)().val()).to.equal("Metsäalan perustutkinto, er");
                expect(lomake.soraTerveys(2, true)().is(":checked"), true);
                expect(lomake.soraOikeudenMenetys(2, true)().is(":checked"), true);
                expect(lomake.soraTerveys(2, false)().is(":checked"), false);
                expect(lomake.soraOikeudenMenetys(2, false)().is(":checked"), false);
            }));

        describe('hakukohteiden validointi', function () {
            before(seqDone(click(lomake.fromHakutoiveet)));
            it('onnistuu', seqDone(headingVisible("Arvosanat")));
        });
    });

    describe("hakemuksen täyttö oppijana", function() {
        before(seqDone(
            logout,
            openPage("/haku-app/lomake/" + hakuOid)
        ));

        describe('henkilötietojen täyttö', function () {
            before(seqDone(
                partials.henkilotiedotTestikaes
            ));
            describe('täytön jälkeen', function () {
                it('ei ongelmia', function () {
                });
            });

            describe('koulutustaustan täyttö', function () {
                before(seqDone(
                    pageChange(lomake.fromHenkilotiedot),
                    headingVisible("Koulutustausta")
                ));
                describe('syötettäessä pelkkä peruskoulutus', function () {
                    before(seqDone(
                        click(lomake.pohjakoulutus("1")),
                        input(
                            lomake.pkPaattotodistusVuosi, "2015",
                            lomake.pkKieli, "FI"
                        )
                    ));
                    describe('syötön jälkeen', function () {
                        it('ei ongelmia', function () {
                        });
                    });

                    describe('hakutoiveiden täyttö', function () {
                        before(seqDone(
                            function() { S('#nav-koulutustausta')[0].click() },
                            pageChange(lomake.fromKoulutustausta),
                            headingVisible("Hakutoiveet"),
                            valitseFaktiaJaKiipula
                        ));
                        describe('täytön jälkeen', function () {
                            it('pohjakoulutus ei vaikuta mitä hakutoiveita voi valita', function () {
                            });

                            it('hakutoiveiden tyhjennysnapeissa oikeat aria-labelit', seqDone(
                                hasAriaLabel(lomake.tyhjenna(1), 'Tyhjennä: FAKTIA, Espoo op, Talonrakennus ja ymäristösuunnittelu, pk'),
                                hasAriaLabel(lomake.tyhjenna(2), 'Tyhjennä: Kiipulan ammattiopisto, Kiipulan toimipaikka, Metsäalan perustutkinto, er'),
                                hasAriaLabel(lomake.tyhjenna(3), 'Tyhjennä')
                            ));
                        });

                        describe('arvosanojen täyttö', function () {
                            before(seqDone(
                                pageChange(lomake.fromHakutoiveet),
                                headingVisible("Arvosanat")
                            ));
                            describe('täytön jälkeen', function () {
                                it('ei ongelmia', function () {
                                });
                            });

                            describe('lupatiedoissa pyydetään tiedot erityisopetuksen tarpeesta', function () {
                                before(seqDone(
                                    pageChange(lomake.fromOsaaminen),
                                    headingVisible("Erityisopetuksen tarve"),
                                    click(lomake.asiointikieli("suomi"))
                                ));
                                describe('Jos yrittää siirtyä eteenpäin antamatta pakollisia tietoja', function () {
                                    before(seqDone(
                                        pageChange(lomake.fromLisatieto),
                                        headingVisible("Erityisopetuksen tarve")
                                    ));
                                    it('näkyy sivun alussa että tuli virheitä', function () {
                                        expect(firstWarningText()).to.contain("Lomakkeella puuttuvia tai virheellisiä tietoja, tarkista lomakkeen tiedot")
                                    });
                                    it('näkyy sivun titlessä, että tuli virheitä', function () {
                                        expect(S("title").text()).to.contain("Lomakkeella puuttuvia tai virheellisiä tietoja, tarkista lomakkeen tiedot")
                                    });
                                    it('näkyy pakollisuus virheet', function () {
                                        expect(S("#hojks-error").text()).to.equal("Pakollinen tieto.");
                                        expect(S("#koulutuskokeilu-error").text()).to.equal("Pakollinen tieto.");
                                        expect(S("#miksi_ammatilliseen-error").text()).to.equal("Pakollinen tieto.");
                                    });
                                });

                                describe('esikatselussa', function () {
                                    before(seqDone(
                                        click(
                                            lomake.hojks(false),
                                            lomake.koulutuskokeilu(true)
                                        ),
                                        input1(lomake.miksi_ammatilliseen, "siksi"),
                                        pageChange(lomake.fromLisatieto),
                                        headingVisible("Henkilötiedot")
                                    ));
                                    it('näkyy kaikki syötetyt tiedot', function () {
                                        var expectedHenkilotiedot = [
                                            ["Sukunimi", "Testikäs"],
                                            ["Etunimet", "Asia Kas"],
                                            ["Kutsumanimi", "Asia"],
                                            ["Kansalaisuus", "Suomi"],
                                            ["Onko sinulla kaksoiskansalaisuutta?", "Ei"],
                                            ["Henkilötunnus", "171175-830Y"],
                                            ["Sukupuoli", "Nainen"],
                                            ["Sähköpostiosoite", "foo@example.com"],
                                            ["Matkapuhelinnumero", ""],
                                            ["Asuinmaa", "Suomi"],
                                            ["Lähiosoite", "Testikatu 4"],
                                            ["", "00100 Helsinki"],
                                            ["Kotikunta", "Janakkala"],
                                            ["Äidinkieli", "Suomi"],
                                            ["Huoltajan nimi (jos olet alle 18-vuotias)", ""],
                                            ["Huoltajan puhelinnumero (jos olet alle 18-vuotias)", ""],
                                            ["Huoltajan sähköpostiosoite (jos olet alle 18-vuotias)", ""]
                                        ];
                                        expect(readTable(S('table#henkilotiedot_teema'), true)).to.deep.equal(expectedHenkilotiedot);

                                        var expectedKoulutustausta = [
                                            ["Valitse tutkinto, jolla haet koulutukseen:", "Perusopetuksen oppimäärä"],
                                            ["Minä vuonna sait tai saat peruskoulun päättötodistuksen?:", "2015"],
                                            ["Millä opetuskielellä olet suorittanut perusopetuksen?:", "Suomi"],
                                            ["Minkä muun koulutuksen/opintoja olet suorittanut?", ""]
                                        ];
                                        expect(readTable(S('table#koulutustausta_teema'), false)).to.deep.equal(expectedKoulutustausta);

                                        var expectedArvosanat = [];
                                        expect(readTable(S('table#arvosanat_teema'), false)).to.deep.equal(expectedArvosanat);

                                        var expectedErityisopetuksenTarve = [
                                            ['Onko sinulle laadittu peruskoulussa tai muita opintoja suorittaessasi HOJKS (Henkilökohtainen opetuksen järjestämistä koskeva suunnitelma)?:', 'Ei'],
                                            ['Oletko ollut koulutuskokeilussa?:', 'Kyllä'],
                                            ['Miksi haet erityisoppilaitokseen?', 'siksi']
                                        ];
                                        expect(readTable(S('table#erityisopetuksen_tarve'), false)).to.deep.equal(expectedErityisopetuksenTarve);
                                    });
                                });
                            });

                            describe('hakutoiveiden järjestyksen muuttaminen', function () {
                                before(seqDone(
                                    openPage("/haku-app/lomake/" + hakuOid + "/hakutoiveet"),
                                    click(lomake.sortDown(1))
                                ));

                                it('hakutoiveiden tyhjennysnapeissa oikeat aria-labelit', seqDone(
                                    hasAriaLabel(lomake.tyhjenna(1), 'Tyhjennä: Kiipulan ammattiopisto, Kiipulan toimipaikka, Metsäalan perustutkinto, er'),
                                    hasAriaLabel(lomake.tyhjenna(2), 'Tyhjennä: FAKTIA, Espoo op, Talonrakennus ja ymäristösuunnittelu, pk'),
                                    hasAriaLabel(lomake.tyhjenna(3), 'Tyhjennä')
                                ));

                                it('siirtyminen arvosanoihin yhä onnistuu', seqDone(
                                    pageChange(lomake.fromHakutoiveet),
                                    headingVisible("Arvosanat")
                                ));
                            });
                        });
                     });
                });

                describe('syötettäessä yli vuoden vanha peruskoulu ja ammattikoulutus pohjakoulutukseksi', function () {
                    before(seqDone(
                        openPage("/haku-app/lomake/" + hakuOid + "/koulutustausta"),
                        visible(lomake.muukoulutus),
                        click(lomake.pohjakoulutus("1")),
                        input(
                            lomake.pkPaattotodistusVuosi, "2010",
                            lomake.pkKieli, "FI"
                        ),
                        click(
                            lomake.ammatillinenKoulutuspaikka(false),
                            lomake.ammatillinenSuoritettu(true)
                        )
                    ));
                    describe('ennen siirtymistä hakutoiveisiin', function () {
                        it('ei tule varoitusta ammattitutkinnon vaikutuksesta', seqDone(
                            notExists(lomake.suorittanutTutkinnonRule)
                        ));
                    });

                    describe('siirtuminen hakutoiveisiin', function () {
                        before(seqDone(
                            pageChange(lomake.fromKoulutustausta)
                        ));

                        it('onnistuu', seqDone(
                            headingVisible("Hakutoiveet")
                        ));

                        describe('arvosanat vaiheessa', function () {
                            before(seqDone(
                                valitseFaktiaJaKiipula,
                                pageChange(lomake.fromHakutoiveet),
                                headingVisible("Arvosanat")
                            ));

                            it('kysytään peruskoulun päättötodistuksen arvosanat', function () {
                                expect(S('table#gradegrid-table tbody > tr:visible').size()).to.equal(18);
                            });

                            it('on äidinkielen oppiaineen valinnalle aria label', function () {
                                var ariaLabelledBy = S('table#gradegrid-table select#PK_AI_OPPIAINE').attr('aria-labelledby').split(" ");
                                expect(S("#" + ariaLabelledBy[0]).text()).to.contain("Äidinkieli ja kirjallisuus");
                                expect(S("#" + ariaLabelledBy[1]).text()).to.equal("Oppiaine");
                            });

                            it('on äidinkielen yhteisen aineen arvosana aria label', function () {
                                var ariaLabelledBy = S('table#gradegrid-table select#PK_AI').attr('aria-labelledby').split(" ");
                                expect(S("#" + ariaLabelledBy[0]).text()).to.contain("Äidinkieli ja kirjallisuus");
                                expect(S("#" + ariaLabelledBy[1]).attr("aria-label")).to.equal("Yhteinen oppiaine: Arvosana");
                            });

                            it('on äidinkielen kolmannn valinnaisen aineen arvosana aria label', function () {
                                var ariaLabelledBy = S('table#gradegrid-table select#PK_AI_VAL3').attr('aria-labelledby').split(" ");
                                expect(S("#" + ariaLabelledBy[0]).text()).to.contain("Äidinkieli ja kirjallisuus");
                                expect(S("#" + ariaLabelledBy[1]).attr("aria-label")).to.equal("Kolmas valinnainen aine: Arvosana");
                            });
                        });
                    });
                });

                describe('syötettäessä lukio-ammattikoulutus-yhdistelmä', function () {
                    before(seqDone(
                        openPage("/haku-app/lomake/" + hakuOid + "/koulutustausta"),
                        visible(lomake.muukoulutus),
                        click(lomake.pohjakoulutus("9")),
                        input(lomake.lukioPaattotodistusVuosi, "2010"),
                        click(lomake.ammatillinenSuoritettu(true))
                    ));
                    it('ei estä pääsyä hakutoiveisiin', seqDone(
                        input(lomake.lukionKieli, "FI"),
                        pageChange(lomake.fromKoulutustausta),
                        headingVisible("Hakutoiveet")
                    ));
                });
            });
        });
    });
});

