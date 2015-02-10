(function () {

    describe('2. asteen hakemus', function () {
        var henkilotietoPage = ToinenAsteLomakeHenkilotietoPage();
        var koulutustaustaPage = ToinenAsteLomakeKoulutustaustaPage();
        var hakutoiveetPage = ToinenAsteLomakeHakutoiveetPage();
        var osaaminenPage = ToinenAsteLomakeOsaaminenPage();
        var lisatietoPage = ToinenAsteLomakeLisatietoPage();
        var esikatseluPage = ToinenAsteLomakeEsikatseluPage();

        var fillHenkilotiedot = function() {
            henkilotietoPage.sukunimi().val("Testikäs");
            henkilotietoPage.sukunimi().blur();
            henkilotietoPage.etunimet().val("Asia Kas");
            henkilotietoPage.etunimet().blur();
            henkilotietoPage.kutsumanimi().val("Asia");
            henkilotietoPage.kutsumanimi().blur();
            henkilotietoPage.kaksoiskansalaisuus(false);
            henkilotietoPage.hetu().val("171175-830Y");
            henkilotietoPage.hetu().change();
            henkilotietoPage.lahiosoite().val("Testikatu 4");
            henkilotietoPage.lahiosoite().blur();
            henkilotietoPage.postinumero().val("00100");
            henkilotietoPage.postinumero().blur();
            henkilotietoPage.kotikunta().val("janakkala");
            henkilotietoPage.kotikunta().blur();
            return true;
        };
        var fillKoulutustausta = function() {
            koulutustaustaPage.pkPaattotodistusVuosi().val("2014");
            koulutustaustaPage.pkPaattotodistusVuosi().blur();
            koulutustaustaPage.pkKieli().val("FI");
        }

        describe("Täytä henkilötiedot", function(done) {

            before(function(done) {
                henkilotietoPage.start()
                    .then(wait.until(function() { return henkilotietoPage.sukunimi().is(':visible'); }))
                    .then(wait.until(fillHenkilotiedot))
                    .then(function() { koulutustaustaPage.fromHenkilotiedot().click(); })
                    .then(wait.until(function() { return S("legend[class=h3]:contains(Koulutustausta)").is(":visible"); }))
                    .then(function() { koulutustaustaPage.pohjakoulutus("1"); })
                    .then(wait.until(function() { return koulutustaustaPage.pkPaattotodistusVuosi().is(":visible") }))
                    .then(fillKoulutustausta)
                    .then(function() { hakutoiveetPage.fromKoulutustausta().click(); } )
                    .then(wait.until(function() { return S("legend[class=h3]:contains(Hakutoiveet)").is(":visible"); }))
                    .then(function () { hakutoiveetPage.opetuspiste1().val("Esp")})
                    .then(function () { hakutoiveetPage.opetuspiste1().trigger("keydown")})
                    .then(wait.until(function() { return hakutoiveetPage.faktia().is(":visible")}))
                    .then(function() { return hakutoiveetPage.faktia().mouseover().click()})
                    .then(wait.until( function() { return hakutoiveetPage.koulutus1().find('option').length > 1 }))
                    .then(function() { return hakutoiveetPage.koulutus1().val("Talonrakennus ja ymäristösuunnittelu, yo").change() })
                    .then(wait.until( function() { return hakutoiveetPage.harkinnanvaraisuus1(false).is(':visible') }))
                    .then(function() { hakutoiveetPage.harkinnanvaraisuus1(false).click() })
                    .then(function() { hakutoiveetPage.soraTerveys1(false).click() })
                    .then(function() { hakutoiveetPage.soraOikeudenMenetys1(false).click() })
                    .then(function() { osaaminenPage.fromHakutoiveet().click() })
                    .then(wait.until(function() { return S("legend[class=h3]:contains(Arvosanat)").is(":visible"); }))
                    .then(function() { lisatietoPage.fromOsaaminen().click() })
                    .then(wait.until(function() { return S("legend[class=h3]:contains(Lupatiedot)").is(":visible"); }))
                    .then(function() { lisatietoPage.asiointikieli("suomi")})
                    .then(function() { esikatseluPage.fromLisatieto().click() })
                    .then(wait.until(function() { return S("legend[class=h3]:contains(Henkilötiedot)").is(":visible"); }))
                    .then(done, done);
             });

            it('mahdollistaa henkilötietovaiheen täyttämisen', function (done) {
                expect()
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