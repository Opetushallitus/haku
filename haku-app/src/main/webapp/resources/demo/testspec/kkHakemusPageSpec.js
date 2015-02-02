(function () {
    function asyncPrint(s) { return function() { console.log(s) } }

    describe('KK-hakemus', function () {
        var page = KkHakemusPage();

        beforeEach(
            page.createApplication
        );

        afterEach(function () {
            if (this.currentTest.state == 'failed') {
                takeScreenshot()
            }
        });

        describe("Muokkaa koulutustaustaa -toiminto", function() {

            beforeEach(
                function(done) {
                    wait.until(function() {return page.editKoulutusTaustaButton().is(':visible')})()
                        .then(function() {return page.editKoulutusTaustaButton().click()})
                        .then(wait.until(function() {return page.addAmmatillinenCheckbox().is(':visible')}))
                        .then(function() {return page.addAmmatillinenCheckbox().click()})
                        .then(wait.until(function() {
                            return page.ammatillinenSuoritusVuosi().is(':visible')
                                && page.ammatillinenTutkintonimike().is(':visible')
                                && page.ammatillinenTutkinnonLaajuus().is(':visible')
                                && page.ammatillinenOppilaitos().is(':visible')
                                && page.ammatillinenNayttotutkinto().is(':visible')
                        }))
                        .then(function() {
                            page.ammatillinenSuoritusVuosi().val("2000");
                            page.ammatillinenTutkintonimike().val("Hitsaajan perustutkinto");
                            page.ammatillinenTutkinnonLaajuus().val("120");
                            page.ammatillinenOppilaitos().val("Ammattikoulu X");
                            page.kkTutkintoSuoritettu().val(['false']);
                        })
                        .then(function() {return page.addAvoinCheckbox().click()})
                        .then(wait.until(function() {
                            return page.avoinAla().is(':visible')
                                && page.avoinKokonaisuus().is(':visible')
                                && page.avoinLaajuus().is(':visible')
                                && page.avoinKorkeakoulu().is(':visible')
                        }))
                        .then(function() {
                            page.avoinAla().val("Avoin ala 1");
                            page.avoinKokonaisuus().val("Avoin kokonaisuus 1");
                            page.avoinLaajuus().val("Avoin laajuus 1");
                            page.avoinKorkeakoulu().val("Avoin Korkeakoulu 1");
                        })
                        .then(function() {page.addSecondAvoinLink().click()})
                        .then(wait.until(function() {
                            return page.avoinAla(2).is(':visible')
                                && page.avoinKokonaisuus(2).is(':visible')
                                && page.avoinLaajuus(2).is(':visible')
                                && page.avoinKorkeakoulu(2).is(':visible')
                        }))
                        .then(function() {
                            page.avoinAla(2).val("Avoin ala 2");
                            page.avoinKokonaisuus(2).val("Avoin kokonaisuus 2");
                            page.avoinLaajuus(2).val("Avoin laajuus 2");
                            page.avoinKorkeakoulu(2).val("Avoin Korkeakoulu 2");
                        })
                        .then(function() {page.saveButton().click()})
                        .then(wait.until(function() {return page.ammatilliset().is(':visible')}))
                        .then(done);
            });

            it('mahdollistaa uuden ammatillisen pohjakoulutuksen lisäämisen kaksi kertaa peräkkäin', function (done) {
                expect(page.answerForQuestion('pohjakoulutus_am_vuosi')).to.equal('2000');
                expect(page.answerForQuestion('pohjakoulutus_avoin_ala')).to.equal('Avoin ala 1');
                expect(page.answerForQuestion('pohjakoulutus_avoin_ala2')).to.equal('Avoin ala 2');
                done();
            });
        });
    });
})();