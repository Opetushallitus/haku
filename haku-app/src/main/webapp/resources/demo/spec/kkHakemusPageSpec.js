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
            it('mahdollistaa uuden ammatillisen pohjakoulutuksen lisäämisen kaksi kertaa peräkkäin', function () {
                console.log("testing, testing, 1 2 3");
                wait.until(function() {return page.editKoulutusTaustaButton().is(':visible')})()
                    .then(function() {return page.editKoulutusTaustaButton().click()})
                    .then(wait.until(function() {return page.addAmmatillinenCheckbox().is(':visible')}))
                    .then(function() {return page.addAmmatillinenCheckbox().click()})
                    .then(wait.until(function() {
                        return page.ammatillinenSuoritusVuosi().is(':visible')
                            && page.ammatillinenTutkintonimike().is(':visible')
                            && page.ammatillinenTutkinnonLaajuus().is(':visible')
                            && page.ammatillinenOppilaitos().is(':visible')
                    }))
                    .then(function() {
                        page.ammatillinenSuoritusVuosi().val("2000");
                        page.ammatillinenTutkintonimike().val("Hitsaajan perustutkinto");
                        page.ammatillinenTutkinnonLaajuus().val("120");
                        page.ammatillinenOppilaitos().val("Ammattikoulu X");
                        page.kkTutkintoSuoritettu().val(['false']);
                        page.saveButton().click();
                    })
                    .then(wait.until(function() {return page.ammatilliset().is(':visible')}))
                    .then(wait.forMilliseconds(5000))

                    .then(wait.until(function() {return page.editKoulutusTaustaButton().is(':visible')}))
                    .then(function() {return page.editKoulutusTaustaButton().click()})
                    .then(wait.until(function() {return page.addSecondAmmatillinenLink().is(':visible')}))
                    .then(function() {return page.addSecondAmmatillinenLink().click()})
                    .then(wait.until(function() {
                        return page.ammatillinenSuoritusVuosi2().is(':visible')
                            && page.ammatillinenTutkintonimike2().is(':visible')
                            && page.ammatillinenTutkinnonLaajuus2().is(':visible')
                            && page.ammatillinenOppilaitos2().is(':visible')
                    }))
                    .then(asyncPrint("second ammatillinen visible"))
                    .then(function() {
                        page.ammatillinenSuoritusVuosi2().val("2002");
                        page.ammatillinenTutkintonimike2().val("Tietotekniikan perustutkinto");
                        page.ammatillinenTutkinnonLaajuus2().val("120");
                        page.ammatillinenOppilaitos2().val("SLK");
                        page.saveButton().click();
                    })
                    .then(wait.until(function() {
                        return page.ammatilliset().is(':visible')
                            && page.ammatillinenOppilaitos2Text().is(':visible')
                    }));
            });
        });
    });
})();