describe('KK-hakemus', function () {
    var virkailija = virkailijaSelectors();

    function answerForQuestion(name) {
        return S('td:has(a[name=' + name + '])').next().html()
    }

    beforeEach(seqDone(
        logout,
        function() {
            return openPage("/haku-app/lomakkeenhallinta/1.2.246.562.29.173465377510", function() {
                return S("form#form-henkilotiedot").first().is(':visible')
            })()
        },
        openPage("/haku-app/virkailija/hakemus", function() {
            return testFrame().document.getElementById('loginForm') !== null;
        }),
        function() {
            function input(name) {
                return testFrame().document.getElementsByName(name)[0];
            }
            input("j_username").value = "officer";
            input("j_password").value = "officer";
            input("login").click();
        },
        click(virkailija.createApplicationButton),
        input(virkailija.selectHaku, "1.2.246.562.29.173465377510"),
        click(virkailija.submitConfirm)
    ));

    describe("Muokkaa koulutustaustaa -toiminto", function() {
        beforeEach(seqDone(
            click(
                virkailija.editKoulutusTaustaButton,
                virkailija.addAmmatillinenCheckbox),
            input(
                virkailija.ammatillinenSuoritusVuosi, "2000",
                virkailija.ammatillinenTutkintonimike, "Hitsaajan perustutkinto",
                virkailija.ammatillinenTutkinnonLaajuus, "120",
                virkailija.ammatillinenOppilaitos, "Ammattikoulu X"),
            click(
                virkailija.kkTutkintoSuoritettu(false),
                virkailija.addAvoinCheckbox),
            input(
                virkailija.avoinAla(), "Avoin ala 1",
                virkailija.avoinKokonaisuus(), "Avoin kokonaisuus 1",
                virkailija.avoinLaajuus(), "Avoin laajuus 1",
                virkailija.avoinKorkeakoulu(), "Avoin Korkeakoulu 1"),
            click(virkailija.addSecondAvoinLink),
            input(
                virkailija.avoinAla(2), "Avoin ala 2",
                virkailija.avoinKokonaisuus(2), "Avoin kokonaisuus 2",
                virkailija.avoinLaajuus(2), "Avoin laajuus 2",
                virkailija.avoinKorkeakoulu(2), "Avoin Korkeakoulu 2"),
            click(virkailija.saveButton),
            exists(virkailija.ammatilliset)
        ));

        it('mahdollistaa uuden ammatillisen pohjakoulutuksen lisäämisen kaksi kertaa peräkkäin', function (done) {
            expect(answerForQuestion('pohjakoulutus_am_vuosi')).to.equal('2000');
            expect(answerForQuestion('pohjakoulutus_avoin_ala')).to.equal('Avoin ala 1');
            expect(answerForQuestion('pohjakoulutus_avoin_ala2')).to.equal('Avoin ala 2');
            done();
        });
    });
});
