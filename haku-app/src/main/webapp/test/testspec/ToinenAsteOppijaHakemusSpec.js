describe('2. asteen lomake', function () {
    var hakuOidVanhaYhteishaku = "1.2.246.562.5.50476818906"
    var startVanhaYhteishaku = seq(
        logout,
        openPage("/haku-app/lomake/" + hakuOidVanhaYhteishaku, function() {
            return S("form#form-henkilotiedot").first().is(':visible')
        })
    )
    var startTamanKevaanPervako = seq(
        logout,
        openPage("/haku-app/lomake/haku6", function() {
          return S("form#form-henkilotiedot").first().is(':visible')
        })
    )
    var startTamanKevaanYhteishaku = seq(
        logout,
        openPage("/haku-app/lomake/haku7", function() {
            return S("form#form-henkilotiedot").first().is(':visible')
        })
    )

    describe("virkailijan näkymä", function() {
      before(seqDone(
          login('officer', 'officer'),
          click(virkailija.createApplicationButton),
          input(virkailija.selectHaku, hakuOidVanhaYhteishaku),
          click(virkailija.submitConfirm),
          exists(virkailija.hakemusOid),
          function() {
            hakemusId = virkailija.hakemusOid().text();
          }
      ));

      describe("syötä hakemus alkutilanne", function() {
        it('avautuu luonnos tilassa', function () {
          expect(virkailija.hakemusStatus().text()).to.equal('Luonnos');
        });
      });
      describe("aktivoitaessa hakemus", function() {
        before(seqDone(
            click(virkailija.activateApplication)
        ));

        describe("napin painamisen jälkeen", function() {
          it("näkyy popup", function () {
            expect(virkailija.popupHeading().text()).to.equal('Aktivoidaanko hakemus?');
          });
        });

        describe("aktivoinnin jälkeen", function() {
          before(seqDone(
              click(virkailija.confirmActivationButton),
              waitJqueryIs(virkailija.hakemusStatus, ":contains('Aktiivinen')"),
              visible(virkailija.notes)
          ));
          it("vaihtuu hakemus aktiivinen tilaan", function () {
          });
          it("on yhä arvosanojen editointiei ole enää mahdollista", function () {
            expect(virkailija.editVaiheButton(hakuOidVanhaYhteishaku, 'osaaminen')().filter(":visible").length).to.equal(0);
          });
        });
      });
    });
    describe("vanhan yhteishaun täyttö hakijana", function() {
        before(seqDone(
            startVanhaYhteishaku,
            partials.henkilotiedotTestikaes(),
            pageChange(lomake.fromHenkilotiedot),
            headingVisible("Koulutustausta"),
            waitForFormReady(),
            click(lomake.pohjakoulutus("1")),
            input(
                lomake.pkPaattotodistusVuosi, "2014",
                lomake.pkKieli, "FI"),
            pageChange(lomake.fromKoulutustausta),
            headingVisible("Hakutoiveet"),
            partials.valitseKoulutus(1, "FAKTIA, Espoo op", "Talonrakennus ja ymäristösuunnittelu, pk"),
            click(
                lomake.harkinnanvaraisuus(1, false),
                lomake.soraTerveys(1, false),
                lomake.soraOikeudenMenetys(1, false)
            ),
            pageChange(lomake.fromHakutoiveet),
            headingVisible("Arvosanat"),
            pageChange(lomake.fromOsaaminen),
            headingVisible("Lupatiedot"),
            click(lomake.asiointikieli("suomi")),
            pageChange(lomake.fromLisatieto),
            headingVisible("Henkilötiedot")
        ));

        it('Täytä hakemus alusta loppuun', function () {
            var expectedHenkilotiedot = [
                ["Sukunimi", "Testikäs"],
                ["Etunimet", "Asia Kas"],
                ["Kutsumanimi", "Asia"],
                ["Kansalaisuus", "Suomi"],
                ["Onko sinulla kaksoiskansalaisuutta?", "Ei"],
                ["Sähköpostiosoite", "foo@example.com"],
                ["SähköpostiosoiteUudelleen", "foo@example.com"],
                ["Henkilötunnus", "171175-830Y"],
                ["Sukupuoli", "Nainen"],
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
                ["Minä vuonna sait tai saat peruskoulun päättötodistuksen?:", "2014"],
                ["Millä opetuskielellä olet suorittanut perusopetuksen?:", "Suomi"]
            ];
            expect(readTable(S('table#koulutustausta_teema'), false)).to.deep.equal(expectedKoulutustausta);

            var expectedArvosanat = [];
            expect(readTable(S('table#arvosanat_teema'), false)).to.deep.equal(expectedArvosanat);
        });

        it('Peruskoulutus-ammattikoulutus-yhdistelmä pyytää lukioita', seqDone(
            function() { S('#nav-koulutustausta')[0].click() },
            headingVisible("Koulutustausta"),
            waitForFormReady(),
            input(lomake.pkPaattotodistusVuosi, "2010"),
            click(
                lomake.ammatillinenKoulutuspaikka(false),
                lomake.ammatillinenSuoritettu(true)),
            visibleText(lomake.suorittanutTutkinnonRule, "Et voi hakea yhteishaussa ammatilliseen koulutukseen"),
            pageChange(lomake.fromKoulutustausta),
            headingVisible("Hakutoiveet")
        ));

        it('Lukio-ammattikoulutus-yhdistelmä estää pääsyn hakutoiveisiin', seqDone(
            function() { S('#nav-koulutustausta')[0].click() },
            headingVisible("Koulutustausta"),
            waitForFormReady(),
            click(lomake.pohjakoulutus("9")),
            input(lomake.lukioPaattotodistusVuosi, "2010"),
            click(lomake.ammatillinenSuoritettu(true)),
            visibleText(lomake.warning, "Et voi hakea yhteishaussa, koska olet jo suorittanut ammatillisen perustutkinnon"),
            input(lomake.lukionKieli, "FI"),
            pageChange(lomake.fromKoulutustausta),
            headingVisible("Koulutustausta")
        ));
    });

    describe("Tämän kevään valmistavan koulutuksen yhteishaun koulutustausta", function() {
      before(seqDone(
          startTamanKevaanPervako,
          partials.henkilotiedotTestikaes(),
          pageChange(lomake.fromHenkilotiedot),
          headingVisible("Koulutustausta"),
          waitForFormReady(),
          hidden(lomake.overlay),
          click(lomake.pohjakoulutus("1"))
      ));
      describe("Edellisvuonna suoritettu peruskoulu", function() {
        before(seqDone(
            input(lomake.pkPaattotodistusVuosi, "" + ((new Date()).getUTCFullYear() - 1)),
            input(lomake.pkKieli, "FI"),
            click(lomake.ammatillinenKoulutuspaikka(false))
        ));
        describe("Ei vaadi saantiajankohtaa", function() {
          before(seqDone(
              pageChange(lomake.fromKoulutustausta)
          ));
          it('ei kysy todistuksen saanti ajankohtaa', seqDone(
              headingVisible("Hakutoiveet")
          ));
        });
      });
    });

    describe("Tämän kevään yhteishaun koulutustausta eri PK päättötodistusvuosilla", function() {
        before(seqDone(
            startTamanKevaanYhteishaku,
            partials.henkilotiedotTestikaes(),
            pageChange(lomake.fromHenkilotiedot),
            headingVisible("Koulutustausta"),
            hidden(lomake.overlay),
            click(lomake.pohjakoulutus("1"))
        ));

        describe("Aiemmin kuin edellisvuonna suoritettu peruskoulu", function() {
            before(seqDone(
                function() { S('#nav-koulutustausta')[0].click() },
                headingVisible("Koulutustausta"),
                waitForFormReady(),
                hidden(lomake.overlay),
                input(lomake.pkPaattotodistusVuosi, "" + ((new Date()).getUTCFullYear() - 3)),
                input(lomake.pkKieli, "FI"),
                click(lomake.ammatillinenKoulutuspaikka(false)),
                click(lomake.ammatillinenSuoritettu(false)),
                pageChange(lomake.fromKoulutustausta)
            ));
            it('ei kysy todistuksen saanti ajankohtaa', seqDone(
                headingVisible("Hakutoiveet")
            ));
        });

        describe("Kuluvana vuonna suoritettu peruskoulu", function() {
            before(seqDone(
                function() { S('#nav-koulutustausta')[0].click() },
                headingVisible("Koulutustausta"),
                waitForFormReady(),
                input(lomake.pkPaattotodistusVuosi, "" + ((new Date()).getUTCFullYear())),
                input(lomake.pkKieli, "FI"),
                pageChange(lomake.fromKoulutustausta)
            ));
            it('ei kysy todistuksen saanti ajankohtaa', seqDone(
                headingVisible("Hakutoiveet")
            ));
        });
    });

    describe("Sääntötestit", function() {
        before(seqDone(
            startVanhaYhteishaku,
            visible(lomake.sukunimi),
            postAsForm("/haku-app/lomake/1.2.246.562.5.50476818906", {
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
                "preference1-Koulutus-id-discretionary": "true",
                "preference1-Koulutus-id-attachments": "false",
                "preference1-discretionary": "false",
                "asiointikieli": "suomi"
            }),
            visible(lomake.sukunimi)
        ));

        describe("Jos PK päättötodistusvuosi 2011", function() {
            it("Kysytäään myös ammatillinen tutkinto", seqDone(
                post("/haku-app/lomake/1.2.246.562.5.50476818906/koulutustausta/rules", {
                    "PK_PAATTOTODISTUSVUOSI": "2011",
                    "POHJAKOULUTUS": "1",
                    "ruleIds[]": ["paattotodistuvuosiPkRule"]
                }),
                function (data) {
                    expect(data).to.contain('type=\\"radio\\" name=\\"ammatillinenTutkintoSuoritettu\\"');
                }
            ));
        });

        describe("Jos PK päättötodistusvuosi on 2012", function() {
            it("Ei kysytä ammatillista tutkintoa", seqDone(
                post("/haku-app/lomake/1.2.246.562.5.50476818906/koulutustausta/rules", {
                    "PK_PAATTOTODISTUSVUOSI": "2012",
                    "POHJAKOULUTUS": "1",
                    "ruleIds[]": ["paattotodistuvuosiPkRule"]
                }),
                function(data) { expect(data).not.to.contain('type=\\"radio\\" name=\\"ammatillinenTutkintoSuoritettu\\"'); }
            ));
        });
    });

    describe("Urheilijakohteet", function() {
        before(seqDone(
            startVanhaYhteishaku,
            visible(lomake.sukunimi),
            postAsForm("/haku-app/lomake/1.2.246.562.5.50476818906", {
                "Sukunimi": "Testikäs",
                "Etunimet": "Asia Kas",
                "Kutsumanimi": "Asia",
                "kansalaisuus": "FIN",
                "onkosinullakaksoiskansallisuus": "false",
                "Henkilotunnus": "171175-830Y",
                "Sähköposti": "foo@example.com",
                "SähköpostiUudelleen": "foo@example.com",
                "sukupuoli": "2",
                "asuinmaa": "FIN",
                "lahiosoite": "Testikatu 4",
                "Postinumero": "00100",
                "kotikunta": "janakkala",
                "aidinkieli": "FI",
                "POHJAKOULUTUS": "1",
                "PK_PAATTOTODISTUSVUOSI": "2014",
                "perusopetuksen_kieli": "FI"
            })
        ));

        it("Urheilevat kokit", seqDone(
            visible(lomake.sukunimi),
            pageChange(lomake.fromHenkilotiedot),
            headingVisible("Koulutustausta"),
            pageChange(lomake.fromKoulutustausta),
            headingVisible("Hakutoiveet"),
            partials.valitseKoulutus(1, "Urheilijoiden koulu", "Urheilevien kokkien koulutus"),
            click(
                lomake.harkinnanvaraisuus(1, false),
                lomake.urheilija(1, true)),
            pageChange(lomake.fromHakutoiveet),
            headingVisible("Arvosanat")
        ));
    });

    describe("Harkinnanvaraisuus lisäkysymykset", function() {
        before(seqDone(
            startVanhaYhteishaku,
            visible(lomake.sukunimi),
            postAsForm("/haku-app/lomake/1.2.246.562.5.50476818906", {
                "Sukunimi": "Testikäs",
                "Etunimet": "Asia Kas",
                "Kutsumanimi": "Asia",
                "kansalaisuus": "FIN",
                "onkosinullakaksoiskansallisuus": "false",
                "Henkilotunnus": "171175-830Y",
                "Sähköposti": "foo@example.com",
                "SähköpostiUudelleen": "foo@example.com",
                "sukupuoli": "2",
                "asuinmaa": "FIN",
                "lahiosoite": "Testikatu 4",
                "Postinumero": "00100",
                "kotikunta": "janakkala",
                "aidinkieli": "FI",
                "POHJAKOULUTUS": "1",
                "PK_PAATTOTODISTUSVUOSI": "2014",
                "perusopetuksen_kieli": "FI"
            })
        ));

        it("ei kysytä musiikkialalta", seqDone(
            visible(lomake.sukunimi),
            pageChange(lomake.fromHenkilotiedot),
            headingVisible("Koulutustausta"),
            pageChange(lomake.fromKoulutustausta),
            headingVisible("Hakutoiveet"),
            partials.valitseKoulutus(1, "Musiikkialan koulu", "Musiikkialan perustutkinto"),

            notExists(lomake.harkinnanvaraisuus(1, false))
        ));
    });

    describe("VALMA, TELMA ja oppisopimus", function() {
        before(seqDone(
            startVanhaYhteishaku,
            visible(lomake.sukunimi),
            postAsForm("/haku-app/lomake/1.2.246.562.5.50476818906", {
                "Sukunimi": "Testikäs",
                "Etunimet": "Asia Kas",
                "Kutsumanimi": "Asia",
                "kansalaisuus": "FIN",
                "onkosinullakaksoiskansallisuus": "false",
                "Henkilotunnus": "171175-830Y",
                "Sähköposti": "foo@example.com",
                "SähköpostiUudelleen": "foo@example.com",
                "sukupuoli": "2",
                "asuinmaa": "FIN",
                "lahiosoite": "Testikatu 4",
                "Postinumero": "00100",
                "kotikunta": "janakkala",
                "aidinkieli": "FI",
                "POHJAKOULUTUS": "1",
                "PK_PAATTOTODISTUSVUOSI": "2014",
                "perusopetuksen_kieli": "FI",
                "preferencesVisible": "5"
            })
        ));

        it("Checkboxit löytyvät", seqDone(
            visible(lomake.sukunimi),
            pageChange(lomake.fromHenkilotiedot),
            headingVisible("Koulutustausta"),
            visible(lomake.lisakoulutusValma),
            visible(lomake.lisakoulutusTelma),
            pageChange(lomake.fromKoulutustausta),
            headingVisible("Hakutoiveet"),
            partials.valitseKoulutus(1, "FAKTIA, Espoo op", "Kaivosalan perustutkinto, pk"),
            click(
                lomake.harkinnanvaraisuus(1, false),
                lomake.soraTerveys(1, false),
                lomake.soraOikeudenMenetys(1, false)
            ),
            pageChange(lomake.fromHakutoiveet),
            headingVisible("Arvosanat"),
            pageChange(lomake.fromOsaaminen),
            headingVisible("Lupatiedot"),
            visible(lomake.kiinnostunutOppisopimuksesta)
        ));
    });

    describe("Kymppiluokan käynyt", function() {
        before(seqDone(
            startVanhaYhteishaku,
            visible(lomake.sukunimi),
            postAsForm("/haku-app/lomake/1.2.246.562.5.50476818906", {
                "Sukunimi": "Testikäs",
                "Etunimet": "Asia Kas",
                "Kutsumanimi": "Asia",
                "kansalaisuus": "FIN",
                "onkosinullakaksoiskansallisuus": "false",
                "Henkilotunnus": "171175-830Y",
                "Sähköposti": "foo@example.com",
                "SähköpostiUudelleen": "foo@example.com",
                "sukupuoli": "2",
                "asuinmaa": "FIN",
                "lahiosoite": "Testikatu 4",
                "Postinumero": "00100",
                "kotikunta": "janakkala",
                "aidinkieli": "FI",
                "POHJAKOULUTUS": "1",
                "PK_PAATTOTODISTUSVUOSI": "2011",
                "perusopetuksen_kieli": "FI",
                "preferencesVisible": "5"
            })
        ));

        it("päättövuotta kysytään taustatiedoissa", seqDone(
            pageChange(lomake.fromHenkilotiedot),
            headingVisible("Koulutustausta"),
            visible(lomake.lisakoulutusKymppi),
            click(lomake.lisakoulutusKymppi),
            visible(lomake.lisakoulutusKymppiYear)
        ));

        it("päättövuonna hakevalta ei kysytä arvosanoja", seqDone(
            input(lomake.lisakoulutusKymppiYear, "2014"),
            click(
                lomake.ammatillinenKoulutuspaikka(false),
                lomake.ammatillinenSuoritettu(true)),

            pageChange(lomake.fromKoulutustausta),
            headingVisible("Hakutoiveet"),
            partials.valitseKoulutus(1, "FAKTIA, Espoo op", "Talonrakennus ja ymäristösuunnittelu, pk"),
            click(
                lomake.harkinnanvaraisuus(1, false),
                lomake.soraTerveys(1, false),
                lomake.soraOikeudenMenetys(1, false)
            ),
            pageChange(lomake.fromHakutoiveet),
            headingVisible("Arvosanat"),
            visible(lomake.noPreferencesText)
        ));
    });
});

