var config = {
    hakukelpoinen: "INELIGIBLE",
    eiHakukelpoinen: "UNELIGABLE",
    puutteellinen: "INADEQUATE",
    liiteSaapunut: "ARRIVED",
    liiteEiSaapunut: "NOT_RECEIVED",
    liiteEiTarkistettu: "NOT_CHECK",
    liiteTarkistettu: "CHECKED",
    tietolahdeUnknown: "UNKNOWN"
};
var kjal = {
    /**
     * luodaan kelpoisuus ja liitteet välihdelle taustajärjestelmästä saadun
     * datan perusteella lomake hakukelpoisuuden ja liitteiden tilojen tallentamiseksi
     */
    populateForm: function () {
        console.log('Kelpoisuus liitteet --->', hakutoiveet);
        console.log('populoidaan hakutoive lomake', window.location.href);
        for (var hktindx in hakutoiveet) {

            var liitteet = hakutoiveet[hktindx].attachments,
                ind = parseInt(hktindx) + 1,
                $form =
                    "<tr>"
                        + "<td style=\"font-weight: bold;\"> Hakukelpoisuus </td>"
                        + "<td>"
                        + "<select class=\"width-12-11\" id=\"hakukelpoisuus-select\" onchange=\"kjal.hakuKelpoisuus(" + ind + ", false)\">"
                        + "<option value=\"NOT_CHECKED\">Kelpoisuus tarkistamatta</option>"
                        + "<option value=\"INELIGIBLE\">Hakukelpoinen</option>"
                        + "<option value=\"UNELIGABLE\">Ei hakukelpoinen</option>"
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
                        + "<td>Hylkäämisen peruste</td>"
                        + "<td colspan=\"2\">"
                        + "<textarea id=\"hylkaamisenperuste\" rows=\"4\" class=\"width-12-11\" disabled onblur=\"kjal.hylkaamisenSyy(" + ind + ")\"></textarea>"
                        + "</td>"
                        + "</tr>"

                        + "<tr>"
                        + "<td id=\"liitteidenmaara\" style=\"font-weight: bold\">Liitteiden määrä " + hakutoiveet[hktindx].attachments.length + " kpl </td>"
                        + "<td colspan=\"2\"></td>"
                        + "</tr>";


            for (var trs in liitteet){
                $form += "<tr class=\"liitteesaapuneet-" + ind +"\" id=\"liitesaapunut-tr-" +ind+ "-" + trs +"\" >"
                    + "<td class=\"width-25\"><input type=\"checkbox\" \" onchange=\"kjal.validateKaikkiLiitteetSaapuneet(" + ind + "," + trs + ")\" > "
                    + "Liite saapunut: "+ liitteet[trs].name + " " + liitteet[trs].header
                    + "</td> "

                    + "<td>"
                    + "<select class=\"width-12-11\" id=\"select-saapunut-" +ind+ "-" + trs + "\" disabled onchange=\"kjal.saapumisTila("+ ind +","+ trs +")\">"
                    + "<option value=\"ARRIVED\">Saapunut</option>"
                    + "<option value=\"ARRIVED_LATE\">Saapunut myöhässä</option>"
                    + "<option value=\"NOT_RECEIVED\" default selected disabled >Ei saapunut</option>"
                    + "</select>"
                    + "</td>"

                    + "<td>"
                    + "<select class=\"width-12-11\" id=\"select-tarkistettu-" +ind+ "-" + trs + "\" disabled onchange=\"kjal.liitteenTila("+ ind +","+ trs +")\">"
                    + "<option value=\"NOT_CHECK\" default selected>Ei tarkistettu</option>"
                    + "<option value=\"COMPLEMENT_REQUESTED\">Odottaa täydennystä</option>"
                    + "<option value=\"INADEQUATE\">Puutteellinen</option>"
                    + "<option value=\"CHECKED\">Tarkistettu</option>"
                    + "<option value=\"05\">Tarpeeton</option>"
                    + "</select>"
                    + "</td>"
                    + "</tr>";
            }
            $('#liitteet-table-' + ind).append($form);
            this.hakuKelpoisuus(ind, true);
            this.asetaLiitteidenTilat(ind);
        }
    },
    /**
     * Asettaa sivun latautuessa taustajärjestelmästä saadut
     * hakutoiveen tilat käyttöliittymälle.
     * @param ind hakutoiveen index numero
     */
    asetaLiitteidenTilat: function (ind) {
        for (var trs in  hakutoiveet[ind-1].liitteet) {
            if (hakutoiveet[ind-1].attachments[trs].receptionStatus !== config.liiteEiSaapunut){
                $('#liitesaapunut-tr-' +ind + '-' + trs + ' [type=checkbox]').attr('checked', 'true');
                $('#select-saapunut-' +ind + '-' + trs).removeAttr('disabled');
                $('#select-tarkistettu-' +ind + '-' + trs).removeAttr('disabled');
            }
            $('#select-saapunut-' +ind + '-' + trs).val(hakutoiveet[ind-1].attachments[trs].receptionStatus)
            $('#select-tarkistettu-' +ind + '-' + trs).val(hakutoiveet[ind-1].attachments[trs].processingStatus);
        }
    },
    /**
     * Asetetaan hakukelpoisuus käyttöliittymästä
     * @param indx hakutoiveen index numero
     */
    hakuKelpoisuus: function (indx, onPopulate) {
        console.log('ennen muutostosta: ',hakutoiveet[indx-1]);

        if(onPopulate){
            $('#liitteet-table-' + indx + ' #hakukelpoisuus-select').val(hakutoiveet[indx-1].status);
            console.log('**',  hakutoiveet[indx-1].source);
            if(hakutoiveet[indx-1].source !== config.tietolahdeUnknown ) {
                console.log('**',  hakutoiveet[indx-1].source);
                $('#liitteet-table-' + indx + ' #hakukelpoisuus-tietolahde').val(hakutoiveet[indx-1].source);
            }
            $('#liitteet-table-' + indx + ' #hylkaamisenperuste').val(hakutoiveet[indx-1].rejectionBasis);
            if(hakutoiveet[indx-1].preferencesChecked === 'false' ){
                $('#kaikki-tiedot-tarkistettu-' + indx).attr('checked', false);
            } else {
                $('#kaikki-tiedot-tarkistettu-' + indx).attr('checked', true);
            }

        } else {
            hakutoiveet[indx-1].status = $('#liitteet-table-' + indx + ' #hakukelpoisuus-select').val();
        }
        if (hakutoiveet[indx-1].status === config.hakukelpoinen) {
            $('#liitteet-table-' + indx + ' #hakukelpoisuus-tietolahde').removeAttr('disabled');
            $('#liitteet-table-' + indx + ' #hylkaamisenperuste').attr('disabled', 'true');
            hakutoiveet[indx-1].rejectionBasis = '';
            $('#liitteet-table-' + indx + ' #hylkaamisenperuste').val('');
        } else if (hakutoiveet[indx-1].status === config.eiHakukelpoinen) {
            $('#liitteet-table-' + indx + ' #hylkaamisenperuste').removeAttr('disabled');
            $('#liitteet-table-' + indx + ' #hakukelpoisuus-tietolahde').attr('disabled', 'true');
            hakutoiveet[indx-1].source = config.tietolahdeUnknown;
            $('#liitteet-table-' + indx + ' #hakukelpoisuus-tietolahde').val('');
        } else {
            $('#liitteet-table-' + indx + ' #hakukelpoisuus-tietolahde').attr('disabled', 'true');
            $('#liitteet-table-' + indx + ' #hylkaamisenperuste').attr('disabled', 'true');
            hakutoiveet[indx-1].source = config.tietolahdeUnknown;
            hakutoiveet[indx-1].rejectionBasis = '';
            $('#liitteet-table-' + indx + ' #hylkaamisenperuste').val('');
            $('#liitteet-table-' + indx + ' #hakukelpoisuus-tietolahde').val('');
        }
        console.log('muutoksen jälkeen: ', hakutoiveet[indx-1]);
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
        hakutoiveet[indx-1].rejectionBasis = $('#liitteet-table-' + indx + ' #hylkaamisenperuste').val();
        this.tarkistaHakutoiveValmis(indx);
    },
    /**
     * Asetetaan kaikki liitteet saapuneet tilaan käyttöliittymästä
     * @param indx hakutoiveen index numero
     */
    kaikkiLiitteetSaapuneet: function (indx) {
        console.log('kaikkiLiitteetSaapuneet()');
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
        $('#btn-kaikki-liitteet-saapuneet-' +indx).addClass('disabled');
        $('#btn-kaikki-liitteet-tarkastettu-' +indx).removeClass('disabled');
        $('#liitteet-table-' + indx +' *:checkbox').prop('checked', true);
        this.tarkistaHakutoiveValmis(indx);
    },
    /**
     * Tarkistaa hakutoiveeseen liittyvien liitepyyntöjen
     * saapumisen tilan näyttääkseen indikaation siitä käyttöliittymässä
     * @param hakutoive hakutoiveen index numero
     */
    tarkistaKaikkiLiitteetSaapuneet: function (hakutoive) {
        console.log('tarkistaKaikkiLiitteetSaapuneet(',hakutoive,')',  _.every(hakutoiveet[hakutoive].attachments, function (liite) { return liite.status !== config.liiteEiSaapunut; }));
        console.log(hakutoiveet[hakutoive].attachments);
        var ind = hakutoive + 1;
        if ( _.every(hakutoiveet[hakutoive].attachments, function (liite) { return liite.receptionStatus !== config.liiteEiSaapunut; })) {
            $('#kaikkiliitteet-' + ind).css('display', '');
            $('#btn-kaikki-liitteet-saapuneet-' +ind).addClass('disabled');
            $('#btn-kaikki-liitteet-tarkastettu-' +ind).removeClass('disabled');
        } else {
            $('#kaikkiliitteet-' + ind).css('display', 'none');
            $('#btn-kaikki-liitteet-saapuneet-' +ind).removeClass('disabled');
            $('#btn-kaikki-liitteet-tarkastettu-' +ind).addClass('disabled');
        }
    },
    /**
     * Tarkistaa hakutoiveeseen liityvien liitepyyntöjen
     * tarkistuksen tilan, jos kaikki on tarkistettu palautta true
     * @param hakutoive hakutoiveen index numero
     * @returns {boolean} true/false
     */
    kaikkiLiitteetTarkistettu: function (hakutoive) {
        console.log('kaikkiLiitteetTarkistettu() -->', _.every(hakutoiveet[hakutoive].attachments, function(liite) { return liite.processingStatus === config.liiteTarkistettu; }));
        console.log('kaikkiLiitteetTarkistettu()', hakutoiveet[hakutoive]);
        var liitteetTarkastettu = _.every(hakutoiveet[hakutoive].attachments, function(liite) { return liite.processingStatus === config.liiteTarkistettu }),
            liitteetSaapuneet = _.every(hakutoiveet[hakutoive].attachments, function (liite) { return liite.receptionStatus !== config.liiteEiSaapunut; }),
            ind = hakutoive + 1;

        if (liitteetTarkastettu || !liitteetSaapuneet ) {
            $('#btn-kaikki-liitteet-tarkastettu-' +ind).addClass('disabled');
        } else {
            $('#btn-kaikki-liitteet-tarkastettu-' +ind).removeClass('disabled');
        }
        return liitteetTarkastettu
    },
    /**
     * Tarkistaa hakutoiveen liitteiden saapumis tilan
     * @param hakutoive hakutoive hakutoiveen index numero
     * @returns {boolean|*} true/false
     */
    kaikkiLiitteetSaapuneetTilassa: function (hakutoive) {
        console.log('kaikkiLiitteetSaapuneetTilassa() -->', _.every(hakutoiveet[hakutoive].attachments, function(liite) { return liite.receptionStatus !== config.liiteEiSaapunut; }));
        return _.every(hakutoiveet[hakutoive].attachments, function(liite) { return liite.receptionStatus !== config.liiteEiSaapunut; });
    },
    /**
     * Asettaa kaikki hakutoiveessa olevat liitepyynnöt
     * käyttöliittymällä saapuneet tilaan
     * @param indx hakutoiveen index numero
     * @param trs hakutoiveen liitteen index numero
     */
    validateKaikkiLiitteetSaapuneet: function (indx, trs) {
        console.log('validateKaikkiLiitteetSaapuneet( ', indx,', ',trs,') ', $('#liitesaapunut-tr-' +indx + '-' + trs + ' [type=checkbox]').prop('checked'));
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

        var aoGroup = hakutoiveet[indx-1].attachments[trs].aoGroupId;
        for (var g in hakutoiveet){
            for(var t in hakutoiveet[g].attachments) {
                if(hakutoiveet[g].attachments[t].aoGroupId === aoGroup && aoGroup !== '') {
                    var ind = parseInt(g) + 1;
                    console.log('saapumis tila ryhmä liitteisiin: ', ind,' ', t,  saapunutCheckBox);
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
        console.log('tarkistaHakutoiveValmis()');
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
            $('#btn-tallenna-kelpoisuus-liitteet-' + indx).addClass('disabled');
        } else {
            $('#tallennettu-' + indx).css('display', 'none');
            $('#muuttunut-' + indx).css('display', '');
            $('#btn-tallenna-kelpoisuus-liitteet-' + indx).removeClass('disabled');
        }

    },
    /**
     * asettetaan liitteen saapunut tilan UI:sta
     * sitä vaihdettaessa pudotusvalikosta
     * @param indx hakutoiveen index numero
     * @param trs hakutoiveen liitteen index numero
     */
    saapumisTila: function (indx, trs) {
        hakutoiveet[indx-1].attachments[trs].receptionStatus = $('#select-saapunut-' + indx +'-' + trs).val();

        var aoGroup = hakutoiveet[indx-1].attachments[trs].aoGroupId;
        for (var g in hakutoiveet){
            for(var t in hakutoiveet[g].attachments) {
                if(hakutoiveet[g].attachments[t].aoGroupId === aoGroup) {
                    var ind = parseInt(g) + 1;
                    hakutoiveet[ind-1].attachments[t].receptionStatus = $('#select-saapunut-' + indx +'-' + trs).val();
                    $('#select-saapunut-' + ind +'-' + t).val($('#select-saapunut-' + indx +'-' + trs).val());
                }
            }
        }
        this.tarkistaHakutoiveValmis(indx);
    },
    /**
     * asettaa liitteen tarkistus tilan UI:sta
     * sitä vaihdettaessa käyttöliittymän puodustvalikosta
     * @param indx hakutoiveen index numero
     * @param trs hakutoiveen liitteen index numero
     */
    liitteenTila: function (indx, trs) {
        console.log('liitteenTila()');
        hakutoiveet[indx-1].attachments[trs].processingStatus = $('#select-tarkistettu-' + indx +'-' + trs).val();
        console.log('-->', hakutoiveet[indx-1].attachments[trs].processingStatus);

        var aoGroup = hakutoiveet[indx-1].attachments[trs].aoGroupId;
        for (var g in hakutoiveet){
            for(var t in hakutoiveet[g].attachments) {
                if(hakutoiveet[g].attachments[t].aoGroupId === aoGroup) {
                    var ind = parseInt(g) + 1;
                    hakutoiveet[ind-1].attachments[t].processingStatus = $('#select-tarkistettu-' + indx +'-' + trs).val();
                    $('#select-tarkistettu-' + ind +'-' + t).val($('#select-tarkistettu-' + indx +'-' + trs).val());
                }
            }
        }
        this.kaikkiLiitteetTarkistettu(indx-1);
        this.tarkistaHakutoiveValmis(indx);
    },
    /**
     * asettaa liitteiden tarkistuksen tilan
     * tarkistetuksi
     * @param indx hakutoiveen index numero
     */
    asetaKaikkiLiitteetTarkastetuksi: function (indx) {
        console.log('asetaKaikkiLiitteetTarkastetuksi()');
        for (var t in hakutoiveet[indx-1].attachments) {
            $('#select-tarkistettu-' + indx + '-' +t).val(config.liiteTarkistettu);
            hakutoiveet[indx-1].attachments[t].processingStatus = $('#select-tarkistettu-' + indx + '-' +t).val();
        }
        $('#btn-kaikki-liitteet-tarkastettu-' + indx).addClass('disabled');
        this.tarkistaHakutoiveValmis(indx);
    },
    /**
     * tallennetaan kelpoisuus ja liitteet
     * @param indx hakutoiveen index numero
     */
    tallennaKelpoisuusJaLiitteet: function (indx) {
        var submitData = _.clone(hakutoiveet);
        for (var s in submitData) {
            delete submitData[s].indx;
            for(var r in submitData[s].attachments){
                delete submitData[s].attachments[r].name;
                delete submitData[s].attachments[r].header;
            }
        }
        $.ajax({
            type: 'POST',
            url: document.URL.split("#")[0] +'processAttachmentsAndEligability',
            data: JSON.stringify(submitData),
            async: true,
            contentType: "application/json;charset=utf-8",
            dataType: "json",
            cache: false,
            success: function (data) {
                window.location = window.location.href + 'liitteetkelpoisuusTab';
                window.location.reload();
            },
            error: function (error) {
                console.log('## kelpoisuus ja liitteet tallennuksessa error ## ', error);
            }
        });

    },
    /**
     * Asettaa kaikki tiedot tarkietettu tilan
     * @param indx hakutoiveen index numero
     */
    kaikkiTiedotTarkistettuCheckBox: function (indx){
        console.log('kaikkiTiedotTarkistettuCheckBox = ', $('#kaikki-tiedot-tarkistettu-' + indx).attr('checked'));
        if( $('#kaikki-tiedot-tarkistettu-' + indx).attr('checked') === 'checked') {
            hakutoiveet[indx-1].preferencesChecked = true;
        } else {
            hakutoiveet[indx-1].preferencesChecked = false;
        }
        this.tarkistaHakutoiveValmis(indx);
    }

};

kjal.populateForm();

$(document).ready(function() {
    /**
     * kelpoisuus ja liitteet välilehden tallennuksen jälkeen
     * asetataan kelpoisuus ja liitteet välilehti takaisin aktiiviseksi
     */
    if( window.location.href.split('#')[1] === 'liitteetkelpoisuusTab' ){
        window.location.href = window.location.href.split('#')[0]+'#';
            $('#kelpoisuusliitteetTab').click();
    }
});
