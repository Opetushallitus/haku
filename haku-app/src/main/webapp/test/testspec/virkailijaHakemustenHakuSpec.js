describe("Virkailija, hakemusten haku", function() {
    before(seqDone(
        login('officer', 'officer')
    ));
    describe("Harkinnanvaraiset", function() {
        describe('hakua ei valittu', function() {
            it('harkinnanvaraiset disabloitu', function() {
                expect(virkailija.searchHarkinnanvaraiset().attr("disabled")).to.exist
            });
            describe("valitse haku", function() {
                beforeEach(seq(
                    select(virkailija.hakukausi, "kausi_k"),
                    input(virkailija.hakukausiVuosi, "2015"),
                    select(virkailija.searchSelectHaku, "1.2.246.562.29.95390561488")
                ));
                it('harkinnanvaraiset enabloitu', function() {
                    expect(virkailija.searchHarkinnanvaraiset().attr("disabled")).to.equal(undefined)
                });
            });
        })
    });
    describe("Hakumaksu", function() {
        beforeEach(seq(
            select(virkailija.searchSelectHaku, "")
        ));
        describe('hakua ei valittu', function() {
            it('maksun tila -valikkoa ei näytetä', function() {
                expect(virkailija.searchMaksuntilaSelect().is(':visible')).to.equal(false)
            });
            describe("valitse haku", function() {
                beforeEach(seq(
                    select(virkailija.hakukausi, "kausi_k"),
                    input(virkailija.hakukausiVuosi, "2015"),
                    select(virkailija.searchSelectHaku, "1.2.246.562.29.95390561488")
                ));
                it('maksun tila -valikko näytetään', function() {
                    expect(virkailija.searchMaksuntilaSelect().is(':visible')).to.equal(true)
                });
                describe("valitse maksun tila", function() {
                    beforeEach(seq(
                        select(virkailija.searchMaksuntila, "NOTIFIED")
                    ));
                    it('maksun tila -valikossa on valittuna Odottaa', function() {
                        assertText(virkailija.searchMaksuntilaSelected, 'Odottaa')
                    });
                    describe("poista haun valinta", function() {
                        beforeEach(seq(
                            select(virkailija.searchSelectHaku, "")
                        ));
                        it('maksun tila -valikko katoaa näkyvistä', function() {
                            expect(virkailija.searchMaksuntilaSelect().is(':visible')).to.equal(false)
                        });
                        describe("valittaessa haku uudelleen", function() {
                            beforeEach(seq(
                                select(virkailija.searchSelectHaku, "1.2.246.562.29.95390561488")
                            ));
                            it("maksun tila -valikossa on valittuna tyhjä arvo", function() {
                                assertValue(virkailija.searchMaksuntilaSelected, '')
                            })
                        })
                    });
                    describe("tyhjennä-painiketta painettaessa", function() {
                        beforeEach(seq(
                            click(virkailija.searchReset)
                        ));
                        it('maksun tila -valikko katoaa näkyvistä', function() {
                            expect(virkailija.searchMaksuntilaSelect().is(':visible')).to.equal(false)
                        })
                    })
                });
            });
        });
    })
});

