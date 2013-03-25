/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */

(function () {
    var addressController = {
        clear: function () {
            $('span.post-office').html('');
            $('input:hidden.post-office').val('');
            $('input:text.postal-code').val('');
        }
    };

    $('input:text.postal-code').blur(function (event) {
        var value = this.value, re5digit = /^\d{5}$/, elementId = this.id;

        if (value && value.length === 5 && value.search(re5digit) !== -1) {
            $.getJSON(document.URL + "/" + elementId +
                "/relatedData/" + value,
                function (data) {
                    if (data && data.postOffice) {
                        var postOffice = data.postOffice.translations[postalcode_settings.lang];
                        $('span.post-office').html(postOffice);
                        $('input:hidden.post-office').val(postOffice);
                    } else {
                        addressController.clear();
                    }
                });
        } else {
            addressController.clear();
        }
    });
})();
