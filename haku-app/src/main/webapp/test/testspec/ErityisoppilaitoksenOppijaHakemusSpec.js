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
                        partials.valitseKoulutus(1, faktia, faktiaPkKoulutus),
                        partials.valitseKoulutus(2, faktia, faktiaErKoulutus),
                        click(
                            lomake.soraTerveys1(false),
                            lomake.soraOikeudenMenetys1(false)
                        ),
                        click(virkailija.saveHakutoiveetButton),
                        visible(virkailija.notes)
                    ));

                    describe("lisäämisen jälkeen", function() {
                        it("toiveet näkyvät", function () {
                            expect(answerForQuestion('preference1')).to.equal(faktia);
                            expect(answerForQuestion('preference2')).to.equal(faktia);
                        });
                    });
                });
            });
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
                            partials.valitseKoulutus(1, faktia, faktiaPkKoulutus),
                            partials.valitseKoulutus(2, faktia, faktiaErKoulutus),
                            click(
                                lomake.soraTerveys1(false),
                                lomake.soraOikeudenMenetys1(false)
                            )
                        ));
                        describe('täytön jälkeen', function () {
                            it('pohjakoulutus ei vaikuta mitä hakutoiveita voi valita', function () {
                            });
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
                                describe('Jos yrittää siirtyä eteenpäin antamatta', function () {
                                    before(seqDone(
                                        pageChange(lomake.fromLisatieto),
                                        headingVisible("Erityisopetuksen tarve")
                                    ));
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
                                            ["Sähköpostiosoite", ""],
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
                                it('ei haittaa', seqDone(
                                    pageChange(lomake.fromHakutoiveet),
                                    headingVisible("Arvosanat")
                                ));
                            });
                        });
                     });
                });

                describe('syötettäessä peruskoulutus-ammattikoulutus-yhdistelmä', function () {
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
                    it('ei haittaa', seqDone(
                        notExists(lomake.suorittanutTutkinnonRule),
                        pageChange(lomake.fromKoulutustausta),
                        headingVisible("Hakutoiveet")
                    ));
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
                        notExists(lomake.warning),
                        input(lomake.lukionKieli, "FI"),
                        pageChange(lomake.fromKoulutustausta),
                        headingVisible("Hakutoiveet")
                    ));
                });
            });
        });
    });
});

