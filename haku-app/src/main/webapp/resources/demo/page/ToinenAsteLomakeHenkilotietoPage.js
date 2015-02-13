function ToinenAsteLomakeHenkilotietoPage() {


    var pageFunctions = {

        sukunimi : function() {
            return S("input#Sukunimi");
        },
        etunimet : function() {
            return S("input#Etunimet");
        },
        kutsumanimi : function() {
            return S("input#Kutsumanimi");
        },
        kaksoiskansalaisuus : function(onKaksoiskansalaisuus) {
            return function() {
                S("input#onkosinullakaksoiskansallisuus[value="+onKaksoiskansalaisuus+"]")
                    .prop("checked", true);
            }
        },
        hetu : function() {
            return S("input#Henkilotunnus");
        },
        sukupuoli : function() {
            return S("input#sukupuoli");
        },
        sukupuoliLabel : function() {
            return S("span#sex");
        },
        lahiosoite : function() {
            return S("input#lahiosoite");
        },
        postinumero : function() {
            return S("input#Postinumero");
        },
        kotikunta : function() {
            return S("select#kotikunta");
        },

        start: function() {
            return logout().then(function() {
                return openPage("/haku-app/lomakkeenhallinta/1.2.246.562.5.50476818906", function() {
                    return S("form#form-henkilotiedot").first().is(':visible')
                })()
            });
        }
    };

    return pageFunctions;
}
