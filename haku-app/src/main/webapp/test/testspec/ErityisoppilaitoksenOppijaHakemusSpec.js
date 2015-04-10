describe('Erityisoppilaitosten lomake', function () {
    var start = seq(
        logout,
        openPage("/haku-app/lomakkeenhallinta/1.2.246.562.20.807716131410", function() {
            return S("form#form-henkilotiedot").first().is(':visible')
        }),
        openPage("/haku-app/lomake/1.2.246.562.20.807716131410")
    )
    describe("Täytä lomake", function() {
        before(seqDone(
            start
        ));

        describe('Henkilötiedot', function () {
            before(seqDone(
                partials.henkilotiedotTestikaes
            ));
            describe('Täytön jälkeen', function () {
                it('ei ongelmia', function () {
                });
            });

            describe('Koulutustausta', function () {
                before(seqDone(
                    pageChange(lomake.fromHenkilotiedot),
                    headingVisible("Koulutustausta")
                ));
                describe('Syötettäessä pelkkä peruskoulutus', function () {
                    before(seqDone(
                        click(lomake.pohjakoulutus("1")),
                        input(
                            lomake.pkPaattotodistusVuosi, "2015",
                            lomake.pkKieli, "FI"
                        )
                    ));
                    describe('Syötön jälkeen', function () {
                        it('ei ongelmia', function () {
                        });
                    });

                    describe('Hakutoiveet', function () {
                        before(seqDone(
                            function() { S('#nav-koulutustausta')[0].click() },
                            pageChange(lomake.fromKoulutustausta),
                            headingVisible("Hakutoiveet"),
                            partials.valitseKoulutus(1, "FAKTIA, Espoo op", "Talonrakennus ja ymäristösuunnittelu, pk"),
                            partials.valitseKoulutus(2, "FAKTIA, Espoo op", "Metsäalan perustutkinto, er"),
                            click(
                                lomake.harkinnanvaraisuus1(false),
                                lomake.soraTerveys1(false),
                                lomake.soraOikeudenMenetys1(false))
                        ));
                        describe('Täytön jälkeen', function () {
                            it('pohjakoulutus ei vaikuta mitä hakutoiveita voi valita', function () {
                            });
                        });

                        describe('Arvosanat', function () {
                            before(seqDone(
                                pageChange(lomake.fromHakutoiveet),
                                headingVisible("Arvosanat")
                            ));
                            describe('Täytön jälkeen', function () {
                                it('ei ongelmia', function () {
                                });
                            });

                            describe('Lupatiedoissa pyydetään tiedot erityisopetuksen tarpeesta', function () {
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

                                describe('Esikatselussa', function () {
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

                            describe('Hakutoiveiden järjestyksen muuttaminen', function () {
                                before(seqDone(
                                    openPage("/haku-app/lomake/1.2.246.562.20.807716131410/hakutoiveet"),
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

                describe('Syötettäessä Peruskoulutus-ammattikoulutus-yhdistelmä', function () {
                    before(seqDone(
                        openPage("/haku-app/lomake/1.2.246.562.20.807716131410/koulutustausta"),
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

                describe('Syötettäessä Lukio-ammattikoulutus-yhdistelmä', function () {
                    before(seqDone(
                        openPage("/haku-app/lomake/1.2.246.562.20.807716131410/koulutustausta"),
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

