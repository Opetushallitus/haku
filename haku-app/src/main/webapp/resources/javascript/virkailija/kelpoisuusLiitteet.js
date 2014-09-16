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
                    + "<option value=\"1\">Kelpoisuus tarkistamatta</option>"
                    + "<option value=\"2\">Hakukelpoinen</option>"
                    + "<option value=\"3\">Ei hakukelpoinen</option>"
                    + "<option value=\"4\">Puuttelinen</option>"
                    + "</select>"
                    + "</td>"
                    + "<td>"
                    + "<select class=\"width-12-11\" id=\"hakukelpoisuus-tietolahde\" disabled onchange=\"tietoLahde(" + ind + ")\">"
                    + "<option value=\"\" default selected disabled>valitse tarkistettu tietolähde</option>"
                    + "<option value=\"1\">Oppilaitoksen toimittava tieto</option>"
                    + "<option value=\"2\">Alkuperäinen todistus</option>"
                    + "<option value=\"3\">Virallinen oikeaksi todistettu kopio</option>"
                    + "<option value=\"4\">Oikeaksi todistettu kopio</option>"
                    + "<option value=\"5\">Kopio</option>"
                    + "<option value=\"6\">Rekisteri</option>"
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
                    + "<select class=\"width-12-11\" id=\"liiteselect-" +ind+ "-" + trs + "\" disabled onchange=\"liitteenTila("+ ind +","+ trs +")\">"
                    + "<option value=\"1\">Saapunut</option>"
                    + "<option value=\"2\">Saapunut myöhässä</option>"
                    + "<option value=\"3\">Odottaa täydennystä</option>"
                    + "<option value=\"4\">Puutteellinen</option>"
                    + "<option value=\"5\">Ei tarkistettu</option>"
                    + "<option value=\"6\" default selected disabled >Ei saapunut</option>"
                    + "<option value=\"7\">Tarkistettu</option>"
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
        if (hakutoiveet[indx-1].hakukelpoisuus === '2') {
            $('#liitteet-table-' + indx + ' #hakukelpoisuus-tietolahde').removeAttr('disabled');
            $('#liitteet-table-' + indx + ' #hylkaamisenperuste').attr('disabled', 'true');
            hakutoiveet[indx-1].hylkaamisperuste = '';
            $('#liitteet-table-' + indx + ' #hylkaamisenperuste').val('');
        } else if (hakutoiveet[indx-1].hakukelpoisuus === '3') {
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
        var hakutoive = parseInt(indx) - 1;
        for(var i in hakutoiveet[hakutoive].liitteet){
            if (hakutoiveet[hakutoive].liitteet[i].tila === 6) {
                $('#liiteselect-' + indx +'-' + i).removeAttr('disabled');
                $('#liiteselect-' + indx +'-' + i).val(1);
                hakutoiveet[hakutoive].liitteet[i].tila = 1;
            }
        }
        $('#kaikkiliitteet-'+ indx).css('display', '');
        $('#liitteet-table-' + indx +' *:checkbox').prop('checked', true);
        tarkistaHakutoiveValmis(indx);
    };
    /**
     * Tarkistaa hakutoiveeseen liittyvien liitepyyntöjen
     * saapumisen tilan näyttääkseen indikaation siitä käyttöliittymässä
     * @param hakutoive hakutoiveen index numero
     */
    kaikkiLiitteetSaapuneet = function (hakutoive) {
        var ind = hakutoive + 1;
        for(var i in hakutoiveet[hakutoive].liitteet){
            console.log('€€ ', hakutoiveet[hakutoive].liitteet[i].tila);
            if(hakutoiveet[hakutoive].liitteet[i].tila === 6) {
                $('#kaikkiliitteet-' + ind).css('display', 'none');
                return;
            } else {
                $('#kaikkiliitteet-' + ind).css('display', '');
            };
        }
    }
    /**
     * Tarkistaa hakutoiveeseen liityvien liitepyyntöjen
     * tarkistuksen tilan, jos kaikki on tarkistettu palautta true
     * @param hakutoive hakutoiveen index numero
     * @returns {boolean} true/false
     */
    kaikkiLiitteetTarkistettu = function (hakutoive) {
        console.log('kaikkiLiitteetTarkistettu() -->');
        for (var j in hakutoiveet[hakutoive].liitteet) {
            console.log(hakutoiveet[hakutoive].liitteet[j].tila);
            if(hakutoiveet[hakutoive].liitteet[j].tila !== '7') {
                return false;
            }
        }
        return true;
    };
    /**
     * Asettaa kaikki hakutoiveessa olevat liitepyynnöt
     * käyttöliittymällä saapuneet tilaan
     * @param indx hakutoiveen index numero
     * @param trs hakutoiveen liitteen index numero
     */
    this.validateKaikkiLiitteetSaapuneet = function (indx, trs) {
        var hakutoive = parseInt(indx) - 1;
        if ($('#liitesaapunut-tr-' +indx + '-' + trs + ' [type=checkbox]').prop('checked')) {
            $('#liiteselect-' +indx + '-' + trs).removeAttr('disabled');
            $('#liiteselect-' +indx + '-' + trs).val(1);
            hakutoiveet[hakutoive].liitteet[trs].tila = 1;
            kaikkiLiitteetSaapuneet(hakutoive);
        } else {
            $('#liiteselect-' +indx + '-' + trs).attr('disabled', 'true');
            $('#liiteselect-' +indx + '-' + trs).val(6);
            hakutoiveet[hakutoive].liitteet[trs].tila = 6;
            kaikkiLiitteetSaapuneet(hakutoive);
        }
        tarkistaHakutoiveValmis(indx);
    };
    /**
     * tarkistaa hakutoiveen valmiuden ja näyttää
     * siitä indikaation käyttöliittymällä
     * @param indx hakutoiveen index numero
     */
    tarkistaHakutoiveValmis = function (indx) {
        if (hakutoiveet[indx - 1].hakukelpoisuus ===  '3') {
            $('#hylatty-' +indx).css('display', '');
            $('#valmis-' +indx).css('display', 'none');
            $('#kesken-' +indx).css('display', 'none');
        } else {
            $('#hylatty-' +indx).css('display', 'none');
            $('#valmis-' +indx).css('display', 'none');
            $('#kesken-' +indx).css('display', 'none');
            if(hakutoiveet[indx - 1].hakukelpoisuus ===  '2' && kaikkiLiitteetTarkistettu(indx-1) && hakutoiveet[indx-1].tietolahde !== '') {
                $('#valmis-' +indx).css('display', '');
            } else {
                $('#kesken-' +indx).css('display', '');
            }
        }
    };
    /**
     * asettetaan liitteen tilan UI:sta
     * sitä vaihdettaessa pudotusvalikosta
     * @param indx hakutoiveen index numero
     * @param trs hakutoiveen liitteen index numero
     */
    this.liitteenTila = function (indx, trs) {
        hakutoiveet[indx-1].liitteet[trs].tila = $('#liiteselect-' + indx +'-' + trs).val();
        tarkistaHakutoiveValmis(indx);
    }

});
