(function () {
    function asyncPrint(s) { return function() { console.log(s) } }

    describe('KK-hakemus', function () {
        var page = KkHakemusPage();

        beforeEach(
            page.createApplication
        );

        describe("Muokkaa koulutustaustaa -toiminto", function() {
            it('voi lisätä ammatillisen pohjakoulutuksen', function () {
                console.log("testing, testing, 1 2 3");
                wait.until(function() {return page.editKoulutusTaustaButton().is(':visible')})()
                    .then(function() {return page.editKoulutusTaustaButton().click()})
                    .then(asyncPrint(page.editKoulutusTaustaButton()))
                    .then(wait.until(function() {return page.addAmmatillinenCheckbox().is(':visible')}))
                    .then(function() {return page.addAmmatillinenCheckbox().click()})
                    .then(wait.until(function() {return page.ammatillinenSuoritusVuosi().is(':visible')}))
                    .then(function() {
                        page.ammatillinenSuoritusVuosi().val("2000");
                        page.ammatillinenTutkintonimike().val("Hitsaajan perustutkinto");
                        page.ammatillinenTutkinnonLaajuus().val("120");
                        page.ammatillinenOppilaitos().val("Ammattikoulu X");
                        page.kkTutkintoSuoritettu().val(['false']);
                        page.saveButton().click();
                    })
            });
        });
    });
})();