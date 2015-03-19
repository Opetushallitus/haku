describe("Virkailija, hakemusten haku", function() {
    before(seqDone(
        function() {
            return openPage("/haku-app/lomakkeenhallinta/1.2.246.562.29.95390561488", function() {
                return S("form#form-henkilotiedot").first().is(':visible')
            })()
        },
        login('officer', 'officer')
    ));
    describe("Harkinnanvaraiset", function() {
        describe('hakua ei valittu', function() {
            it('harkinnanvaraiset disabloitu', function() {
                expect(virkailija.searchHarkinnanvaraiset().attr("disabled")).to.exist
            });
            describe("valitse haku", function() {
                beforeEach(
                    select(virkailija.searchSelectHaku, "1.2.246.562.29.95390561488")
                );
                it('harkinnanvaraiset enabloitu', function() {
                    expect(virkailija.searchHarkinnanvaraiset().attr("disabled")).to.undefined
                });
            });
        })
    })
});

