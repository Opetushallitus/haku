$(document).ready(function () {
    console.log('Kelpoisuus liitteet --->', hakutoiveet);

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
    };

    this.tietoLahde = function (indx) {
        hakutoiveet[indx-1].tietolahde = $('#liitteet-table-' + indx + ' #hakukelpoisuus-tietolahde').val();
    };

    this.hylkaamisenSyy = function (indx) {
        hakutoiveet[indx-1].hylkaamisperuste = $('#liitteet-table-' + indx + ' #hylkaamisenperuste').val();
    };

    $('#liitesaapunutCB1').change(function () {
        if ($(this).is(':checked')) {
            $('#liiteselect1').removeAttr('disabled');
        } else {
            $('#liiteselect1').attr('disabled', 'true');
        }
    });
    $('#liitesaapunutCB2').change(function () {
        if ($(this).is(':checked')) {
            $('#liiteselect2').removeAttr('disabled');
        } else {
            $('#liiteselect2').attr('disabled', 'true');
        }
    });

    $('#liiteselect1').change(function () {
        if ($('#liiteselect1').val() === '3') {
            $('#liiteselectsyy1').removeAttr('disabled');
        } else {
            $('#liiteselectsyy1').attr('disabled', 'true');
        }
    });
    $('#liiteselect2').change(function () {
        if ($('#liiteselect2').val() === '3') {
            $('#liiteselectsyy2').removeAttr('disabled');
        } else {
            $('#liiteselectsyy2').attr('disabled', 'true');
        }
    });

    $('#tt').click(function () {
        $('#tiedotarkistettu').css('display', '');
    });
    var toggle1 = true;
    $('#ls').click(function () {
        if (toggle1) {
            $('#liitesaapunutCB1').prop('checked', true);
            $('#liitesaapunutCB2').prop('checked', true);
            $('#kaikkiliitteet').css('display', '');
            $('#liiteselect1').removeAttr('disabled');
            $('#liiteselect2').removeAttr('disabled');
            toggle1 = false;
        } else {
            $('#liitesaapunutCB1').prop('checked', false);
            $('#liitesaapunutCB2').prop('checked', false);
            $('#kaikkiliitteet').css('display', 'none');
            $('#liiteselect1').attr('disabled', 'true');
            $('#liiteselect2').attr('disabled', 'true');
            toggle1 = true;
        }


    });

    for(var hakut in hakutoiveet) {
//        console.log('&&&& ', hakutoiveet[hakut].form_Id);
//         console.log($('#form-kelpliit-'+hakutoiveet[hakut].form_Id).find('#tiedotarkistettu').text());
        $('#form-kelpliit-'+ hakutoiveet[hakut].form_Id +' #tiedotarkistettu').attr('id', 'tiedotarkistettu-'+hakutoiveet[hakut].form_Id);
        $('#form-kelpliit-'+ hakutoiveet[hakut].form_Id +' #kaikkiliitteet').attr('id', 'kaikkiliitteet-'+hakutoiveet[hakut].form_Id);
//        console.log('*** ',$('#tiedotarkistettu-'+hakutoiveet[hakut].form_Id));
    }
    var toggle2 = true;
    this.ttt = function (indx) {
        console.log($('#tiedotarkistettu-'+ indx), indx);
        if(toggle2){
            $('#tiedotarkistettu-'+ indx).css('display', '');
            toggle2 = false;
        } else {
            $('#tiedotarkistettu-'+ indx).css('display', 'none');
            toggle2 = true;
        }

    };

    this.kaikkiLiitteetSaapuneet = function (indx) {
        console.log($('#kaikkiliitteet-'+ indx), indx);
        if(toggle2){
            $('#kaikkiliitteet-'+ indx).css('display', '');
            toggle2 = false;
        } else {
            $('#kaikkiliitteet-'+ indx).css('display', 'none');
            toggle2 = true;
        }

    };

});
