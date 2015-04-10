describe('2. asteen lomake', function () {
    var start = seq(
        logout,
        openPage("/haku-app/lomakkeenhallinta/1.2.246.562.5.50476818906", function() {
            return S("form#form-henkilotiedot").first().is(':visible')
        }),
        openPage("/haku-app/lomake/1.2.246.562.5.50476818906")
    )
    describe("Täytä lomake", function() {
        before(seqDone(
            start,
            partials.henkilotiedotTestikaes,
            pageChange(lomake.fromHenkilotiedot),
            headingVisible("Koulutustausta"),
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
            click(lomake.pohjakoulutus("9")),
            input(lomake.lukioPaattotodistusVuosi, "2010"),
            click(lomake.ammatillinenSuoritettu(true)),
            visibleText(lomake.warning, "Et voi hakea yhteishaussa, koska olet jo suorittanut ammatillisen perustutkinnon"),
            input(lomake.lukionKieli, "FI"),
            pageChange(lomake.fromKoulutustausta),
            headingVisible("Koulutustausta")
        ));

        it('Aikaisempien koulutusten keskiarvot tallentuvat erikseen', seqDone(
            function() { S('#nav-koulutustausta')[0].click() }
        ));
    });

    describe("Sääntötestit", function() {
        before(seqDone(
            start,
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
                "preference1-Koulutus-id-attachments": "false",
                "preference1-discretionary": "false",
                "asiointikieli": "suomi"
            }),
            visible(lomake.sukunimi)
        ));

        it("PK päättötodistusvuosi 2011", seqDone(
            post("/haku-app/lomake/1.2.246.562.5.50476818906/koulutustausta/rules", {
                "PK_PAATTOTODISTUSVUOSI": "2011",
                "POHJAKOULUTUS": "1",
                "ruleIds[]": ["paattotodistuvuosiPkRule"]
            }),
            function(data) { expect(data).to.contain('<input type=\\"radio\\" name=\\"ammatillinenTutkintoSuoritettu\\"'); }
        ));

        it("PK päättötodistusvuosi 2012", seqDone(
            post("/haku-app/lomake/1.2.246.562.5.50476818906/koulutustausta/rules", {
                "PK_PAATTOTODISTUSVUOSI": "2012",
                "POHJAKOULUTUS": "1",
                "ruleIds[]": ["paattotodistuvuosiPkRule"]
            }),
            function(data) { expect(data).not.to.contain('<input type=\\"radio\\" name=\\"ammatillinenTutkintoSuoritettu\\"'); }
        ));
    });

    describe("Urheilijakohteet", function() {
        before(seqDone(
            start,
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
});

