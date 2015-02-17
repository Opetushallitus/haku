describe('KK-hakemus', function () {
    var virkailija = virkailijaSelectors();

    function answerForQuestion(name) {
        return S('td:has(a[name=' + name + '])').next().html()
    }

    function start() {
        return logout().then(function() {
            return openPage("/haku-app/lomakkeenhallinta/1.2.246.562.29.173465377510", function() {
                return S("form#form-henkilotiedot").first().is(':visible')
            })()})
            .then(openPage("/haku-app/virkailija/hakemus", function() {
                return testFrame().document.getElementById('loginForm') !== null;
            }))
            .then(function() {
                function input(name) {
                    return testFrame().document.getElementsByName(name)[0];
                }
                input("j_username").value = "officer";
                input("j_password").value = "officer";
                input("login").click();
            })
            .then(click(virkailija.createApplicationButton))
            .then(input(virkailija.selectHaku, "1.2.246.562.29.173465377510"))
            .then(click(virkailija.submitConfirm))
    }

    beforeEach(start);

    describe("Muokkaa koulutustaustaa -toiminto", function() {
        beforeEach(function(done) {
            Q.fcall(click(virkailija.editKoulutusTaustaButton, virkailija.addAmmatillinenCheckbox))
                .then(function() {
                    return Q.all([
                        input(virkailija.ammatillinenSuoritusVuosi, "2000"),
                        input(virkailija.ammatillinenTutkintonimike, "Hitsaajan perustutkinto"),
                        input(virkailija.ammatillinenTutkinnonLaajuus, "120"),
                        input(virkailija.ammatillinenOppilaitos, "Ammattikoulu X")
                    ]);
                })
                .then(click(
                    virkailija.kkTutkintoSuoritettu(false),
                    virkailija.addAvoinCheckbox))
                .then(function() {
                    return Q.all([
                        input(virkailija.avoinAla(), "Avoin ala 1"),
                        input(virkailija.avoinKokonaisuus(), "Avoin kokonaisuus 1"),
                        input(virkailija.avoinLaajuus(), "Avoin laajuus 1"),
                        input(virkailija.avoinKorkeakoulu(), "Avoin Korkeakoulu 1")
                    ]);
                })
                .then(click(virkailija.addSecondAvoinLink))
                .then(function() {
                    return Q.all([
                        input(virkailija.avoinAla(2), "Avoin ala 2"),
                        input(virkailija.avoinKokonaisuus(2), "Avoin kokonaisuus 2"),
                        input(virkailija.avoinLaajuus(2), "Avoin laajuus 2"),
                        input(virkailija.avoinKorkeakoulu(2), "Avoin Korkeakoulu 2")
                    ]);
                })
                .then(click(virkailija.saveButton))
                .then(exists(virkailija.ammatilliset))
                .then(done, done);
        });

        it('mahdollistaa uuden ammatillisen pohjakoulutuksen lisäämisen kaksi kertaa peräkkäin', function (done) {
            expect(answerForQuestion('pohjakoulutus_am_vuosi')).to.equal('2000');
            expect(answerForQuestion('pohjakoulutus_avoin_ala')).to.equal('Avoin ala 1');
            expect(answerForQuestion('pohjakoulutus_avoin_ala2')).to.equal('Avoin ala 2');
            done();
        });
    });
});
