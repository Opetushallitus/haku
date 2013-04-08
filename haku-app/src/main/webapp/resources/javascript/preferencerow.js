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

(function() {
    var preferenceRow = {
        populateSelectInput : function(orgId, selectInputId) {
            $.getJSON(sortabletable_settings.koulutusinformaatioBaseUrl + "/ao/search/" + sortabletable_settings.applicationPeriodId + "/" + orgId, {
                prerequisite : sortabletable_settings.tutkintoId,
                vocational : sortabletable_settings.vocational
            }, function(data) {
                var hakukohdeId = $("#" + selectInputId + "-id").val(), $selectInput = $("#" + selectInputId);

                $selectInput.html("<option></option>");
                $(data).each(function(index, item) {
                    var selected = "";
                    if (hakukohdeId == item.id) {
                        selected = 'selected = "selected"';
                        preferenceRow.searchAdditionalQuestions(hakukohdeId, $selectInput.data("additionalquestions"));
                    }
                    $selectInput.append('<option value="' + item.name + '" ' + selected + ' data-id="' + item.id + '" data-educationdegree="' + item.educationDegree + '">' + item.name + '</option>');
                });
            });
        },

        clearSelectInput : function(selectInputId) {
            $("#" + selectInputId + "-id").val("");
            $("#" + selectInputId + "-educationDegree").val("");
            $("#" + selectInputId).html("<option></option>");

        },

        searchAdditionalQuestions : function(hakukohdeId, additionalQuestionsId) {
            var url = sortabletable_settings.contextPath + "/lomake/" + sortabletable_settings.applicationPeriodId + "/" +
                sortabletable_settings.formId + "/" + sortabletable_settings.vaiheId + "/" +
                sortabletable_settings.teemaId + "/additionalquestions/" + hakukohdeId;

            $.get(url, function(data) {
                $("#" + additionalQuestionsId).html(data);
            });
        }
    };

    $('button.reset').click(function(event) {
        var id = $(this).data('id');
        $('[id|="' + id + '"]').val('').html('');
        preferenceRow.clearSelectInput(id + "-Koulutus");
    });

    $(".field-container-text input:text").each(function(index) {
        var selectInputId = $(this).data('selectinputid');
        var $hiddenInput = $("#" + this.id + "-id");
        $(this).autocomplete({
            minLength : 1,
            source : function(request, response) {
                $.getJSON(sortabletable_settings.koulutusinformaatioBaseUrl + "/lop/search/" + request.term, {
                    asId : sortabletable_settings.applicationPeriodId,
                    prerequisite : sortabletable_settings.tutkintoId,
                    vocational : sortabletable_settings.vocational
                }, function(data) {
                    response($.map(data, function(result) {
                        return {
                            label : result.name,
                            value : result.name,
                            dataId : result.id
                        }
                    }));
                });
            },
            select: function (event, ui) {
                $hiddenInput.val(ui.item.dataId);
                preferenceRow.populateSelectInput(ui.item.dataId, selectInputId);
            },
            change: function (ev, ui) {
                if (!ui.item) {
                    $(this).val("");
                    $hiddenInput.val("");
                    preferenceRow.clearSelectInput(selectInputId);
                }
            }
        });
        if ($hiddenInput.val() !== '') {
            preferenceRow.populateSelectInput($hiddenInput.val(), selectInputId);
        }
    });

    $(".field-container-select select").change(function(event) {
        var $hiddenInput = $("#" + this.id + "-id"), $educationDegreeInput = $("#" + this.id + "-educationDegree"),
            selectedId, educationDegree, value = $(this).val();
        $(this).children().removeAttr("selected");
        $(this).children("option[value='" + value + "']").attr("selected", "selected");
        selectedId = $("#" + this.id + " option:selected").data("id");
        $hiddenInput.val(selectedId);
        educationDegree = $("#" + this.id + " option:selected").data("educationdegree");
        $educationDegreeInput.val(educationDegree);
        preferenceRow.searchAdditionalQuestions(selectedId, $(this).data("additionalquestions"));
    });
})();
