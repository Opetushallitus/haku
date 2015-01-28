function KkHakemusPage() {
    var lomakkeenhallintaPage = openPage("/haku-app/lomakkeenhallinta/1.2.246.562.29.173465377510", function() {
        return S("#form-henkilotiedot").first().is(':visible')
    });
    var hakemusPage = openPage("/haku-app/virkailija/hakemus", function() {
        return S("#create-application").first().is(':visible')
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
        saveButton: function() {
            return S("button.save[value=koulutustausta]").first()
        },
        createApplication: function() {
            return lomakkeenhallintaPage()
                .then(hakemusPage)
                .then(wait.until(pageFunctions.createApplicationButton().isEnabled))
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