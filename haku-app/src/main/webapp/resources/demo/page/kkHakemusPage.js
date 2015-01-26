function KkHakemusPage() {
    var lomakkeenhallintaPage = openPage("/haku-app/lomakkeenhallinta", function() {
        return $("#testframe").get(0).contentWindow.document.getElementById("1.2.246.562.29.173465377510")
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
        kkTutkintoSuoritettu: function() {
            return S("input[name=suoritusoikeus_tai_aiempi_tutkinto]")
        },
        saveButton: function() {
            return Button(function() { return S("button.save").first() })
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