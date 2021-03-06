var hakutoiveet = [],
  hakutoiveetCache = [];

var config = {
    hakukelpoinen: "ELIGIBLE",
    eiHakukelpoinen: "INELIGIBLE",
    puutteellinen: "INADEQUATE",
    kelpoisuusTarkistamatta: "NOT_CHECKED",
    automaattisestiHyvaksytty: "AUTOMATICALLY_CHECKED_ELIGIBLE",
    liiteSaapunut: "ARRIVED",
    liiteEiSaapunut: "NOT_RECEIVED",
    liiteEiTarkistettu: "NOT_CHECKED",
    liiteTarkistettu: "CHECKED",
    tietolahdeUnknown: "UNKNOWN",
    tietolahdeRekisteri: "REGISTER",
    showlogs: true
};
var kjal = {
    /**
     * luodaan kelpoisuus ja liitteet välihdelle taustajärjestelmästä saadun
     * datan perusteella lomake hakukelpoisuuden ja liitteiden tilojen tallentamiseksi
     */
    populateForm: function () {
        this.LOGS('Kelpoisuus ja liitteet saatu data:', hakutoiveet);
        this.LOGS('populoidaan kelpoisuus ja liittee välilehti: ', window.location.href);
        for (var hktindx in hakutoiveet) {

            var liitteet = hakutoiveet[hktindx].attachments,
                ind = parseInt(hktindx) + 1,
                $form =
                    "<tr>"
                        + "<td style=\"font-weight: bold;\"> Hakukelpoisuus </td>"
                        + "<td>"
                        + "<select class=\"width-12-11\" id=\"hakukelpoisuus-select\" onchange=\"kjal.hakuKelpoisuus(" + ind + ", false)\">"
                        + "<option value=\"NOT_CHECKED\">Kelpoisuus tarkistamatta</option>"
                        + "<option value=\"AUTOMATICALLY_CHECKED_ELIGIBLE\">Kelpoisuus hyväksytty automaattisesti</option>"
                        + "<option value=\"ELIGIBLE\">Hakukelpoinen</option>"
                        + "<option value=\"INELIGIBLE\">Ei hakukelpoinen</option>"
                        + "<option value=\"INADEQUATE\">Puutteelinen</option>"
                        + "</select>"
                        + "</td>"
                        + "<td>"
                        + "<select class=\"width-12-11\" id=\"hakukelpoisuus-tietolahde\" disabled onchange=\"kjal.tietoLahde(" + ind + ")\">"
                        + "<option value=\"\" default selected disabled>valitse tietolähde</option>"
                        + "<option value=\"LEARNING_PROVIDER\">Oppilaitoksen toimittama tieto</option>"
                        + "<option value=\"ORIGINAL_DIPLOMA\">Alkuperäinen todistus</option>"
                        + "<option value=\"OFFICIALLY_AUTHENTICATED_COPY\">Virallinen oikeaksi todistettu kopio</option>"
                        + "<option value=\"AUTHENTICATED_COPY\">Oikeaksi todistettu kopio</option>"
                        + "<option value=\"COPY\">Kopio</option>"
                        + "<option value=\"REGISTER\">Rekisteri</option>"
                        + "</select>"
                        + "</td>"
                        + "</tr>"

                        + "<tr>"
                        + "<td>Lisätietoja</td>"
                        + "<td colspan=\"2\">"
                        + "<textarea id=\"hylkaamisenperuste\" rows=\"4\" class=\"width-12-11\" onblur=\"kjal.hylkaamisenSyy(" + ind + ")\"></textarea>"
                        + "</td>"
                        + "</tr>";

            if (liitteet !== undefined) {
               $form +="<tr>"
                    + "<td id=\"liitteidenmaara\" style=\"font-weight: bold\">Liitteiden määrä " + liitteet.length + " kpl </td>"
                    + "<td colspan=\"2\"></td>"
                + "</tr>";
            }

            $form +="<tr>"
              + "<td id=\"maksuvelvollisuus\" style=\"font-weight: bold\">Maksuvelvollisuus</td>"
              + "<td>"
              + "<select class=\"width-12-11\" id=\"maksuvelvollisuus-select\" onchange=\"kjal.maksuvelvollisuus(" + ind + ", false)\">"
              + "<option value=\"NOT_CHECKED\">Ei tarkistettu</option>"
              + "<option value=\"REQUIRED\">Maksuvelvollinen</option>"
              + "<option value=\"NOT_REQUIRED\">Ei maksuvelvollinen</option>"
              + "</select>"
              + "</td>"
              + "<td>"
              + "</td>"
              + "</tr>";



            for (var trs in liitteet){
                var liiteDesc = liitteet[trs].description;
                $form += "<tr class=\"liitteesaapuneet-" + ind +"\" id=\"liitesaapunut-tr-" +ind+ "-" + trs +"\" >"
                    + "<td class=\"width-25\"><input type=\"checkbox\" \" onchange=\"kjal.validateKaikkiLiitteetSaapuneet(" + ind + "," + trs + ")\" > "
                    + "Liite saapunut: "+ liitteet[trs].name + " " + liitteet[trs].header;
                    if (liiteDesc !== undefined && liiteDesc.length > 0) {
                        $form += "<a href=\"#\" title=\"" + liiteDesc + " \" class=\"helplink\"></a>";
                    }

                    $form += "</td> "

                    + "<td>"
                    + "<select class=\"width-12-11\" id=\"select-saapunut-" +ind+ "-" + trs + "\" disabled onchange=\"kjal.saapumisTila("+ ind +","+ trs +")\">"
                    + "<option value=\"ARRIVED\">Saapunut</option>"
                    + "<option value=\"ARRIVED_LATE\">Saapunut myöhässä</option>"
                    + "<option value=\"NOT_RECEIVED\" default selected disabled >Ei saapunut</option>"
                    + "</select>"
                    + "</td>"

                    + "<td>"
                    + "<select class=\"width-12-11\" id=\"select-tarkistettu-" +ind+ "-" + trs + "\" disabled onchange=\"kjal.liitteenTila("+ ind +","+ trs +")\">"
                    + "<option value=\"NOT_CHECKED\" default selected>Ei tarkistettu</option>"
                    + "<option value=\"COMPLEMENT_REQUESTED\">Odottaa täydennystä</option>"
                    + "<option value=\"INADEQUATE\">Puutteellinen</option>"
                    + "<option value=\"CHECKED\">Tarkistettu</option>"
                    + "<option value=\"UNNECESSARY\">Tarpeeton</option>"
                    + "</select>"
                    + "</td>"
                    + "</tr>";
            }
            $('#liitteet-table-' + ind).append($form);
        }
        this.asetaHakuKelpoisuudetJaLiitteidenTilat();
    },
    /**
     * Asettaa sivun latautuessa taustajärjestelmästä saadut
     * hakutoiveen ja liitteiden tilat UI:lle.
     * @param ind hakutoiveen index numero
     */
    asetaHakuKelpoisuudetJaLiitteidenTilat: function () {
        this.LOGS('asetaHakuKelpoisuudetJaLiitteidenTilat()');
        for (var ht in hakutoiveet) {
            this.LOGS('########## hakutoive ######## array index:', ht);
            var ind = parseInt(ht) +1;
            $('#liitteet-table-' + ind + ' #maksuvelvollisuus-select').val(hakutoiveet[ht].maksuvelvollisuus);
            $('#liitteet-table-' + ind + ' #hakukelpoisuus-select').val(hakutoiveet[ht].status);
            this.LOGS('hakutoiveen tila: ',  hakutoiveet[ht].status);
            if(hakutoiveet[ht].source !== config.tietolahdeUnknown ) {
                this.LOGS('hakutoiveen tietolähde: ',  hakutoiveet[ht].source);
                $('#liitteet-table-' + ind + ' #hakukelpoisuus-tietolahde').val(hakutoiveet[ht].source);
            }
            $('#liitteet-table-' + ind + ' #hylkaamisenperuste').val(_.str.unescapeHTML(hakutoiveet[ht].rejectionBasis));
            if(hakutoiveet[ht].preferencesChecked === 'false' ){
                $('#kaikki-tiedot-tarkistettu-' + ind).attr('checked', false);
            } else {
                $('#kaikki-tiedot-tarkistettu-' + ind).attr('checked', true);
            }
            this.asetaHakuKelpoisuus(ind, true);
            for(var trs in hakutoiveet[ht].attachments){
                this.LOGS('Liitteen saapumistila: ', hakutoiveet[ht].attachments[trs].receptionStatus);
                $('#select-saapunut-' +ind + '-' + trs).val(hakutoiveet[ht].attachments[trs].receptionStatus)
                $('#select-tarkistettu-' +ind + '-' + trs).val(hakutoiveet[ht].attachments[trs].processingStatus);
                if (hakutoiveet[ht].attachments[trs].receptionStatus !== config.liiteEiSaapunut){
                    $('#liitesaapunut-tr-' +ind + '-' + trs + ' [type=checkbox]').attr('checked', 'true');
                    $('#select-saapunut-' +ind + '-' + trs).removeAttr('disabled');
                    $('#select-tarkistettu-' +ind + '-' + trs).removeAttr('disabled');
                }

            }
            this.tarkistaKaikkiLiitteetSaapuneet(ht);
            this.kaikkiLiitteetTarkistettu(ht);
        }
    },
    /**
     * Asetetaan hakukelpoisuus sivun latauksen yhteydessä
     * @param indx hakutoiveen index numero
     */
    asetaHakuKelpoisuus: function (indx, onload) {
        this.LOGS('hakuKelpoisuus()', ' -> in tila: ',hakutoiveet[indx-1].status);
        if(!onload){
            hakutoiveet[indx-1].status = $('#liitteet-table-' + indx + ' #hakukelpoisuus-select').val();
        }
        if (hakutoiveet[indx-1].status === config.hakukelpoinen) {
            $('#liitteet-table-' + indx + ' #hakukelpoisuus-tietolahde').removeAttr('disabled');
        } else if (hakutoiveet[indx-1].status === config.eiHakukelpoinen) {
            $('#liitteet-table-' + indx + ' #hakukelpoisuus-tietolahde').attr('disabled', 'true');
            hakutoiveet[indx-1].source = config.tietolahdeUnknown;
            $('#liitteet-table-' + indx + ' #hakukelpoisuus-tietolahde').val('');
        } else if (hakutoiveet[indx-1].status === config.automaattisestiHyvaksytty && hakutoiveet[indx-1].source === config.tietolahdeRekisteri) {
            $('#liitteet-table-' + indx + ' #hakukelpoisuus-tietolahde').attr('disabled', 'true');
        } else if (hakutoiveet[indx-1].status === config.kelpoisuusTarkistamatta && hakutoiveet[indx-1].source === config.tietolahdeRekisteri) {
            $('#liitteet-table-' + indx + ' #hakukelpoisuus-tietolahde').attr('disabled', 'true');
        } else {
            $('#liitteet-table-' + indx + ' #hakukelpoisuus-tietolahde').attr('disabled', 'true');
            hakutoiveet[indx-1].source = config.tietolahdeUnknown;
            $('#liitteet-table-' + indx + ' #hakukelpoisuus-tietolahde').val('');
        }
        this.LOGS('hakuKelpoisuus()', ' out -> tila: ', hakutoiveet[indx-1].status);
        this.tarkistaHakutoiveValmis(indx);
    },
    /**
     * Asetetaan hakukelpoisuus käyttöliittymästä
     * @param indx hakutoiveen index numero
     */
    hakuKelpoisuus: function (indx, onload) {
        this.LOGS('hakuKelpoisuus()', ' -> in tila: ',hakutoiveet[indx-1].status);
        if(!onload){
            hakutoiveet[indx-1].status = $('#liitteet-table-' + indx + ' #hakukelpoisuus-select').val();
        }
        if (hakutoiveet[indx-1].status === config.hakukelpoinen) {
            $('#liitteet-table-' + indx + ' #hakukelpoisuus-tietolahde').removeAttr('disabled');
            hakutoiveet[indx-1].source = config.tietolahdeUnknown;
            $('#liitteet-table-' + indx + ' #hakukelpoisuus-tietolahde').val('');
        } else if (hakutoiveet[indx-1].status === config.eiHakukelpoinen) {
            $('#liitteet-table-' + indx + ' #hakukelpoisuus-tietolahde').attr('disabled', 'true');
            hakutoiveet[indx-1].source = config.tietolahdeUnknown;
        } else {
            $('#liitteet-table-' + indx + ' #hakukelpoisuus-tietolahde').attr('disabled', 'true');
            hakutoiveet[indx-1].source = config.tietolahdeUnknown;
            $('#liitteet-table-' + indx + ' #hakukelpoisuus-tietolahde').val('');
        }
        this.LOGS('hakuKelpoisuus()', ' out -> tila: ', hakutoiveet[indx-1].status);
        this.tarkistaHakutoiveValmis(indx);
    },
    maksuvelvollisuus: function (indx, onload) {
        this.LOGS('maksuvelvollisuus()', ' -> in tila: ',hakutoiveet[indx-1].maksuvelvollisuus);
        if(!onload){
            hakutoiveet[indx-1].maksuvelvollisuus = $('#liitteet-table-' + indx + ' #maksuvelvollisuus-select').val();
        }
        this.LOGS('maksuvelvollisuus()', ' out -> tila: ', hakutoiveet[indx-1].maksuvelvollisuus);
        this.tarkistaHakutoiveValmis(indx);
    },
    /**
     * Asetetaan hakukelpoisuuteen liittyvän tarkastuksen tietolähde
     * @param indx hakutoiveen index numero
     */
    tietoLahde: function (indx) {
        hakutoiveet[indx-1].source = $('#liitteet-table-' + indx + ' #hakukelpoisuus-tietolahde').val();
        this.tarkistaHakutoiveValmis(indx);
    },
    /**
     * Asetetaan hakukelpoisuuteen liittyvä hylkäämisen syy
     * @param indx hakutoiveen index numero
     */
    hylkaamisenSyy: function (indx) {
        hakutoiveet[indx-1].rejectionBasis = _.str.escapeHTML($('#liitteet-table-' + indx + ' #hylkaamisenperuste').val());
        this.tarkistaHakutoiveValmis(indx);
    },
    /**
     * Asetetaan kaikki liitteet saapuneet tilaan käyttöliittymästä
     * @param indx hakutoiveen index numero
     */
    asetaKaikkiLiitteetSaapuneet: function (indx) {
        this.LOGS('asetaKaikkiLiitteetSaapuneet()');
        var hakutoive = parseInt(indx) - 1;
        for(var i in hakutoiveet[hakutoive].attachments){
            if (hakutoiveet[hakutoive].attachments[i].receptionStatus === config.liiteEiSaapunut) {
                $('#select-saapunut-' + indx +'-' + i).removeAttr('disabled');
                $('#select-saapunut-' + indx +'-' + i).val(config.liiteSaapunut);
                $('#select-tarkistettu-' + indx +'-' + i).removeAttr('disabled');
                $('#select-tarkistettu-' + indx +'-' + i).val(config.liiteEiTarkistettu);
                hakutoiveet[hakutoive].attachments[i].receptionStatus = config.liiteSaapunut;
                hakutoiveet[hakutoive].attachments[i].processingStatus = config.liiteEiTarkistettu;
            }
        }
        $('#kaikkiliitteet-'+ indx).css('display', '');
        this.disableBtnKaikkiLiitteetSaapuneet(indx);
        this.enableBtnKaikkiLiitteetTarkastettu(indx);
        this.ryhmaanKuuluvienKysymystenTilanAsetus(indx);
        $('#liitteet-table-' + indx +' *:checkbox').prop('checked', true);
        this.tarkistaHakutoiveValmis(indx);
    },
    /**
     * Asettaa kaikissa hakutoiveissa olevien samaan ryhmään kuuluvine kysymysten tilan
     * samaan kuin muokatussa hakukohtessa, kun käyttäjä valitsee
     * napin "Kaikki liitteet saapuneet"
     * @param indx hakutoiveen index nro.
     */
    ryhmaanKuuluvienKysymystenTilanAsetus: function (indx) {
        this.LOGS('ryhmaanKuuluvienKysymystenTilanAsetus()', indx);
        var aoGroupIds = [];
        for (var htAoG in hakutoiveet[indx-1].attachments) {
            if(hakutoiveet[indx-1].attachments[htAoG].aoGroupId !== ''){
                aoGroupIds.push(hakutoiveet[indx-1].attachments[htAoG].aoGroupId);
            }
        }

        for (var g in hakutoiveet){
            for (var t in hakutoiveet[g].attachments) {
                for (var htg in aoGroupIds){
                    if(hakutoiveet[g].attachments[t].aoGroupId === aoGroupIds[htg] ) {
                        var ind = parseInt(g) + 1;
                        hakutoiveet[g].attachments[t].receptionStatus = config.liiteSaapunut;
                        $('#liitesaapunut-tr-' +ind + '-' + t + ' [type=checkbox]').attr('checked', 'true');
                        $('#select-saapunut-' + ind +'-' + t).val(config.liiteSaapunut);
                        $('#select-saapunut-' + ind +'-' + t).removeAttr('disabled');
                        $('#select-tarkistettu-' + ind +'-' + t).removeAttr('disabled');
                        $('#select-tarkistettu-' + ind +'-' + t).val(config.liiteEiTarkistettu);
                        hakutoiveet[g].attachments[t].receptionStatus = config.liiteSaapunut;
                        hakutoiveet[g].attachments[t].processingStatus = config.liiteEiTarkistettu;
                        if (this.kaikkiLiitteetSaapuneetTilassa(g)) {
                            this.disableBtnKaikkiLiitteetSaapuneet(g);
                        }
                        this.kaikkiLiitteetTarkistettu(g);
                    }
                }
            }
        }
    },
    /**
     * Tarkistaa hakutoiveeseen liittyvien liitepyyntöjen
     * saapumisen tilan näyttääkseen indikaation siitä käyttöliittymässä
     * @param hakutoive hakutoiveen index numero
     */
    tarkistaKaikkiLiitteetSaapuneet: function (hakutoive) {
        this.LOGS('tarkistaKaikkiLiitteetSaapuneet(',hakutoive,')',  _.every(hakutoiveet[hakutoive].attachments, function (liite) { return liite.receptionStatus !== config.liiteEiSaapunut; }));
        this.LOGS(hakutoiveet[hakutoive].attachments);
        var ind = parseInt(hakutoive) + 1;
        if ( _.every(hakutoiveet[hakutoive].attachments, function (liite) { return liite.receptionStatus !== config.liiteEiSaapunut; })) {
            $('#kaikkiliitteet-' + ind).css('display', '');
            this.disableBtnKaikkiLiitteetSaapuneet(ind);
            this.enableBtnKaikkiLiitteetTarkastettu(ind);
        } else {
            $('#kaikkiliitteet-' + ind).css('display', 'none');
            this.enableBtnKaikkiLiitteetSaapuneet(ind);
            this.disableBtnKaikkiLiitteetTarkastettu(ind);
        }
    },
    /**
     * Tarkistaa hakutoiveeseen liityvien liitepyyntöjen
     * tarkistuksen tilan, jos kaikki on tarkistettu palautta true
     * @param hakutoive hakutoiveen index numero
     * @returns {boolean} true/false
     */
    kaikkiLiitteetTarkistettu: function (hakutoive) {
        this.LOGS('kaikkiLiitteetTarkistettu() -->', _.every(hakutoiveet[hakutoive].attachments, function(liite) { return liite.processingStatus === config.liiteTarkistettu; }));
        this.LOGS('kaikkiLiitteetTarkistettu()', hakutoiveet[hakutoive]);
        var liitteetTarkastettu = _.every(hakutoiveet[hakutoive].attachments, function(liite) { return liite.processingStatus === config.liiteTarkistettu }),
            liitteetSaapuneet = this.kaikkiLiitteetSaapuneetTilassa(hakutoive),
            ind = parseInt(hakutoive) + 1;
        if (liitteetTarkastettu || !liitteetSaapuneet ) {
            this.disableBtnKaikkiLiitteetTarkastettu(ind);
        } else {
            this.enableBtnKaikkiLiitteetTarkastettu(ind);
        }
        return liitteetTarkastettu
    },
    /**
     * Tarkistaa hakutoiveen liitteiden saapumis tilan
     * @param hakutoive hakutoive hakutoiveen index numero
     * @returns {boolean|*} true/false
     */
    kaikkiLiitteetSaapuneetTilassa: function (hakutoive) {
        this.LOGS('kaikkiLiitteetSaapuneetTilassa() -->', _.every(hakutoiveet[hakutoive].attachments, function(liite) { return liite.receptionStatus !== config.liiteEiSaapunut; }));
        return _.every(hakutoiveet[hakutoive].attachments, function(liite) { return liite.receptionStatus !== config.liiteEiSaapunut; });
    },
    /**
     * Asettaa kaikki hakutoiveessa olevat liitepyynnöt
     * käyttöliittymällä saapuneet tilaan
     * @param indx hakutoiveen index numero
     * @param trs hakutoiveen liitteen index numero
     */
    validateKaikkiLiitteetSaapuneet: function (indx, trs) {
        this.LOGS('validateKaikkiLiitteetSaapuneet( ', indx,', ',trs,') ', $('#liitesaapunut-tr-' +indx + '-' + trs + ' [type=checkbox]').prop('checked'));
        var hakutoive = parseInt(indx) - 1,
            saapunutCheckBox = $('#liitesaapunut-tr-' +indx + '-' + trs + ' [type=checkbox]').prop('checked') ;
        if (saapunutCheckBox) {

            $('#select-saapunut-' +indx + '-' + trs).removeAttr('disabled');
            $('#select-tarkistettu-' +indx + '-' + trs).removeAttr('disabled');
            $('#select-saapunut-' +indx + '-' + trs).val(config.liiteSaapunut);
            hakutoiveet[hakutoive].attachments[trs].receptionStatus = config.liiteSaapunut;
            this.tarkistaKaikkiLiitteetSaapuneet(hakutoive);
        } else {
            $('#select-saapunut-' +indx + '-' + trs).attr('disabled', 'true');
            $('#select-tarkistettu-' +indx + '-' + trs).attr('disabled', 'true');
            $('#select-saapunut-' +indx + '-' + trs).val(config.liiteEiSaapunut);
            $('#select-tarkistettu-' +indx + '-' + trs).val(config.liiteEiTarkistettu);
            hakutoiveet[hakutoive].attachments[trs].receptionStatus = config.liiteEiSaapunut;
            hakutoiveet[hakutoive].attachments[trs].processingStatus = config.liiteEiTarkistettu;
            this.tarkistaKaikkiLiitteetSaapuneet(hakutoive);
        }
        var aoGroup = hakutoiveet[indx-1].attachments[trs].aoGroupId,
            atcId = hakutoiveet[indx-1].attachments[trs].id;
        for (var g in hakutoiveet){

            for(var t in hakutoiveet[g].attachments) {
                this.LOGS('liiteen ryhmä id:', aoGroup, hakutoiveet[g].attachments[t].aoGroupId);
                this.LOGS('liite id: ', hakutoiveet[g].attachments[t].id, ' - ', atcId );
                if(hakutoiveet[g].attachments[t].aoGroupId === aoGroup && aoGroup !== ''
                    && hakutoiveet[g].attachments[t].id === atcId) {
                    var ind = parseInt(g) + 1;
                    this.LOGS('saapumis tila ryhmä liitteisiin: ', ind,' ', t,  saapunutCheckBox);
                    if (saapunutCheckBox){
                        $('#liitesaapunut-tr-' +ind + '-' + t + ' [type=checkbox]').attr('checked', 'true');
                        $('#select-saapunut-' +ind + '-' + t).removeAttr('disabled');
                        $('#select-tarkistettu-' +ind + '-' + t).removeAttr('disabled');
                        $('#select-saapunut-' +ind + '-' + t).val(config.liiteSaapunut);
                        hakutoiveet[ind - 1].attachments[t].receptionStatus = config.liiteSaapunut;
                        this.tarkistaKaikkiLiitteetSaapuneet(ind - 1);
                    } else if(ind !== indx){
                        $('#liitesaapunut-tr-' +ind + '-' + t + ' [type=checkbox]').prop('checked', '');
                        $('#select-saapunut-' +ind + '-' + t).attr('disabled', 'true');
                        $('#select-tarkistettu-' +ind + '-' + t).attr('disabled', 'true');
                        $('#select-saapunut-' +ind + '-' + t).val(config.liiteEiSaapunut);
                        $('#select-tarkistettu-' +ind + '-' + t).val(config.liiteEiTarkistettu);
                        hakutoiveet[ind - 1].attachments[t].receptionStatus = config.liiteEiSaapunut;
                        hakutoiveet[ind - 1].attachments[t].processingStatus = config.liiteEiTarkistettu;
                        this.tarkistaKaikkiLiitteetSaapuneet(ind - 1);
                    }
                }

            }
        }

        this.tarkistaHakutoiveValmis(indx);
    },
    /**
     * tarkistaa hakutoiveen valmiuden ja näyttää
     * siitä indikaation käyttöliittymällä
     * @param indx hakutoiveen index numero
     */
    tarkistaHakutoiveValmis: function (indx) {
        this.LOGS('tarkistaHakutoiveValmis()');
        $('#hylatty-' +indx).css('display', 'none');
        $('#hakukelpoinen-' +indx).css('display', 'none');
        $('#puutteellinen-' +indx).css('display', 'none');
        if (hakutoiveet[indx - 1].status ===  config.eiHakukelpoinen) {
            $('#hylatty-' +indx).css('display', '');
        } else if(hakutoiveet[indx - 1].status ===  config.hakukelpoinen ){
            $('#hakukelpoinen-' +indx).css('display', '');
        } else if (hakutoiveet[indx - 1].status === config.puutteellinen) {
            $('#puutteellinen-' +indx).css('display', '');
        }
        if (_.isEqual(hakutoiveet[indx-1], hakutoiveetCache[indx-1])){
            $('#tallennettu-' + indx).css('display', '');
            $('#muuttunut-' + indx).css('display', 'none');
            this.disableBtnTallennaKelpoisuusLiitteet(indx);
        } else {
            $('#tallennettu-' + indx).css('display', 'none');
            $('#muuttunut-' + indx).css('display', '');
            this.enableBtnTallennaKelpoisuusLiitteet(indx);
        }

    },
    /**
     * asettetaan liitteen saapunut tilan UI:sta
     * sitä vaihdettaessa pudotusvalikosta
     * @param indx hakutoiveen index numero
     * @param trs hakutoiveen liitteen index numero
     */
    saapumisTila: function (indx, trs) {
        this.LOGS('saapumisTila()', indx);
        hakutoiveet[indx-1].attachments[trs].receptionStatus = $('#select-saapunut-' + indx +'-' + trs).val();
        this.tarkistaHakutoiveValmis(indx);
        var aoGroup = hakutoiveet[indx-1].attachments[trs].aoGroupId,
        atcId = hakutoiveet[indx-1].attachments[trs].id;
        for (var g in hakutoiveet){
            for (var t in hakutoiveet[g].attachments) {
                this.LOGS('liite id: ', hakutoiveet[g].attachments[t].id, ' - ', atcId );
                if(hakutoiveet[g].attachments[t].aoGroupId === aoGroup && aoGroup !== ''
                    && hakutoiveet[g].attachments[t].id === atcId) {
                    var ind = parseInt(g) + 1;
                    hakutoiveet[ind-1].attachments[t].receptionStatus = $('#select-saapunut-' + indx +'-' + trs).val();
                    $('#select-saapunut-' + ind +'-' + t).val($('#select-saapunut-' + indx +'-' + trs).val());
                }
            }
        }
    },

    /**
     * asettaa liitteen tarkistus tilan UI:sta
     * sitä vaihdettaessa käyttöliittymän puodustvalikosta
     * @param indx hakutoiveen index numero
     * @param trs hakutoiveen liitteen index numero
     */
    liitteenTila: function (indx, trs) {
        this.LOGS('liitteenTila()', indx);
        hakutoiveet[indx-1].attachments[trs].processingStatus = $('#select-tarkistettu-' + indx +'-' + trs).val();
        this.LOGS('-->', hakutoiveet[indx-1].attachments[trs].processingStatus);

        var aoGroup = hakutoiveet[indx-1].attachments[trs].aoGroupId,
            atcId = hakutoiveet[indx-1].attachments[trs].id;
        for (var g in hakutoiveet){
            for(var t in hakutoiveet[g].attachments) {
                this.LOGS('liite id: ', hakutoiveet[g].attachments[t].id, ' - ', atcId );
                if(hakutoiveet[g].attachments[t].aoGroupId === aoGroup && aoGroup !== ''
                    && hakutoiveet[g].attachments[t].id === atcId) {
                    var ind = parseInt(g) + 1;
                    hakutoiveet[ind-1].attachments[t].processingStatus = $('#select-tarkistettu-' + indx +'-' + trs).val();
                    $('#select-tarkistettu-' + ind +'-' + t).val($('#select-tarkistettu-' + indx +'-' + trs).val());
                    this.kaikkiLiitteetTarkistettu(g);
                }
            }
        }
        this.kaikkiLiitteetTarkistettu(indx-1);
        this.tarkistaHakutoiveValmis(indx);
    },
    /**
     * asettaa liitteiden tarkistuksen tilan, myös ryhmäkohtaisten
     * kysmysten tilan
     * tarkistetuksi
     * @param indx hakutoiveen index numero
     */
    asetaKaikkiLiitteetTarkastetuksi: function (indx) {
        this.LOGS('asetaKaikkiLiitteetTarkastetuksi()');
        for (var t in hakutoiveet[indx-1].attachments) {
            $('#select-tarkistettu-' + indx + '-' +t).val(config.liiteTarkistettu);
            hakutoiveet[indx-1].attachments[t].processingStatus = $('#select-tarkistettu-' + indx + '-' +t).val();
        }
        var aoGroupIds = _.uniq(_.map(hakutoiveet[indx-1].attachments, function (grIds) { return grIds.aoGroupId; })),
            atcIds = _.uniq(_.map(hakutoiveet[indx-1].attachments, function (attachmentsIds) { return attachmentsIds.id; })),
            toiveInd = 0,
            toiveNro = 1;

        _.each(hakutoiveet, function (toive) {
                var liiteInd = 0;
                _.each(toive.attachments, function (liite) {
                        _.each(aoGroupIds, function (grId) {
                                _.each(atcIds, function (atcId) {
                                        kjal.LOGS('liiteen id: ', liite.id, ' - ', atcId);
                                        if ( liite.aoGroupId === grId && grId !== '' && liite.id === atcId){
                                            $('#select-tarkistettu-' + toiveNro + '-' + liiteInd).val(config.liiteTarkistettu);
                                            hakutoiveet[toiveInd].attachments[liiteInd].processingStatus = config.liiteTarkistettu;
                                        }
                                    }
                                )
                            }
                        )
                        liiteInd += 1;
                    }
                )
                kjal.kaikkiLiitteetTarkistettu(toiveInd);
                toiveInd += 1;
                toiveNro += 1;
            }
        );
        $('#btn-kaikki-liitteet-tarkastettu-' + indx).addClass('disabled');
        this.tarkistaHakutoiveValmis(indx);
    },
    /**
     * tallennetaan kelpoisuus ja liitteet
     * @param indx hakutoiveen index numero
     */
    tallennaKelpoisuusJaLiitteet: function (applicationOid, eligibilitiesAndAttachmentsUpdated) {
        var submitData = _.clone(hakutoiveet);
        for (var s in submitData) {
            delete submitData[s].indx;
            for (var r in submitData[s].attachments) {
                delete submitData[s].attachments[r].name;
                delete submitData[s].attachments[r].header;
                delete submitData[s].attachments[r].description;
            }
        }

        var submitData2 = {
            eligibilities: submitData,
            updated: eligibilitiesAndAttachmentsUpdated || null
        };

        this.LOGS('Lähettävä data:', submitData2);
        $.ajax({
            type: 'POST',
            url: window.url("haku-app.processAttachmentsAndEligibility", applicationOid),
            data: JSON.stringify(submitData2),
            async: true,
            contentType: "application/json;charset=utf-8",
            dataType: "json",
            cache: false,
            success: function () {
                /*
                var navToY = kjal.documentYposition();
                window.location.href = window.url("haku-app.application", applicationOid) + '#liitteetkelpoisuusTab#'+navToY;
                window.location.reload(true);
                */
                loadKelpoisuusJaLiitteetContent();
            },
            error: function (jqXHR, textStatus, errorThrown) {
                kjal.LOGS('## kelpoisuus ja liitteet tallennuksessa error ## ', jqXHR, textStatus, errorThrown);

                if(jqXHR.status === 409) {
                    //$('#lock-kelpoisuus-liitteet').removeClass('hidden');
                    $('#overlay-fixed').show();
                    $('#conflict-banner').show();
                } else {
                    $('#error-kelpoisuus-liitteet').removeClass('hidden');
                }

            }
        });
    },
    /**
     * Asettaa kaikki tiedot tarkietettu tilan
     * @param indx hakutoiveen index numero
     */
    kaikkiTiedotTarkistettuCheckBox: function (indx){
        this.LOGS('kaikkiTiedotTarkistettuCheckBox = ', $('#kaikki-tiedot-tarkistettu-' + indx).attr('checked'));
        if( $('#kaikki-tiedot-tarkistettu-' + indx).attr('checked') === 'checked') {
            hakutoiveet[indx-1].preferencesChecked = true;
        } else {
            hakutoiveet[indx-1].preferencesChecked = false;
        }
        this.tarkistaHakutoiveValmis(indx);
    },
    /**
     * Tämän tiedoston consoli logien näyttäminen
     * debuggausta varten. Asetetaan päälle ja pois
     * tämän tiedoston config objetissa.
     */
    LOGS: function (){
        if(config.showlogs){
            console.log(arguments);
        }
    },
    /**
     * palautaa dokumentistä sen Y koordinaatin jossa
     * tallenus tapahtui, jotta vaälilehden tallennuksen
     * yhteydessä voidaan näkymä palauttaa käyttäjälle
     * siihen kohtaan.
     * @returns {*} dokumentin Y-koordinaatti
     */
    documentYposition: function () {
        var yScroll;
        if (self.pageYOffset) {
            yScroll = self.pageYOffset;
        } else if (document.documentElement && document.documentElement.scrollTop) {
            yScroll = document.documentElement.scrollTop;
        } else if (document.body) {
            yScroll = document.body.scrollTop;
        }
        return yScroll;
    },
    /**
     * asetettaa "kaikki liitteet tarkastettu" napin disabled tilaan
     * @param ind hakukohteen index
     */
    disableBtnKaikkiLiitteetTarkastettu: function (ind) {
        this.LOGS('disable ', 'btn kaikki liitteet ', 'tarkastettu');
        $('#btn-kaikki-liitteet-tarkastettu-' +ind).addClass('disabled');
        $('#btn-kaikki-liitteet-tarkastettu-' +ind).attr('disabled', 'true');
    },
    /**
     * asettaa "kaikki liitteet tarkastettu" napin enabled tilaan
     * @param ind hakukohteen index
     */
    enableBtnKaikkiLiitteetTarkastettu: function (ind) {
        this.LOGS('enable ', 'btn kaikki liitteet ', 'tarkastettu');
        $('#btn-kaikki-liitteet-tarkastettu-' +ind).removeClass('disabled');
        $('#btn-kaikki-liitteet-tarkastettu-' +ind).removeAttr('disabled');
    },
    /**
     * asetettaa "kaikki liitteet saapuneet" napin disabled tilaan
     * @param ind hakukohteen index
     */
    disableBtnKaikkiLiitteetSaapuneet: function (ind) {
        this.LOGS('disable ', 'btn kaikki liitteet ', 'saapuneet');
        $('#btn-kaikki-liitteet-saapuneet-' +ind).addClass('disabled');
        $('#btn-kaikki-liitteet-saapuneet-' +ind).attr('disabled', 'true');
    },
    /**
     * asetettaa "kaikki liitteet saapuneet" napin enabled tilaan
     * @param ind hakukohteen index
     */
    enableBtnKaikkiLiitteetSaapuneet: function (ind) {
        this.LOGS('enable ', 'btn kaikki liitteet ', 'saapuneet');
        $('#btn-kaikki-liitteet-saapuneet-' +ind).removeClass('disabled');
        $('#btn-kaikki-liitteet-saapuneet-' +ind).removeAttr('disabled');
    },
    /**
     * asetettaa "tallenna" napin disabled tilaan
     * @param ind hakukohteen index
     */
    disableBtnTallennaKelpoisuusLiitteet: function (ind) {
        this.LOGS('disable ', 'btn ', 'tallene');
        $('#btn-tallenna-kelpoisuus-liitteet').addClass('disabled');
        $('#btn-tallenna-kelpoisuus-liitteet').attr('disabled', 'true');
    },
    /**
     * asetettaa "tallenna" napin enabled tilaan
     * @param ind hakukohteen index
     */
    enableBtnTallennaKelpoisuusLiitteet: function (ind) {
        this.LOGS('enable ', 'btn ', 'tallenna');
        $('#btn-tallenna-kelpoisuus-liitteet').removeClass('disabled');
        $('#btn-tallenna-kelpoisuus-liitteet').removeAttr('disabled');
    },
    showConflictBanner: function() {
        $('#overlay-fixed').show();
        $('#conflict-banner').show();
    }
};
