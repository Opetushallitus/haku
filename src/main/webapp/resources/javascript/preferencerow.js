(function(){
	var preferenceRow = {
        populateSelectInput : function(orgId, selectInputId) {
            $.getJSON("/haku/education/hakukohde/search", {
                  organisaatioId : orgId
            }, function(data) {
                var hakukohdeId = $("#" + selectInputId + "-id").val(),
                    $selectInput = $("#" + selectInputId);

                $selectInput.html("<option></option>");
                $(data).each(function(index, item) {
                    var selected = "";
                    if (hakukohdeId == item.id) {
                         selected = 'selected = "selected"';
                         preferenceRow.searchAdditionalQuestions(hakukohdeId, $selectInput.data("additionalquestions"));
                    }
                    $selectInput.append('<option value="' + item.name + '" ' + selected + ' data-id="' + item.id + '">' + item.name + '</option>');
                });
            });
        },

        clearSelectInput : function(selectInputId) {
            $("#" + selectInputId + "-id").val("");
            $("#" + selectInputId).html("<option></option>");

        },

        searchAdditionalQuestions : function(hakukohdeId, additionalQuestionsId) {
            var url = "/haku/education/additionalquestion/" + sortabletable_settings.applicationPeriodId + "/" +
                      sortabletable_settings.formId + "/" + sortabletable_settings.vaiheId + "/" +
                      sortabletable_settings.teemaId + "/" + hakukohdeId;
            $.get(url, function(data) {
              $("#" + additionalQuestionsId).html(data);
            });
        }
    };

	$('button.reset').click(function(event){
	    var id = $(this).data('id');
        $('[id|="' + id + '"]').val('').html('');
	});

	$(".field-container-text input:text").each(function(index) {
	    var selectInputId = $(this).data('selectinputid');
	    var $hiddenInput = $("#" + this.id + "-id");
        $(this).autocomplete({
            minLength : 1,
            source : function(request, response) {
                $.getJSON("/haku/education/organisaatio/search", {
                    term : request.term
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
        if($hiddenInput.val() !== '') {
             preferenceRow.populateSelectInput($hiddenInput.val(), selectInputId);
        }
    });

    $(".field-container-select select").change(function(event){
        var $hiddenInput = $("#" + this.id + "-id"),
            selectedId,
            value = $(this).val();
        $(this).children().removeAttr("selected");
        $(this).children("option[value='" + value + "']").attr( "selected" , "selected");
        selectedId = $("#" + this.id  + " option:selected").data("id");
        $hiddenInput.val(selectedId);
        preferenceRow.searchAdditionalQuestions(selectedId, $(this).data("additionalquestions"));
    });
})();