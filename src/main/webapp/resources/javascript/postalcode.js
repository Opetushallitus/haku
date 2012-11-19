(function() {
    var addressController = {
            clear : function() {
                $('span.post-office').html('');
                $('input:hidden.post-office').val('');
                $('input:text.postal-code').val('');
            }
    };

    $('input:text.postal-code').blur(function(event) {
        var value = this.value,
            re5digit =/^\d{5}$/,
            elementId = this.id;

        if (value && value.length === 5 && value.search(re5digit) !== -1) {
             $.getJSON("/haku/lomake/" + postalcode_settings.applicationPeriodId + "/" +
                     postalcode_settings.formId + "/" + elementId + 
                     "/relatedData/" + value,
                function(data) {
                    if (data && data.name) {
                        $('span.post-office').html(data.name);
                        $('input:hidden.post-office').val(data.name);
                    } else {
                        addressController.clear();
                    }
             });
        } else {
            addressController.clear();
        }
    });
})();