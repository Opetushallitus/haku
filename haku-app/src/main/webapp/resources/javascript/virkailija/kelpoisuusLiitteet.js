$(document).ready(function () {
    //TODO: poista tämä
    console.log('Kelpoisuus liitteet --->', hakutoiveet);
    /**
     * luodaan kelpoisuus ja liitteet välihdelle taustajärjestelmästä saadun
     * datan perusteella lomake hakukelpoisuuden ja liitteiden tilojen tallentamiseksi
     */
    populateForm = function () {
        //TODO: poista tämä
        console.log('populoidaan hakutoive lomake');
        for (var hktindx in hakutoiveet) {

            var liitteet = hakutoiveet[hktindx].liitteet,
                ind = parseInt(hktindx) +1;

            var $form =
                "<tr>"
                    + "<td> Hakukelpoisuus </td>"
                    + "<td>"
                    + "<select class=\"width-12-11\" id=\"hakukelpoisuus-select\" onchange=\"hakuKelpoisuus(" + ind + ")\">"
                    + "<option value=\"01\">Kelpoisuus tarkistamatta</option>"
                    + "<option value=\"02\">Hakukelpoinen</option>"
                    + "<option value=\"03\">Ei hakukelpoinen</option>"
                    + "<option value=\"04\">Puuttelinen</option>"
                    + "</select>"
                    + "</td>"
                    + "<td>"
                    + "<select class=\"width-12-11\" id=\"hakukelpoisuus-tietolahde\" disabled onchange=\"tietoLahde(" + ind + ")\">"
                    + "<option value=\"\" default selected disabled>valitse tarkistettu tietolähde</option>"
                    + "<option value=\"01\">Oppilaitoksen toimittava tieto</option>"
                    + "<option value=\"02\">Alkuperäinen todistus</option>"
                    + "<option value=\"03\">Virallinen oikeaksi todistettu kopio</option>"
                    + "<option value=\"04\">Oikeaksi todistettu kopio</option>"
                    + "<option value=\"05\">Kopio</option>"
                    + "<option value=\"06\">Rekisteri</option>"
                    + "</select>"
                    + "</td>"
                    + "</tr>"

                    + "<tr>"
                    + "<td>Hylkäämisen peruste</td>"
                    + "<td colspan=\"2\">"
                    + "<textarea id=\"hylkaamisenperuste\" rows=\"4\" class=\"width-12-11\" disabled onblur=\"hylkaamisenSyy(" + ind + ")\"></textarea>"
                    + "</td>"
                    + "</tr>"

                    + "<tr>"
                    + "<td id=\"liitteidenmaara\" style=\"font-weight: bold\">Liitteiden määrä " + hakutoiveet[hktindx].liitteet.length + " kpl </td>"
                    + "<td></td>"
                    + "</tr>";

            for (var trs in liitteet){
                $form += "<tr class=\"liitteesaapuneet-" + ind +"\" id=\"liitesaapunut-tr-" +ind+ "-" + trs +"\" >"
                    + "<td><input type=\"checkbox\" \" onchange=\"validateKaikkiLiitteetSaapuneet(" + ind + "," + trs + ")\" > "
                    + "Liite saapunut: "+ liitteet[trs].nimi + "</td> "
                    + "<td>"
                    + "<select class=\"width-12-11\" id=\"select-saapunut-" +ind+ "-" + trs + "\" disabled onchange=\"saapumisTila("+ ind +","+ trs +")\">"
                    + "<option value=\"01\">Saapunut</option>"
                    + "<option value=\"02\">Saapunut myöhässä</option>"
                    + "<option value=\"03\" default selected disabled >Ei saapunut</option>"
                    + "</select>"
                    + "</td>"
                    + "<td>"
                    + "<select class=\"width-12-11\" id=\"select-tarkistettu-" +ind+ "-" + trs + "\" disabled onchange=\"liitteenTila("+ ind +","+ trs +")\">"
                    + "<option value=\"01\" default selected>Ei tarkistettu</option>"
                    + "<option value=\"02\">Odottaa täydennystä</option>"
                    + "<option value=\"03\">Puutteellinen</option>"
                    + "<option value=\"04\">Tarkistettu</option>"
                    + "</select>"
                    + "</td>"
                    + "</tr>";
            }
            $('#liitteet-table-' + ind).append($form);
        }
    }();
    /**
     * Asetetaan hakukelpoisuus käyttöliittymästä
     * @param indx hakutoiveen index numero
     */
    this.hakuKelpoisuus =  function (indx) {
        hakutoiveet[indx-1].hakukelpoisuus = $('#liitteet-table-' + indx + ' #hakukelpoisuus-select').val();
        if (hakutoiveet[indx-1].hakukelpoisuus === '02') {
            $('#liitteet-table-' + indx + ' #hakukelpoisuus-tietolahde').removeAttr('disabled');
            $('#liitteet-table-' + indx + ' #hylkaamisenperuste').attr('disabled', 'true');
            hakutoiveet[indx-1].hylkaamisperuste = '';
            $('#liitteet-table-' + indx + ' #hylkaamisenperuste').val('');
        } else if (hakutoiveet[indx-1].hakukelpoisuus === '03') {
            $('#liitteet-table-' + indx + ' #hylkaamisenperuste').removeAttr('disabled');
            $('#liitteet-table-' + indx + ' #hakukelpoisuus-tietolahde').attr('disabled', 'true');
            hakutoiveet[indx-1].tietolahde = '';
            $('#liitteet-table-' + indx + ' #hakukelpoisuus-tietolahde').val('');
        } else {
            $('#liitteet-table-' + indx + ' #hakukelpoisuus-tietolahde').attr('disabled', 'true');
            $('#liitteet-table-' + indx + ' #hylkaamisenperuste').attr('disabled', 'true');
            hakutoiveet[indx-1].tietolahde = '';
            hakutoiveet[indx-1].hylkaamisperuste = '';
            $('#liitteet-table-' + indx + ' #hylkaamisenperuste').val('');
            $('#liitteet-table-' + indx + ' #hakukelpoisuus-tietolahde').val('');
        }
        tarkistaHakutoiveValmis(indx);
    };
    /**
     * Asetetaan hakukelpoisuuteen liittyvän tarkastuksen tietolähde
     * @param indx hakutoiveen index numero
     */
    this.tietoLahde = function (indx) {
        hakutoiveet[indx-1].tietolahde = $('#liitteet-table-' + indx + ' #hakukelpoisuus-tietolahde').val();
        tarkistaHakutoiveValmis(indx);
    };
    /**
     * Asetetaan hakukelpoisuuteen liittyvä hylkäämisen syy
     * @param indx hakutoiveen index numero
     */
    this.hylkaamisenSyy = function (indx) {
        hakutoiveet[indx-1].hylkaamisperuste = $('#liitteet-table-' + indx + ' #hylkaamisenperuste').val();
        tarkistaHakutoiveValmis(indx);
    };
    /**
     * Asetetaan kaikki liitteet saapuneet tilaan käyttöliittymästä
     * @param indx hakutoiveen index numero
     */
    this.kaikkiLiitteetSaapuneet = function (indx) {
        console.log('kaikkiLiitteetSaapuneet()');
        var hakutoive = parseInt(indx) - 1;
        for(var i in hakutoiveet[hakutoive].liitteet){
            if (hakutoiveet[hakutoive].liitteet[i].tila === '03') {
                $('#select-saapunut-' + indx +'-' + i).removeAttr('disabled');
                $('#select-saapunut-' + indx +'-' + i).val('01');
                $('#select-tarkistettu-' + indx +'-' + i).removeAttr('disabled');
                $('#select-tarkistettu-' + indx +'-' + i).val('01');
                hakutoiveet[hakutoive].liitteet[i].tila = '01';//$('#select-saapunut-' + indx +'-' + i).val();
                hakutoiveet[hakutoive].liitteet[i].liitteentila = '01'; // $('#select-tarkistettu-' + indx +'-' + i).val();
            }
        }
        $('#kaikkiliitteet-'+ indx).css('display', '');
        $('#btn-kaikki-liitteet-saapuneet-' +indx).addClass('disabled');
        $('#btn-kaikki-liitteet-tarkastettu-' +indx).removeClass('disabled');
        $('#liitteet-table-' + indx +' *:checkbox').prop('checked', true);
        tarkistaHakutoiveValmis(indx);
    };
    /**
     * Tarkistaa hakutoiveeseen liittyvien liitepyyntöjen
     * saapumisen tilan näyttääkseen indikaation siitä käyttöliittymässä
     * @param hakutoive hakutoiveen index numero
     */
    tarkistaKaikkiLiitteetSaapuneet = function (hakutoive) {
        console.log('tarkistaKaikkiLiitteetSaapuneet(',hakutoive,')');
        console.log(hakutoiveet[hakutoive].liitteet);
        var ind = hakutoive + 1;
        if ( _.every(hakutoiveet[hakutoive].liitteet, function (liite) { return liite.tila !== '03'; })) {
            $('#kaikkiliitteet-' + ind).css('display', '');
            $('#btn-kaikki-liitteet-saapuneet-' +ind).addClass('disabled');
            $('#btn-kaikki-liitteet-tarkastettu-' +ind).removeClass('disabled');
        } else {
            $('#kaikkiliitteet-' + ind).css('display', 'none');
            $('#btn-kaikki-liitteet-saapuneet-' +ind).removeClass('disabled');
            $('#btn-kaikki-liitteet-tarkastettu-' +ind).addClass('disabled');
        }
    };
    /**
     * Tarkistaa hakutoiveeseen liityvien liitepyyntöjen
     * tarkistuksen tilan, jos kaikki on tarkistettu palautta true
     * @param hakutoive hakutoiveen index numero
     * @returns {boolean} true/false
     */
    kaikkiLiitteetTarkistettu = function (hakutoive) {
        console.log('kaikkiLiitteetTarkistettu() -->', _.every(hakutoiveet[hakutoive].liitteet, function(liite) { return liite.liitteentila === '04'; }));
        console.log('kaikkiLiitteetTarkistettu()', hakutoiveet[hakutoive]);
        var liitteetTarkastettu = _.every(hakutoiveet[hakutoive].liitteet, function(liite) { return liite.liitteentila === '04' && liite.tila !== '03'; }),
            ind = hakutoive + 1;
        if (liitteetTarkastettu) {
            console.log('tttt');
            $('#btn-kaikki-liitteet-tarkastettu-' +ind).addClass('disabled');
        } else {
            console.log('juuuu');
            $('#btn-kaikki-liitteet-tarkastettu-' +ind).removeClass('disabled');

        }
        return liitteetTarkastettu
    };
    /**
     * Tarkistaa hakutoiveen liitteiden saapumis tilan
     * @param hakutoive hakutoive hakutoiveen index numero
     * @returns {boolean|*} true/false
     */
    kaikkiLiitteetSaapuneetTilassa = function (hakutoive) {
        console.log('kaikkiLiitteetSaapuneetTilassa() -->', _.every(hakutoiveet[hakutoive].liitteet, function(liite) { return liite.tila !== '03'; }));
        return _.every(hakutoiveet[hakutoive].liitteet, function(liite) { return liite.tila !== '03'; });
    };
    /**
     * Asettaa kaikki hakutoiveessa olevat liitepyynnöt
     * käyttöliittymällä saapuneet tilaan
     * @param indx hakutoiveen index numero
     * @param trs hakutoiveen liitteen index numero
     */
    this.validateKaikkiLiitteetSaapuneet = function (indx, trs) {
        console.log('validateKaikkiLiitteetSaapuneet( ', indx,', ',trs,')');
        var hakutoive = parseInt(indx) - 1;
        if ($('#liitesaapunut-tr-' +indx + '-' + trs + ' [type=checkbox]').prop('checked')) {
            $('#select-saapunut-' +indx + '-' + trs).removeAttr('disabled');
            $('#select-tarkistettu-' +indx + '-' + trs).removeAttr('disabled');
            $('#select-saapunut-' +indx + '-' + trs).val('01');
            hakutoiveet[hakutoive].liitteet[trs].tila = '01';
            tarkistaKaikkiLiitteetSaapuneet(hakutoive);
            kaikkiLiitteetTarkistettu(hakutoive);
        } else {
            $('#select-saapunut-' +indx + '-' + trs).attr('disabled', 'true');
            $('#select-tarkistettu-' +indx + '-' + trs).attr('disabled', 'true');
            $('#select-saapunut-' +indx + '-' + trs).val('03');
            $('#select-tarkistettu-' +indx + '-' + trs).val('01');
            hakutoiveet[hakutoive].liitteet[trs].tila = '03';
            hakutoiveet[hakutoive].liitteet[trs].liitteentila = '01';
            tarkistaKaikkiLiitteetSaapuneet(hakutoive);
            kaikkiLiitteetTarkistettu(hakutoive);
        }
        tarkistaHakutoiveValmis(indx);
    };
    /**
     * tarkistaa hakutoiveen valmiuden ja näyttää
     * siitä indikaation käyttöliittymällä
     * @param indx hakutoiveen index numero
     */
    tarkistaHakutoiveValmis = function (indx) {
        console.log('tarkistaHakutoiveValmis()');
        if (hakutoiveet[indx - 1].hakukelpoisuus ===  '03') {
            $('#hylatty-' +indx).css('display', '');
            $('#valmis-' +indx).css('display', 'none');
            $('#kesken-' +indx).css('display', 'none');
        } else {
            $('#hylatty-' +indx).css('display', 'none');
            $('#valmis-' +indx).css('display', 'none');
            $('#kesken-' +indx).css('display', 'none');
            if(hakutoiveet[indx - 1].hakukelpoisuus ===  '02' &&
                kaikkiLiitteetTarkistettu(indx-1) &&
                hakutoiveet[indx-1].tietolahde !== '' &&
                kaikkiLiitteetSaapuneetTilassa(indx-1) ) {
                $('#valmis-' +indx).css('display', '');
            } else {
                $('#kesken-' +indx).css('display', '');
            }
        }
    };
    /**
     * asettetaan liitteen saapunut tilan UI:sta
     * sitä vaihdettaessa pudotusvalikosta
     * @param indx hakutoiveen index numero
     * @param trs hakutoiveen liitteen index numero
     */
    this.saapumisTila = function (indx, trs) {
        hakutoiveet[indx-1].liitteet[trs].tila = $('#select-saapunut-' + indx +'-' + trs).val();
        tarkistaHakutoiveValmis(indx);
    }
    /**
     * asettaa liitteen tarkistus tilan UI:sta
     * sitä vaihdettaessa käyttöliittymän puodustvalikosta
     * @param indx hakutoiveen index numero
     * @param trs hakutoiveen liitteen index numero
     */
    this.liitteenTila = function (indx, trs) {
        console.log('liitteenTila()');
        hakutoiveet[indx-1].liitteet[trs].liitteentila = $('#select-tarkistettu-' + indx +'-' + trs).val();
        console.log('-->', hakutoiveet[indx-1].liitteet[trs].liitteentila);
        kaikkiLiitteetTarkistettu(indx-1);
        tarkistaHakutoiveValmis(indx);
    }

    this.asetaKaikkiLiitteetTarkastetuksi = function (indx) {
        console.log('asetaKaikkiLiitteetTarkastetuksi()');
        for (var t in hakutoiveet[indx-1].liitteet) {
            $('#select-tarkistettu-' + indx + '-' +t).val('04');
            hakutoiveet[indx-1].liitteet[t].liitteentila = $('#select-tarkistettu-' + indx + '-' +t).val();
        }
        $('#btn-kaikki-liitteet-tarkastettu-' + indx).addClass('disabled');
        tarkistaHakutoiveValmis(indx);
    }

});
