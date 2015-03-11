"use strict";

$(document).ready(function() {
	$('fieldset').each(function(item) {
		$(this).find('script').remove();
		var newElem = $('<div class="fieldset"></div>');
		newElem.append( $(this).html() );
		$(this).replaceWith(newElem);
	});
});

var complexRule = {
    init: function(ruleData){
    }
};
