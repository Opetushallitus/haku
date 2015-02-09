(function () {

    describe('2. asteen hakemus', function () {
        var henkilotietoPage = ToinenAsteLomakeHenkilotietoPage();
        var koulutustaustaPage = ToinenAsteLomakeKoulutustaustaPage();
        var hakutoiveetPage = ToinenAsteLomakeHakutoiveetPage();

        var fillHenkilotiedot = function() {
            henkilotietoPage.sukunimi().val("Testikäs");
            henkilotietoPage.sukunimi().blur();
            henkilotietoPage.etunimet().val("Asia Kas");
            henkilotietoPage.etunimet().blur();
            henkilotietoPage.kutsumanimi().val("Asia");
            henkilotietoPage.kutsumanimi().blur();
            henkilotietoPage.kaksoiskansalaisuus(false);
            henkilotietoPage.hetu().val("171175-830Y");
            henkilotietoPage.hetu().blur();
            henkilotietoPage.lahiosoite().val("Testikatu 4");
            henkilotietoPage.lahiosoite().blur();
            henkilotietoPage.postinumero().val("00100");
            henkilotietoPage.postinumero().blur();
            henkilotietoPage.kotikunta().val("janakkala");
            henkilotietoPage.kotikunta().blur();
            return true;
        };
        var fillKoulutustausta = function() {
            koulutustaustaPage.pohjakoulutus("1");
            koulutustaustaPage.pkPaattotodistusvuosi().val(2014);
            koulutustaustaPage.pkPaattotodistusvuosi().blur();
            koulutustaustaPage.pkKieli().val("FI");
            return true;
        }

        describe("Täytä henkilötiedot", function(done) {

            before(function(done) {
                wait.until(function() { return henkilotietoPage.start(); })()
                    .then(wait.until(function() { return henkilotietoPage.sukunimi().is(':visible'); }))
                    .then(wait.until(fillHenkilotiedot))
                    .then(wait.until(koulutustaustaPage.fromHenkilotiedot().click()))
                    .then(wait.until(function() { return S("fieldset#koulutustausta.teema").is(":visible"); }))
                    .then(wait.until(fillKoulutustausta))
                    .then(done);
             });

            it('mahdollistaa henkilötietovaiheen täyttämisen', function (done) {

                expect(S("span.post-office").first().html()).to.equal("Helsinki");

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