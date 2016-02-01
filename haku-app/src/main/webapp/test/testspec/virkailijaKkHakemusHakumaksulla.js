describe("KK-hakemus hakumaksulla", function() {
    var hakuOid = "1.2.246.562.29.75203638285";
    var hakemusId;

    describe("virkailijan näkymä", function() {
        before(seqDone(
            login('officer', 'officer'),
            click(virkailija.createApplicationButton),
            input(virkailija.selectHaku, hakuOid),
            click(virkailija.submitConfirm),
            exists(virkailija.hakemusOid),
            function() {
                hakemusId = virkailija.hakemusOid().text();
            }
        ));

        describe("syötä hakemus alkutilanne", function() {
            it("avautuu", function () {

            });
        });

        describe("ulkomaisen ei-eta-maassa suoritetun kk-kelpoisuuden antavan koulutuksen lisääminen", function() {
            before(seqDone(
                click(virkailija.editKoulutusTaustaButton(hakuOid)),
                wait.forMilliseconds(1000),
                click(virkailija.addUlkomainenKkKelpoisuus),
                input(
                    virkailija.ulkomainenKkKelpoisuusVuosi, "2000",
                    virkailija.ulkomainenKkKelpoisuusNimike, "maisteri",
                    virkailija.ulkomainenKkKelpoisuusOppilaitos, "foo-koulu",
                    virkailija.ulkomainenKkKelpoisuusMaa, "XXX",
                    virkailija.ulkomainenKkKelpoisuusMuuMaa, "foo-maa"),
                click(virkailija.kkTutkintoSuoritettu(false)),
                click(virkailija.saveKoulutusTaustaButton),
                visible(virkailija.editKoulutusTaustaButton(hakuOid))
            ));

            describe("lisäämisen jälkeen", function() {
                it("vastaukset näkyvät", function () {
                    expect(answerForQuestion('pohjakoulutus_ulk_vuosi')).to.equal('2000');
                    expect(answerForQuestion('pohjakoulutus_ulk_nimike')).to.equal('maisteri');
                    expect(answerForQuestion('pohjakoulutus_ulk_oppilaitos')).to.equal('foo-koulu');
                });
            });
        });

        describe("hakutoiveiden lisäys", function() {
            describe("lisättäessä hakumaksun vaativa hakutoive", function() {
                before(seqDone(
                    click(virkailija.editHakutoiveetButton(hakuOid)),
                    tyhjennaHakutoiveet(5),
                    aaltoTekniikanKandiJaDi(1),
                    click(virkailija.saveHakutoiveetButton),
                    visible(virkailija.notes)
                ));

                describe("lisäämisen jälkeen", function() {
                    it("toiveet näkyvät", function () {
                        expect(answerForQuestion('preference1')).to.equal(aalto);
                        // TODO jatka plz
                    });
                });
            })
        });

        describe("palattaessa pohjakoulutusten muokkaukseen", function() {
            before(seqDone(
                click(virkailija.editKoulutusTaustaButton(hakuOid)),
                visible(virkailija.saveKoulutusTaustaButton)
            ));

            it("maksunotifikaatio näkyy", function() {
                expect(virkailija.koulutustaustaPaymentNotification().is(':visible')).to.equal(true)
            });

            describe("valittaessa Suomessa suoritettu ylioppilastutkinto ja/tai lukion oppimäärä", function() {
                before(seqDone(
                    click(virkailija.pohjakoulutusYo),
                    visible(virkailija.pohjakoulutusYoVuosi)
                ));

                it("maksunotifikaatio katoaa", function() {
                    expect(virkailija.koulutustaustaPaymentNotification().is(':visible')).to.equal(false)
                })
            })
        });
    });
});