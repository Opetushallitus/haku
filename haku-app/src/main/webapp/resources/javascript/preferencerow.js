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
var lopCache = {};
var preferenceRow = {
    populateSelectInput: function (orgId, selectInputId, isInit, providerInputId) {
        
            $.getJSON(sortabletable_settings.koulutusinformaatioBaseUrl + "/ao/search/" + sortabletable_settings.applicationSystemId + "/" + orgId,
            {
                baseEducation: sortabletable_settings.baseEducation,
                vocational: sortabletable_settings.vocational,
                uiLang: sortabletable_settings.uiLang
            },
            function (data) {
                var hakukohdeId = $("#" + selectInputId + "-id").val(), $selectInput = $("#" + selectInputId),
                    selectedPreferenceOK = false;

                preferenceRow.clearChildLONames($("#" + selectInputId).data("childlonames"));
                $("#" + selectInputId).html("<option value=''>&nbsp;</option>");

                $(data).each(function (index, item) {
                    var selected = "";
                    childLONames[item.id] = item.childLONames;
                    if (hakukohdeId == item.id) {
                        selectedPreferenceOK = true;
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
                        '" data-kaksoistutkinto="' + item.kaksoistutkinto +
                        '" data-vocational="' + item.vocational +
                        '" data-educationcode="' + item.educationCodeUri +
                        '" data-athlete="' + item.athleteEducation + '" >' + item.name + '</option>');
                });
                if (isInit && !selectedPreferenceOK && hakukohdeId && hakukohdeId !== '') {
                    $selectInput.parent().find(".warning").hide();
                    var $providerInput = $("#" + providerInputId),
                        warning = '<div class="notification warning margin-top-1"><span>' +
                            sortabletable_settings.preferenceAndBaseEducationConflictMessage +
                            '</span><span><small>' +
                            $providerInput.val() +
                            '</small></span><span><small>' +
                            $selectInput.data('selectedname') +
                            '</small></span></div>';
                    $('[id|="' + providerInputId + '"]').val('');
                    preferenceRow.clearSelectInput(selectInputId);
                    $selectInput.after(warning);
                }
            });
    },

    clearSelectInput: function (selectInputId) {
        $("#" + selectInputId + "-id").val("").change();
        $("#" + selectInputId + "-educationDegree").val("").change();
        $("#" + selectInputId + "-id-lang").val("").change();
        $("#" + selectInputId + "-id-sora").val(false).change();
        $("#" + selectInputId + "-id-aoIdentifier").val("").change();
        $("#" + selectInputId + "-id-kaksoistutkinto").val(false).change();
        $("#" + selectInputId + "-id-vocational").val(false).change();
        $("#" + selectInputId + "-id-educationcode").val(false).change();
        $("#" + selectInputId + "-id-athlete").val(false).change();
        $("#" + selectInputId).html("<option>&nbsp;</option>");
        preferenceRow.clearChildLONames($("#" + selectInputId).data("childlonames"));
    },

    displayChildLONames: function (hakukohdeId, childLONamesId) {
        var $names =  $("#" + childLONamesId), data = '<ol class="list-style-none">', loNames = childLONames[hakukohdeId];

        if (loNames && loNames.length > 0) {
            for (var index in loNames) {
                data = data.concat("<li><small>", loNames[index], "</small></li>");
            }
            data = data.concat("</ol>");
            $names.html(data);
            $("#container-" + childLONamesId).show();
        } else {
            preferenceRow.clearChildLONames(childLONamesId);
        }
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
            $('[id|="' + id + '"]').change();
        });

//        $(".field-container-text input:text#preferenceLopInput").each(function (index) {
            $('[data-special-id="preferenceLopInput"]').each(function (index) {
            var selectInputId = $(this).data('selectinputid');
            var $hiddenInput = $("#" + this.id + "-id");
            //$(this).autocomplete = null;
            //$(this).unbind();
            $(this).autocomplete({
                minLength: 1,
                source: function (request, response) {
                    var term = request.term;
                    if ( term in lopCache ) {
                        response($.map(lopCache[ term ], function (result) {
                            return {
                                label: result.name,
                                value: result.name,
                                dataId: result.id
                            }
                        }));
                        return;
                    }
                    var lopParams = {
                        asId: sortabletable_settings.applicationSystemId,
                        vocational: sortabletable_settings.vocational,
                        start: 0,
                        rows: 999999,
                        lang: sortabletable_settings.uiLang
                    }
                    if (sortabletable_settings.baseEducation) {
                        lopParams.baseEducation = sortabletable_settings.baseEducation;
                    }
                    $.getJSON(sortabletable_settings.koulutusinformaatioBaseUrl + "/lop/search/" + encodeURI(request.term),
                            lopParams, function (data) {
                        lopCache[request.term] = data;
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
                    preferenceRow.populateSelectInput(ui.item.dataId, selectInputId, false, this.id);
                },
                change: function (ev, ui) {
                    if (!ui.item) {
                        $(this).val("");
                        $hiddenInput.val("");
                        preferenceRow.clearSelectInput(selectInputId);
                    }
                }
            });
            $(this).focus(function(event) {
                wasOpen = $(this).autocomplete( "widget" ).is( ":visible" );
                if (!wasOpen && (!$(this).val() || $(this).val() === '')) {
                    $(this).autocomplete("search", "*");
                }
            });
            if ($hiddenInput.val() && $hiddenInput.val() !== '') {
                preferenceRow.populateSelectInput($hiddenInput.val(), selectInputId, true, this.id);
            }
        });
        $(".field-container-select select").unbind();
        var selectChange = function (event) {
                                       var $hiddenInput = $("#" + this.id + "-id"),
                                           $educationDegreeInput = $("#" + this.id + "-educationDegree"),
                                           $educationDegreeLang = $("#" + this.id + "-id-lang"),
                                           $educationDegreeSora = $("#" + this.id + "-id-sora"),
                                           $educationDegreeKaksoistutkinto = $("#" + this.id + "-id-kaksoistutkinto"),
                                           $educationDegreeVocational = $("#" + this.id + "-id-vocational"),
                                           $educationDegreeAoIdentifier = $("#" + this.id + "-id-aoIdentifier"),
                                           $educationDegreeAthlete = $("#" + this.id + "-id-athlete"),
                                           $educationDegreeEducationCode = $("#" + this.id + "-id-educationcode"),
                                           selectedId, educationDegree, value = $(this).val(),
                                           preferenceRowId = this.id.split("-")[0];
                                       $(this).parent().find(".warning").hide();
                                       $(this).children().removeAttr("selected");
                                       $(this).children("option[value='" + value + "']").attr("selected", "selected");
                                       var selectedOption = $("#" + this.id + " option:selected");
                                       selectedId = selectedOption.data("id");
                                       $hiddenInput.val(selectedId).change();
                                       var educationDegree = selectedOption.data("educationdegree");
                                       $educationDegreeInput.val(educationDegree).change();
                                       $educationDegreeLang.val(selectedOption.data("lang")).change();
                                       $educationDegreeSora.val(selectedOption.data("sora")).change();
                                       $educationDegreeKaksoistutkinto.val(selectedOption.data("kaksoistutkinto")).change();
                                       $educationDegreeVocational.val(selectedOption.data("vocational")).change();
                                       $educationDegreeAoIdentifier.val(selectedOption.data("aoidentifier")).change();
                                       $educationDegreeAthlete.val(selectedOption.data("athlete")).change();
                                       $educationDegreeEducationCode.val(selectedOption.data("educationcode")).change();
                                       preferenceRow.displayChildLONames(selectedId, $(this).data("childlonames"));
                                   };
        $('button[name=phaseId]').click(selectChange);
        $(".field-container-select select").change(selectChange);
    }
};

preferenceRow.init();

