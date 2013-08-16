(function($) {
    $.fn.getAttributes = function () {
        var elem = this,
            attr = {};

        if(elem.length) $.each(elem.get(0).attributes, function(v,n) {
            n = n.nodeName||n.name;
            v = elem.attr(n);
            if(v != undefined && v !== false) attr[n] = v
        })

        return attr
    }
})(jQuery);

(function(){
    var sortabletable = {
        moveRow : function(id, targetId) {

            var $clone, $targetClone, $source, $target;

            $source = $('#' + id + "-row-content");
            $clone = $source.clone();
            $target = $('#' + targetId + "-row-content");
            $targetClone = $target.clone();

            var checkedTargetIds = sortabletable.getChecked($source, id, targetId);
            var checkedSourceIds = sortabletable.getChecked($target, targetId, id);

            $source.empty().append($targetClone.children());
            $target.empty().append($clone.children());

            sortabletable.replaceAllAttrSubString($source, targetId, id);
            sortabletable.replaceAllAttrSubString($target, id, targetId);

            sortabletable.replaceRuleSettings($source, targetId, id);
            sortabletable.replaceRuleSettings($target, id, targetId);

            sortabletable.updateChecked(checkedTargetIds);
            sortabletable.updateChecked(checkedSourceIds);

            preferenceRow.init();
        },
        replaceAllAttrSubString : function($elem, findString, replaceWith) {
            $elem.find("*").each(function() {
                var attrs = $(this).getAttributes();
                for (attrName in attrs) {
                    if (attrName) {
                        if (!($(this).hasClass("related-question-rule-class") &&
                            attrName === "id")) {
                            var oldVal =  $(this).attr(attrName);
                            if (oldVal && jQuery.type(oldVal) === "string") {
                                var newVal = oldVal.replace(findString, replaceWith);
                                $(this).prop(attrName, newVal);
                            }
                        }
                    }
                }
                var dataAttrs = $(this).data();
                for (dataName in dataAttrs) {
                    if (dataName) {
                        var oldVal =  $(this).data(dataName);
                        if (oldVal && jQuery.type(oldVal) === "string") {
                            var newVal = oldVal.replace(findString, replaceWith);
                            $(this).data(dataName, newVal);
                        }
                    }
                }
            });
        },

        replaceRuleSettings : function($elem, findString, replaceWith) {
            $elem.find(".related-question-rule-class").each(function() {
                var ruleId = this.id;
                var settings = window[ruleId.replace(new RegExp('-', 'g'), '_')];
                for (var index in settings.childIds) {
                    var cId = settings.childIds[index];
                    settings.childIds[index] = cId.replace(new RegExp(findString, 'g'), replaceWith);
                }
                settings.expression = settings.expression.replace(new RegExp(findString, 'g'), replaceWith);
                settings.relatedSelector = settings.relatedSelector.replace(new RegExp(findString, 'g'), replaceWith);
                $(settings.relatedSelector).unbind();
                $(settings.relatedSelector).change(window[ruleId.replace(new RegExp('-', 'g'), '_') + "_func"]);
            });
        },

        getChecked : function($elem, findIdString, replaceIdWith) {
            var cc  = new Array();
            $elem.find("input:checked").each(function(i) {
                cc[i] = this.id.replace(new RegExp(findIdString, 'g'), replaceIdWith);
            });
            return cc;
        },

        updateChecked: function(checkedElemIds) {
            if (checkedElemIds) {
                $.each(checkedElemIds, function(index, value) {
                    $("#" + value).prop("checked", true);
                });
            }
        }
    };



	$('button.sort').click(function(event){
	    var id = $(this).data('id'),
	        targetId = $(this).data('target');
	    sortabletable.moveRow(id, targetId);
	});
})();
