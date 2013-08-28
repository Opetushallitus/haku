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

var childLONames = {};
var preferenceRow = {
    populateSelectInput: function (orgId, selectInputId) {
        $.getJSON(sortabletable_settings.koulutusinformaatioBaseUrl + "/ao/search/" + sortabletable_settings.applicationSystemId + "/" + orgId,
            {
                'baseEducation': sortabletable_settings.baseEducation,
                'vocational': sortabletable_settings.vocational
            },
            function (data) {
                var hakukohdeId = $("#" + selectInputId + "-id").val(), $selectInput = $("#" + selectInputId);

                preferenceRow.clearChildLONames($("#" + selectInputId).data("childlonames"));
                $("#" + selectInputId).html("<option></option>");

                $(data).each(function (index, item) {
                    var selected = "";
                    childLONames[item.id] = item.childLONames;
                    if (hakukohdeId == item.id) {
                        selected = 'selected = "selected"';
                        // overrides additional questions rendered in the backend
                        preferenceRow.displayChildLONames(hakukohdeId, $selectInput.data("childlonames"));
                    }
                    $selectInput.append('<option value="' + item.name
                        + '" ' + selected + ' data-id="' + item.id +
                        '" data-educationdegree="' + item.educationDegree +
                        '" data-lang="' + item.teachingLanguages[0] +
                        '" data-sora="' + item.sora +
                        '" data-aoidentifier="' + item.aoIdentifier +
                        '" data-athlete="' + item.athleteEducation + '" >' + item.name + '</option>');
                });
            });
    },

    clearSelectInput: function (selectInputId) {
        $("#" + selectInputId + "-id").val("");
        $("#" + selectInputId + "-educationDegree").val("").change();
        $("#" + selectInputId + "-id-lang").val("").change();
        $("#" + selectInputId + "-id-sora").val(false).change();
        $("#" + selectInputId + "-id-aoIdentifier").val("").change();
        $("#" + selectInputId + "-id-athlete").val(false).change();
        $("#" + selectInputId).html("<option></option>");
        preferenceRow.clearChildLONames($("#" + selectInputId).data("childlonames"));
    },

    displayChildLONames: function (hakukohdeId, childLONamesId) {
        var $names =  $("#" + childLONamesId), data = '<ol class="list-style-none">', loNames = childLONames[hakukohdeId];

        for (var index in loNames) {
            data = data.concat("<li><small>", loNames[index], "</small></li>");
        }
        data = data.concat("</ol>");
        $names.html(data);
        $("#container-" + childLONamesId).show();
    },

    clearChildLONames: function (childLONamesId) {
        $("#container-" + childLONamesId).hide();
        $("#" + childLONamesId).html('');
    },

    init : function () {
        $('button.reset').unbind();
        $('button.reset').click(function (event) {
            var id = $(this).data('id');

            $('[id|="' + id + '"]').val('');
            preferenceRow.clearSelectInput(id + "-Koulutus");
            $(this).parent().find(".warning").hide();
        });

        $(".field-container-text input:text").each(function (index) {
            var selectInputId = $(this).data('selectinputid');
            var $hiddenInput = $("#" + this.id + "-id");
            //$(this).autocomplete = null;
            //$(this).unbind();
            $(this).autocomplete({
                minLength: 1,
                source: function (request, response) {
                    $.getJSON(sortabletable_settings.koulutusinformaatioBaseUrl + "/lop/search/" + encodeURI(request.term), {
                        asId: sortabletable_settings.applicationSystemId,
                        baseEducation: sortabletable_settings.baseEducation
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
                    preferenceRow.clearSelectInput(selectInputId);
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
        $(".field-container-select select").unbind();
        $(".field-container-select select").change(function (event) {
            var $hiddenInput = $("#" + this.id + "-id"),
                $educationDegreeInput = $("#" + this.id + "-educationDegree"),
                $educationDegreeLang = $("#" + this.id + "-id-lang"),
                $educationDegreeSora = $("#" + this.id + "-id-sora"),
                $educationDegreeAoIdentifier = $("#" + this.id + "-id-aoIdentifier"),
                $educationDegreeAthlete = $("#" + this.id + "-id-athlete"),
                selectedId, educationDegree, value = $(this).val(),
                preferenceRowId = this.id.split("-")[0];
            $(this).children().removeAttr("selected");
            $(this).children("option[value='" + value + "']").attr("selected", "selected");
            var selectedOption = $("#" + this.id + " option:selected");
            selectedId = selectedOption.data("id");
            $hiddenInput.val(selectedId);
            var educationDegree = selectedOption.data("educationdegree");
            $educationDegreeInput.val(educationDegree).change();
            $educationDegreeLang.val(selectedOption.data("lang")).change();
            $educationDegreeSora.val(selectedOption.data("sora")).change();
            $educationDegreeAoIdentifier.val(selectedOption.data("aoidentifier")).change();
            $educationDegreeAthlete.val(selectedOption.data("athlete")).change();
            preferenceRow.searchAdditionalQuestions(selectedId, $(this).data("additionalquestions"), educationDegree, preferenceRowId, false);
            preferenceRow.displayChildLONames(selectedId, $(this).data("childlonames"));
        });
    }
};
preferenceRow.init();
