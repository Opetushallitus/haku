$(document).ready(function () {
    console.log('Kelpoisuus liitteet --->');

        this.hakuKelpoisuus =  function () {
            if ($('#hakukelpoisuus-select').val() === '2') {
                $('#hakukelpoisuus-tietolahde').removeAttr('disabled');
                $('#hylkaamisenperuste').attr('disabled', 'true');
            } else if ($('#hakukelpoisuus-select').val() === '3') {
                $('#hylkaamisenperuste').removeAttr('disabled');
                $('#hakukelpoisuus-tietolahde').attr('disabled', 'true');
            } else {
                $('#hakukelpoisuus-tietolahde').attr('disabled', 'true');
                $('#hylkaamisenperuste').attr('disabled', 'true');
            }
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
});
