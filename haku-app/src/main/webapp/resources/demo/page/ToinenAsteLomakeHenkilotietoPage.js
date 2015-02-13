function ToinenAsteLomakeHenkilotietoPage() {
    var selectors = initSelectors({
        sukunimi: "input#Sukunimi",
        etunimet: "input#Etunimet",
        kutsumanimi: "input#Kutsumanimi",
        hetu: "input#Henkilotunnus",
        sukupuoli: "input#sukupuoli",
        sukupuoliLabel: "span#sex",
        lahiosoite: "input#lahiosoite",
        postinumero: "input#Postinumero",
        kotikunta: "select#kotikunta"
    });

    selectors.kaksoiskansalaisuus = function(onKaksoiskansalaisuus) {
        return function() {
            S("input#onkosinullakaksoiskansallisuus[value="+onKaksoiskansalaisuus+"]")
                .prop("checked", true);
        }
    }

    selectors.start = function() {
        return logout().then(function() {
            return openPage("/haku-app/lomakkeenhallinta/1.2.246.562.5.50476818906", function() {
                return S("form#form-henkilotiedot").first().is(':visible')
            })()
        });
    }

    return selectors;
}
