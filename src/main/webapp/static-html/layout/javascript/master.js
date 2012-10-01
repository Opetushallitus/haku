$(document).ready(function(){
/* Master.js begins */


var formReplacements = {
	settings : {
		checkboxAmount : 0,
		radioAmount : 0
	},
	build:function(){
		// Generate replacements, set triggers
		formReplacements.jsCheckbox.generate();
		formReplacements.setTriggers();
	},
	jsCheckbox: {
		generate:function(){
			var id = 0;
		
			// todo: on rerun, exclude inputs that have js-checkbox
			$('input[type="checkbox"]:not([data-js-checkbox-id])').each(function(){
				
				id = formReplacements.settings.checkboxAmount;
				
				$(this).attr('data-js-checkbox-id', id);
				html = '<span class="js-checkbox" data-js-checkbox-id="'+id+'">&#8302;</span>'
				$(this).before(html).css({'display':'none'});
				
				if($(this).prop('checked') == true || $(this).attr('checked') == true)
				{
					$(this).attr('checked', 'checked');
					$('.js-checkbox[data-js-checkbox-id="'+id+'"]').addClass('selected');
				}
				
				if($(this).prop('disabled') == true || $(this).attr('disabled') == true)
				{
					$(this).attr('disabled', 'disabled');
					$('.js-checkbox[data-js-checkbox-id="'+id+'"]').addClass('disabled');
				}
				
				formReplacements.settings.checkboxAmount = id+1;
			});
		},
		change:function(id){
			console.log(id);
			input = $('input[data-js-checkbox-id="'+id+'"]');
			replacement = $('.js-checkbox[data-js-checkbox-id="'+id+'"]');
			console.log(input);
			console.log(replacement);
			if (replacement.hasClass('selected'))
			{
				replacement.removeClass('selected');
				input.removeAttr('checked');
			}
			else
			{
				replacement.addClass('selected');
				input.attr('checked', 'checked');
			}
		}
	},
	jsRadio:function(){
	
	},
	setTriggers:function(){
		$('body').on('click', '.js-checkbox', function(event){
			event.preventDefault();
			if (typeof $(this).attr('data-js-checkbox-id') != 'undefined' && !$(this).hasClass('disabled'))
			{
				id = parseInt($(this).attr('data-js-checkbox-id'));
				formReplacements.jsCheckbox.change(id);
			}
		});
	}
}

formReplacements.build();



var protoFunctions = {
build:function(){
	
var formStep = {
	stepUrls : {
		1 : 'index.html',
		2 : 'koulutustausta.html',
		3 : 'hakutoiveet.html',
		4 : 'arvosanat.html',
		5 : 'lisatiedot.html',
		6 : 'esikatselu.html',
		7 : 'valmis.html'
	},
	build:function(){
		formStep.setTriggers();
	},
	change:function(param){
		// Next and Previous buttons
		if (param == 'next' || param == 'previous')
		{
			index = parseInt($('form[data-form-step-id]').attr('data-form-step-id'));
			
			if (param == 'next')
			{
				window.location.href = formStep.stepUrls[index+1];
			}
			else
			{
				window.location.href = formStep.stepUrls[index-1];
			}
		}
		else if(param.indexOf('goto') != -1)
		{
			params = param.split(':');
			index = params[1];
			
			window.location.href = formStep.stepUrls[index];
		}

	},
	setTriggers:function(){
		$('button[data-form-step-action]').click(function(event){
			event.preventDefault();
			params = $(this).attr('data-form-step-action');
			formStep.change(params);
		});
		
		$('a[data-form-step-action]').click(function(event){
			event.preventDefault();
			params = $(this).attr('data-form-step-action');
			formStep.change(params);
		});
	}
}
	
	
$('button').click(function(event){
	event.preventDefault();
});
	
	
formStep.build();

}
}

protoFunctions.build();


/* Master.js ends */
});