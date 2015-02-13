function KkHakemusPage() {
    var hakemusPage = openPage("/haku-app/virkailija/hakemus", function() {
        return testFrame().document.getElementById('loginForm') !== null;
    });

    var pageFunctions = {
        createApplicationButton: function () {
            return Button(function() {
                return S("#create-application").first()
            })
        },
        submitConfirm: function() {
            return Button(function() {
                return S("#submit_confirm").first()
            })
        },
        selectHaku: function () {
            return S("#asSelect").first()
        },
        editKoulutusTaustaButton: function() {
            return S("a[href*='1.2.246.562.29.173465377510/koulutustausta']").first().children("button")
        },
        addAmmatillinenCheckbox: function() {
            return S("input#pohjakoulutus_am").first()
        },
        addAvoinCheckbox: function() {
            return S("input#pohjakoulutus_avoin").first()
        },
        ammatilliset: function () {
            var el = S("a[name=pohjakoulutus_am]").first();
            if (el.get(0)) el.get(0).scrollIntoView();
            return el;
        },
        addSecondAmmatillinenLink: function() {
            return S("a#addAmmatillinenRule2-link").first()
        },
        ammatillinenSuoritusVuosi: function() {
            return S("input#pohjakoulutus_am_vuosi").first()
        },
        ammatillinenTutkintonimike: function() {
            return S("input#pohjakoulutus_am_nimike").first()
        },
        ammatillinenTutkinnonLaajuus: function() {
            return S("input#pohjakoulutus_am_laajuus").first()
        },
        ammatillinenOppilaitos: function() {
            return S("input#pohjakoulutus_am_oppilaitos").first()
        },
        ammatillinenNayttotutkinto: function() {
            return S("input[name=pohjakoulutus_am_nayttotutkintona]")
        },
        ammatillinenSuoritusVuosi2: function() {
            return S("input#pohjakoulutus_am_vuosi2").first()
        },
        ammatillinenTutkintonimike2: function() {
            return S("input#pohjakoulutus_am_nimike2").first()
        },
        ammatillinenTutkinnonLaajuus2: function() {
            return S("input#pohjakoulutus_am_laajuus2").first()
        },
        ammatillinenOppilaitos2: function() {
            return S("input#pohjakoulutus_am_oppilaitos2").first()
        },
        ammatillinenOppilaitos2Text: function() {
            return S("td:contains(SLK)").first()
        },
        ammatillinenNayttotutkinto2: function() {
            return S("input[name=pohjakoulutus_am_nayttotutkintona2]")
        },
        kkTutkintoSuoritettu: function() {
            return S("input[name=suoritusoikeus_tai_aiempi_tutkinto]")
        },
        avoinAla: function(index) {
            index = index ? index : '';
            return S("input#pohjakoulutus_avoin_ala"+index);
        },
        avoinKokonaisuus: function(index) {
            index = index ? index : '';
            return S("input#pohjakoulutus_avoin_kokonaisuus"+index);
        },
        avoinLaajuus: function(index) {
            index = index ? index : '';
            return S("input#pohjakoulutus_avoin_laajuus"+index);
        },
        avoinKorkeakoulu: function(index) {
            index = index ? index : '';
            return S("input#pohjakoulutus_avoin_korkeakoulu"+index);
        },
        addSecondAvoinLink: function() {
            return S("a#addAvoinTutkintoRule2-link").first()
        },
        saveButton: function() {
            return S("button.save[value=koulutustausta]").first()
        },
        answerForQuestion: function(name) {
            return S('td:has(a[name='+name+'])').next().html()
        },
        createApplication: function() {
            return logout().then(function() {
                return openPage("/haku-app/lomakkeenhallinta/1.2.246.562.29.173465377510", function() {
                    return S("form#form-henkilotiedot").first().is(':visible')
                })()})
                .then(function() { return hakemusPage(); })
                .then(function() {
                    function input(name) {
                        return testFrame().document.getElementsByName(name)[0];
                    }
                    input("j_username").value = "officer";
                    input("j_password").value = "officer";
                    input("login").click();
                })
                .then(wait.until(pageFunctions.createApplicationButton().isVisible))
                .then(pageFunctions.createApplicationButton().scrollIntoView)
                .then(pageFunctions.createApplicationButton().click)
                .then(wait.until(function() {return pageFunctions.selectHaku().is(':visible')}))
                .then(function(){
                    pageFunctions.selectHaku().val("1.2.246.562.29.173465377510");
                    pageFunctions.submitConfirm().click()
                })
        }
    };

    return pageFunctions;
}
