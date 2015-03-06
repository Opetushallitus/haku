describe('KK-hakemus', function () {
    function answerForQuestion(name) {
        return S('td:has(a[name=' + name + '])').next().html()
    }
    function previewPagePath() {
        return"/haku-app/virkailija/hakemus/" + virkailija.hakemusOid().text() + "/print/view"
    }
    before(seqDone(
        logout,
        function() {
            return openPage("/haku-app/lomakkeenhallinta/1.2.246.562.29.173465377510", function() {
                return S("form#form-henkilotiedot").first().is(':visible')
            })()
        }
    ));

    describe("virkailijan näkymä", function() {
        before(seqDone(
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
            click(virkailija.submitConfirm),
            exists(virkailija.hakemusOid)
        ));

        describe("syötä hakemus alkutilanne", function() {
            it('avautuu', function () {
            });
        });

        describe("kahden ammatillisen pohjakoulutuksen lisäys", function() {
            before(seqDone(
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
                click(virkailija.saveKoulutusTaustaButton),
                visible(virkailija.editKoulutusTaustaButton)
            ));

            describe("lisäämisen jälkeen", function() {
                it("vastaukset näkyvät", function () {
                    expect(answerForQuestion('pohjakoulutus_am_vuosi')).to.equal('2000');
                    expect(answerForQuestion('pohjakoulutus_avoin_ala')).to.equal('Avoin ala 1');
                    expect(answerForQuestion('pohjakoulutus_avoin_ala2')).to.equal('Avoin ala 2');
                });
            });

            describe("kahden hakutoiveen lisäys", function() {
                var oulunYliopisto = "Oulun yliopisto, Humanistinen tiedekunta";
                var novia = "Yrkeshögskolan Novia, Raasepori";
                before(seqDone(
                    click(virkailija.editHakutoiveetButton),
                    valitseKoulutus(1, oulunYliopisto, "Aate- ja oppihistoria, humanististen tieteiden kandidaatti ja filosofian maisteri"),
                    valitseKoulutus(2, novia, "Agrolog (YH)/Miljöplanerare (YH)/Skogsbruksingenjör (YH), dagstudier"),
                    click(virkailija.saveHakutoiveetButton),
                    visible(virkailija.editHakutoiveetButton)
                ));

                describe("lisäämisen jälkeen", function() {
                    it("toiveet näkyvät", function () {
                        expect(answerForQuestion('preference1')).to.equal(oulunYliopisto);
                        expect(answerForQuestion('preference2')).to.equal(novia);
                    });
                });

                describe("esikatselussa", function() {
                    before(seqDone(
                        openPage(previewPagePath, function() {
                            return S("#applicationAttachments").first().is(':visible');
                        })
                    ));
                    it('pyydetään sekä avoimen kk:n että ammatillisen koulutuksen todistusten liitteiden toimittaminen molempiin kouluihin', function () {
                        var avoinKkTodistuskopio = 'Todistuskopio tai ote avoimen korkeakoulun opintosuorituksista';
                        var amTodistuskopio = 'Todistuskopio ammatillisesta perustutkinnosta';
                        expect(virkailija.previewLiitteet()).to.have.length(4);
                        expect(virkailija.previewLiitteet().find("td:first:contains(" + oulunYliopisto + "):contains(" + avoinKkTodistuskopio + ")").length).to.equal(1);
                        expect(virkailija.previewLiitteet().find("td:first:contains(" + oulunYliopisto + "):contains(" + amTodistuskopio + ")").length).to.equal(1);
                        expect(virkailija.previewLiitteet().find("td:first:contains(" + novia + "):contains(" + avoinKkTodistuskopio + ")").length).to.equal(1);
                        expect(virkailija.previewLiitteet().find("td:first:contains(" + novia + "):contains(" + amTodistuskopio + ")").length).to.equal(1);
                    });
                });
            });

        });
    });
});
