function ToinenAsteLomakeHenkilotietoPage() {

    var lomakePage = openPage("/haku-app/lomakkeenhallinta/1.2.246.562.5.50476818906", function() {
                        return S("form#form-henkilotiedot").first().is(':visible')
                       });


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
            S("input#onkosinullakaksoiskansallisuus[value="+onKaksoiskansalaisuus+"]")
                .prop("checked", true);
        },
        hetu : function() {
            return S("input#Henkilotunnus");
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
            openPage("/haku-app/user/logout", function() {
                return S("body").is(":visible");
            });
            return lomakePage().then(wait.until(pageFunctions.sukunimi().is(":visible")));
        }
    };

    return pageFunctions;
}