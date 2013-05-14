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
    var childLONames = {};
    var preferenceRow = {
        populateSelectInput: function (orgId, selectInputId) {
            $.getJSON(sortabletable_settings.koulutusinformaatioBaseUrl + "/ao/search/" + sortabletable_settings.applicationPeriodId + "/" + orgId, {
                prerequisite: sortabletable_settings.tutkintoId,
                vocational: sortabletable_settings.vocational
            }, function (data) {
                var hakukohdeId = $("#" + selectInputId + "-id").val(), $selectInput = $("#" + selectInputId);
                preferenceRow.clearChildLONames($("#" + selectInputId).data("childlonames"));
                $("#" + selectInputId).html("<option></option>");
                var data = [];
                data[0] = {
                    'childLONames': ['aaa', 'bbb'],
                    'id': '1234567890',
                    'name': "name1",
                    'educationDegree': '32'
                };
                data[1] = {
                    'childLONames': ['ccc', 'ddd'],
                    'id': '2345678901',
                    'name': "name2",
                    'educationDegree': '10'
                };
                $(data).each(function (index, item) {
                    var selected = "";
                    childLONames[item.id] = item.childLONames;
                    if (hakukohdeId == item.id) {
                        selected = 'selected = "selected"';
                        // overrides additional questions rendered in the backend
                        //preferenceRow.searchAdditionalQuestions(hakukohdeId, $selectInput.data("additionalquestions"), item.educationDegree, null, false);
                        preferenceRow.displayChildLONames(hakukohdeId, $selectInput.data("childlonames"));
                    }
                    item.lang = (index % 2 == 0) ? "SV" : "FI";
                    item.sora = (index % 2 == 0);
                    $selectInput.append('<option value="' + item.name + '" ' + selected + ' data-id="' + item.id + '" data-educationdegree="' + item.educationDegree + '" data-lang="' + item.lang + '" data-sora="' + item.sora + '">' + item.name + '</option>');
                });
            });
        },

        clearSelectInput: function (selectInputId) {
            $("#" + selectInputId + "-id").val("");
            $("#" + selectInputId + "-educationDegree").val("");
            $("#" + selectInputId + "-id-lang").val("");
            $("#" + selectInputId + "-id-sora").val(false);
            $("#" + selectInputId).html("<option></option>");
            preferenceRow.clearChildLONames($("#" + selectInputId).data("childlonames"));
        },

        searchAdditionalQuestions: function (hakukohdeId, additionalQuestionsId, educationDegree, preferenceRowId, soraRequired) {
            var url = sortabletable_settings.contextPath + "/lomake/" + sortabletable_settings.applicationPeriodId + "/" +
                sortabletable_settings.formId + "/" + sortabletable_settings.vaiheId + "/" +
                sortabletable_settings.teemaId + "/additionalquestions/" + hakukohdeId;

//            $.get(url, {
//                    'ed': educationDegree,
//                    'preferenceRowId': preferenceRowId,
//                    'sora': soraRequired
//                },
//                function (data) {
//                    $("#" + additionalQuestionsId).html(data);
//                });
        },

        displayChildLONames: function (hakukohdeId, childLONamesId) {
            $("#" + childLONamesId).html(childLONames[hakukohdeId]);
            $("#container-" + childLONamesId).show();
        },

        clearChildLONames: function (childLONamesId) {
            $("#container-" + childLONamesId).hide();
            $("#" + childLONamesId).html('');
        }
    };

    $('button.reset').click(function (event) {
        var id = $(this).data('id');
        $('[id|="' + id + '"]').val('').html('');
        preferenceRow.clearSelectInput(id + "-Koulutus");
        $(this).parent().find(".warning").hide();
    });

    $(".field-container-text input:text").each(function (index) {
        var selectInputId = $(this).data('selectinputid');
        var $hiddenInput = $("#" + this.id + "-id");
        $(this).autocomplete({
            minLength: 1,
            source: function (request, response) {
                $.getJSON(sortabletable_settings.koulutusinformaatioBaseUrl + "/lop/search/" + request.term, {
                    asId: sortabletable_settings.applicationPeriodId,
                    prerequisite: sortabletable_settings.tutkintoId,
                    vocational: sortabletable_settings.vocational
                }, function (data) {
                    response($.map(data, function (result) {
                        return {
                            label: result.name,
                            value: result.name,
                            dataId: result.id
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

    $(".field-container-select select").change(function (event) {
        var $hiddenInput = $("#" + this.id + "-id"),
            $educationDegreeInput = $("#" + this.id + "-educationDegree"),
            $educationDegreeLangInput = $("#" + this.id + "-id-lang"),
            $educationDegreeLangSora = $("#" + this.id + "-id-sora"),
            selectedId, educationDegree, value = $(this).val(),
            preferenceRowId = this.id.split("-")[0];
        $(this).children().removeAttr("selected");
        $(this).children("option[value='" + value + "']").attr("selected", "selected");
        var selectedOption = $("#" + this.id + " option:selected");
        selectedId = selectedOption.data("id");
        $hiddenInput.val(selectedId);
        educationDegree = selectedOption.data("educationdegree");
        $educationDegreeInput.val(educationDegree).trigger('change');
        $educationDegreeLangInput.val(selectedOption.data("lang")).trigger('change');
        $educationDegreeLangSora.val(selectedOption.data("sora")).trigger('change');
        preferenceRow.searchAdditionalQuestions(selectedId, $(this).data("additionalquestions"), educationDegree, preferenceRowId, false);
        preferenceRow.displayChildLONames(selectedId, $(this).data("childlonames"));
    });
})();
