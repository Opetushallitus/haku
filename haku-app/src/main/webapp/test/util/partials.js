function henkilotiedotTestikaes(lomake) {
    return seq(
        input(
            lomake.sukunimi, "Testikäs",
            lomake.etunimet, "Asia Kas",
            lomake.kutsumanimi, "Asia",
            lomake.hetu, "171175-830Y"),
        wait.until(function() {
            return S("input#sukupuoli").length > 0;
        }),
        click(lomake.kaksoiskansalaisuus(false)),
        function() {
            expect(lomake.sukupuoli().val()).to.equal('2');
        },
        input(
            lomake.asuinmaa, 'FIN',
            lomake.lahiosoite, "Testikatu 4",
            lomake.postinumero, "00100",
            lomake.kotikunta, "janakkala"));
}
