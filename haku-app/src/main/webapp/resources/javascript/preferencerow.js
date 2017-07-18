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
var attachments = {};
var lopCache = {};

var preferenceRow = {
    searchLOP: function(term, response) {
        function resultToResponse(result) {
            return {
                label: result.name,
                value: result.name,
                dataId: result.id
            }
        }
        if (term in lopCache) {
            response($.map(lopCache[term], resultToResponse));
            return;
        }
        var lopParams = {
            asId: sortabletable_settings.applicationSystemId,
            vocational: sortabletable_settings.vocational,
            start: 0,
            rows: 999999,
            ongoing: sortabletable_settings.ongoing,
            lang: sortabletable_settings.uiLang
        };
        if (sortabletable_settings.baseEducation) {
            lopParams.baseEducation = sortabletable_settings.baseEducation;
        }
        $.getJSON(window.url("koulutusinformaatio-app.lop.search", term, lopParams), function(data) {
                lopCache[term] = data;
                response($.map(data, resultToResponse));
            });
    },

    populateSelectInput: function(orgId, selectInputId, isInit, providerInputId) {
        $.getJSON(window.url("koulutusinformaatio-app.ao.search", sortabletable_settings.applicationSystemId, orgId, {
                baseEducation: sortabletable_settings.baseEducation,
                vocational: sortabletable_settings.vocational,
                uiLang: sortabletable_settings.uiLang,
                ongoing: sortabletable_settings.ongoing
            }),
            function (data) {
                data = _.sortBy(data, 'name');

                // Filter already selected items from dropdown to prevent duplicates
                var koulutusIdElements = $( "input[name$='Koulutus-id']" );
                koulutusIdElements = _.filter(koulutusIdElements, function (e) {
                    return !e.id.startsWith(selectInputId); // don't check against element currently being populated
                });
                var selectedKoulutusIds = _.pluck(koulutusIdElements, 'value');
                if (selectedKoulutusIds) {
                    data = _.filter(data, function (item) {
                        return !selectedKoulutusIds.includes(item.id);
                    })
                }

                var hakukohdeId = $("#" + selectInputId + "-id").val();
                var $selectInput = $("#" + selectInputId);
                var selectedPreferenceOK = false;

                $selectInput.prop('readonly', true);
                preferenceRow.clearChildLONames($selectInput.data("childlonames"));

                $selectInput.html("<option value=''>&nbsp;</option>");

                $(data).each(function (index, item) {
                    var selected = null;
                    childLONames[item.id] = item.childLONames;
                    if (item.attachments) {
                        attachments[item.id] = item.attachments;
                    }
                    if (hakukohdeId == item.id) {
                        selectedPreferenceOK = true;
                        selected = "selected";
                        // overrides additional questions rendered in the backend
                        preferenceRow.displayChildLONames(hakukohdeId, $selectInput.data("childlonames"));
                    }
                    var organizationGroups = item.organizationGroups || [];
                    var aoGroups = new Array();
                    var attachmentGroups = new Array();
                    for (var i = 0; i < organizationGroups.length; i++) {
                        var group = organizationGroups[i];
                        var groupTypes = group.groupTypes;
                        var isAoGroup = false;
                        for (var j = 0; j < groupTypes.length; j++) {
                            if (groupTypes[j] === 'hakukohde') {
                                isAoGroup = true;
                                break;
                            }
                        }
                        if (!isAoGroup) { continue; }
                        aoGroups.push(group.oid);
                        var usages = group.usageGroups;
                        var isAttachmentGroup = false;
                        for (var j = 0; j < usages.length; j++) {
                            if (usages[j] === 'hakukohde_liiteosoite') {
                                isAttachmentGroup = true;
                                break;
                            }
                        }
                        if (!isAttachmentGroup) { continue; }
                        attachmentGroups.push(group.oid);
                    }

                    var hasAttachments = false;
                    if (item.attachments) {
                        hasAttachments = true;
                    }

                    function addAttributes(elem, map) {
                        for (var property in map) {
                            if (map.hasOwnProperty(property)) {
                                var value = map[property];
                                if(value != undefined) {
                                    elem.attr(property, value)
                                }
                            }
                        }
                        return elem;
                    }

                    var option = addAttributes($("<option/>"), {
                        "value": item.name,
                        "selected": selected,
                        "data-id": item.id,
                        "data-educationdegree": item.educationDegree,
                        "data-requiredbaseeducations": item.requiredBaseEducations ? item.requiredBaseEducations.join(",") : '',
                        "data-lang": item.teachingLanguages[0],
                        "data-sora": item.sora,
                        "data-aoidentifier": item.aoIdentifier,
                        "data-ao-groups": aoGroups.join(","),
                        "data-kaksoistutkinto": item.kaksoistutkinto,
                        "data-vocational": item.vocational,
                        "data-educationcode": item.educationCodeUri,
                        "data-attachments": hasAttachments,
                        "data-attachmentgroups": attachmentGroups.join(","),
                        "data-athlete": item.athleteEducation,
                        "data-discretionary": item.kysytaanHarkinnanvaraiset
                    });
                    option.text(item.name);
                    $selectInput.append(option);
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

                $selectInput.prop('readonly', false);

                var idx = selectInputId.indexOf('-');
                var id = selectInputId.substring(0, idx);
                var opetuspiste = $("#" + id + "-Opetuspiste").val();
                var hakukohde = $("#" + id + "-Koulutus").val();
                $('#'+id+'-reset').attr('aria-label', $('#'+id+'-reset').data('label') + ': ' + opetuspiste + ", " + hakukohde);

            });
    },

    clearSelectInput: function (selectInputId) {
        $("#" + selectInputId + "-id").val("").change();
        $("#" + selectInputId + "-educationDegree").val("").change();
        $("#" + selectInputId + "-requiredBaseEducations").val("").change();
        $("#" + selectInputId + "-id-lang").val("").change();
        $("#" + selectInputId + "-id-sora").val(false).change();
        $("#" + selectInputId + "-id-aoIdentifier").val("").change();
        $("#" + selectInputId + "-id-ao-groups").val("").change();
        $("#" + selectInputId + "-id-kaksoistutkinto").val(false).change();
        $("#" + selectInputId + "-id-vocational").val(false).change();
        $("#" + selectInputId + "-id-educationcode").val(false).change();
        $("#" + selectInputId + "-id-athlete").val(false).change();
        $("#" + selectInputId + "-id-attachments").val("").change();
        $("#" + selectInputId + "-id-attachmentgroups").val("").change();
        $("#" + selectInputId + "-id-discretionary").val(false).change();
        $("#" + selectInputId).html("<option>&nbsp;</option>");
        preferenceRow.clearChildLONames($("#" + selectInputId).data("childlonames"));
        "${sortableItem.id}-reset"
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

    translateLop: function(lopOid, field) {
        var lopParams = {
            lang: sortabletable_settings.uiLang
        };
        if(lopOid !== undefined && lopOid !== ""){
            $.getJSON(window.url("koulutusinformaatio-app.lop", lopOid, lopParams), function(data) {
                $(field).attr("value", data.name);
            });
        }
    },

    init : function () {
        $('button.reset').unbind();
        $('button.reset').click(function (event) {
            var id = $(this).data('id');
            $('[id|="' + id + '"]').prop("readonly", false);
            $('[id|="' + id + '"]').val('');
            preferenceRow.clearSelectInput(id + "-Koulutus");
            $(this).parent().find(".warning").hide();
            $('[id|="' + id + '"]').change();
            $(this).attr('aria-label', $(this).data('label'))
        });

        var $lopInputs = $('[data-special-id="preferenceLopInput"]');
        if ($lopInputs.first().is('select')) {
            preferenceRow.searchLOP("*", function(options) {
                $lopInputs.each(function() {
                    var html = '<option value=""></option>';
                    var $select = $(this);
                    options.forEach(function(o) {
                        var selected = ($("#" + $select.attr('id') + "-id").val() === o.dataId ? ' selected' : '');
                        html += '<option data-id="' + o.dataId + '" value="' + o.value + '"' + selected + '>' + o.label + '</option>';
                    });
                    $select.html(html);
                    $select.change(function(event) {
                        var $option = $(event.target).find(":selected");
                        preferenceRow.clearSelectInput($select.data('id') + "-Koulutus");
                        preferenceRow.populateSelectInput($option.data('id'), $(event.target).data('selectinputid'), false, event.target.id);
                    })
                });
            });
            $lopInputs.each(function() {
                var selectInputId = $(this).data('selectinputid');
                var $hiddenInput = $("#" + this.id + "-id");
                if ($hiddenInput.val() && $hiddenInput.val() !== '') {
                    $(this).prop("readonly", true);
                    preferenceRow.populateSelectInput($hiddenInput.val(), selectInputId, true, this.id);
                }
            });
        } else {
            $lopInputs.each(function(index) {
                var selectInputId = $(this).data('selectinputid');
                var $hiddenInput = $("#" + this.id + "-id");

                // translate prefilled lops
                preferenceRow.translateLop($hiddenInput.attr("value"), $lopInputs[index]);

                $(this).autocomplete({
                    minLength: 1,
                    source: function(request, response) {
                        preferenceRow.searchLOP(request.term, response);
                    },
                    select: function(event, ui) {
                        $hiddenInput.val(ui.item.dataId);
                        preferenceRow.clearSelectInput(selectInputId);
                        preferenceRow.populateSelectInput(ui.item.dataId, selectInputId, false, this.id);
                        var idx = selectInputId.indexOf('-');
                        var id = selectInputId.substring(0, idx);
                        $('#'+id+'-reset').attr('aria-label', $('#'+id+'-reset').data('label') + ': ' + ui.item.label);
                        $(this).prop("readonly", true);
                    },
                    change: function(ev, ui) {
                        if (!ui.item) {
                            $(this).val("");
                            $hiddenInput.val("");
                            preferenceRow.clearSelectInput(selectInputId);
                        }
                    }
                });
                $(this).focus(function(event) {
                    wasOpen = $(this).autocomplete("widget").is(":visible");
                    if (!wasOpen && (!$(this).val() || $(this).val() === '')) {
                        $(this).autocomplete("search", "*");
                    }
                });
                if ($hiddenInput.val() && $hiddenInput.val() !== '') {
                    $(this).prop("readonly", true);
                    preferenceRow.populateSelectInput($hiddenInput.val(), selectInputId, true, this.id);
                }
            });
        }
        $(".field-container-select select").unbind();
        var selectChange = function (event) {
                                       var $hiddenInput = $("#" + this.id + "-id"),
                                           $educationDegreeInput = $("#" + this.id + "-educationDegree"),
                                           $educationDegreeRequiredBaseEdInput = $("#" + this.id + "-requiredBaseEducations"),
                                           $educationDegreeLang = $("#" + this.id + "-id-lang"),
                                           $educationDegreeSora = $("#" + this.id + "-id-sora"),
                                           $educationDegreeKaksoistutkinto = $("#" + this.id + "-id-kaksoistutkinto"),
                                           $educationDegreeVocational = $("#" + this.id + "-id-vocational"),
                                           $educationDegreeAoIdentifier = $("#" + this.id + "-id-aoIdentifier"),
                                           $educationOptionGroups = $("#" + this.id + "-id-ao-groups"),
                                           $educationDegreeAthlete = $("#" + this.id + "-id-athlete"),
                                           $educationAttachments = $("#" + this.id + "-id-attachments"),
                                           $educationAttachmentGroups = $("#" + this.id + "-id-attachmentgroups"),
                                           $educationDiscretionary = $("#" + this.id + "-id-discretionary"),
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
                                       $educationDegreeRequiredBaseEdInput.val(selectedOption.data("requiredbaseeducations")).change();
                                       $educationDegreeLang.val(selectedOption.data("lang")).change();
                                       $educationDegreeSora.val(selectedOption.data("sora")).change();
                                       $educationDegreeKaksoistutkinto.val(selectedOption.data("kaksoistutkinto")).change();
                                       $educationDegreeVocational.val(selectedOption.data("vocational")).change();
                                       $educationDegreeAoIdentifier.val(selectedOption.data("aoidentifier")).change();
                                       $educationOptionGroups.val(selectedOption.data("ao-groups")).change();
                                       $educationDegreeAthlete.val(selectedOption.data("athlete")).change();
                                       $educationAttachments.val(selectedOption.data("attachments")).change();
                                       $educationAttachmentGroups.val(selectedOption.data("attachmentgroups")).change();
                                       $educationDiscretionary.val(selectedOption.data("discretionary")).change();
                                       $educationDegreeEducationCode.val(selectedOption.data("educationcode")).change();
                                       preferenceRow.displayChildLONames(selectedId, $(this).data("childlonames"));
                                       var idx = this.id.indexOf('-');
                                       var id = this.id.substring(0, idx);
                                       var opetuspiste = $("#" + id + "-Opetuspiste").val();
                                       var hakukohde = $("#" + id + "-Koulutus").val();
                                       $('#'+id+'-reset').attr('aria-label',
                                               $('#'+id+'-reset').data('label') + ': ' + opetuspiste + ", " + hakukohde);

                                   };
//        $('button[name=phaseId]').click(function() {throw new Error("foo")});
        $(".field-container-select select").change(selectChange);

    }
};

preferenceRow.init();
