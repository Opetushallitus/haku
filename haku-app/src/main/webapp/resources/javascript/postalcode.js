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

    $('input:text.postal-code').blur(function (event) {
        var value = this.value;
        if (value) {
            var url = document.URL.split("?")[0] + '/' + this.id;
            $.ajax({
                type: 'POST',
                url: url,
                async: false,
                data: $("form.form").serialize(),
                success: function (data, textStatus, jqXHR) {
                    $(".post-office").replaceWith($(data).find(".post-office"))
                },
                error: function (e, ts, et) {
                    console.log("refresh view error" + ts);
                }
            });
        } else {
            $('span.post-office').html('');
        }
    });
})();
