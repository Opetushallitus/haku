$(document).ready(function(){
/* Master.js begins */


var applicationBasket = {
	build:function(){
		applicationBasket.setTriggers();
	},
	setTriggers:function(){
		// Trigger on replacement radio
		$('body').on('click', '[data-basket-action]', function(event){
			event.preventDefault();
			
			action = $(this).attr('data-basket-action');
			if(action == 'hide'){
				$('[data-basket-action="show"]').delay(400).fadeIn(400);
				$('.application-basket').slideUp(400);
			}
			else if(action == 'show')
			{
				$('[data-basket-action="show"]').hide(0);
				$('.application-basket').slideDown(400);
			}
			
		});
	}
}


var formReplacements = {
	settings : {
		checkboxAmount : 0,
		radioAmount : 0
	},
	build:function(){
		// Generate replacements, set triggers
		formReplacements.jsCheckbox.generate();
		formReplacements.jsRadio.generate();
		formReplacements.setTriggers();
	},
	jsCheckbox : {
		generate:function(){
			var id = 0;
		
			// Generate javascript replacement for all new checkboxes
			$('input[type="checkbox"]:not([data-js-checkbox-id])').each(function(){
				
				// Get id for checkbox
				id = formReplacements.settings.checkboxAmount;
				
				// Add pairing id to input and label
				$(this).attr('data-js-checkbox-id', id);
				field_id = $(this).attr('id');
				$('label[for="'+field_id+'"]').attr('data-js-checkbox-id', id);
				
				// Generate replacement element with pairing id, and hide original input
				html = '<span class="js-checkbox" data-js-checkbox-id="'+id+'">&#8302;</span>'
				$(this).before(html).css({'display':'none'});
				
				// Check & set checked status
				if($(this).prop('checked') == true || $(this).attr('checked') == true)
				{
					$(this).attr('checked', 'checked');
					$('.js-checkbox[data-js-checkbox-id="'+id+'"]').addClass('selected');
				}
				
				// Check & set disabled status
				if($(this).prop('disabled') == true || $(this).attr('disabled') == true)
				{
					$(this).attr('disabled', 'disabled');
					$('.js-checkbox[data-js-checkbox-id="'+id+'"]').addClass('disabled');
				}

				
				// Set id for next checkbox
				formReplacements.settings.checkboxAmount = id+1;
			});
		},
		change:function(id){
		
			// Get paired elements
			input = $('input[data-js-checkbox-id="'+id+'"]');
			replacement = $('.js-checkbox[data-js-checkbox-id="'+id+'"]');

			// Change checked status
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
	jsRadio : {
		generate:function(){
			var id = 0;
		
			// Generate javascript replacement for all new checkboxes
			$('input[type="radio"]:not([data-js-radio-id])').each(function(){
				
				// Get id for radio
				id = formReplacements.settings.radioAmount;
				
				// Add pairing id to input and label
				$(this).attr('data-js-radio-id', id);
				field_id = $(this).attr('id');
				$('label[for="'+field_id+'"]').attr('data-js-radio-id', id);
				
				// Generate replacement element with pairing id, and hide original input
				html = '<span class="js-radio" data-js-radio-id="'+id+'">&#8302;</span>'
				$(this).before(html).css({'display':'none'});
				
				// Check & set checked status
				if($(this).prop('checked') == true || $(this).attr('checked') == true)
				{
					$(this).attr('checked', 'checked');
					$('.js-radio[data-js-radio-id="'+id+'"]').addClass('selected');
				}
				
				// Check & set disabled status
				if($(this).prop('disabled') == true || $(this).attr('disabled') == true)
				{
					$(this).attr('disabled', 'disabled');
					$('.js-radio[data-js-radio-id="'+id+'"]').addClass('disabled');
				}
				
				// Set id for next radio
				formReplacements.settings.radioAmount = id+1;
			});
		},
		change:function(){
		
			// Get paired elements
			input = $('input[data-js-radio-id="'+id+'"]');
			replacement = $('.js-radio[data-js-radio-id="'+id+'"]');
			
			// Set checked status
			replacement.addClass('selected');
			input.attr('checked', 'checked');
			
			// Determine other radio fields in same set
			name = input.attr('name');
			other_inputs = $('input[name="'+name+'"]:not([data-js-radio-id="'+id+'"])');
			
			// Set unchecked status on other radio fields in the same set
			other_inputs.each(function(){
				this_id = $(this).attr('data-js-radio-id');
				$('.js-radio[data-js-radio-id="'+this_id+'"]').removeClass('selected');
				$(this).removeAttr('checked');
			});
		}
	},
	setTriggers:function(){
		// Trigger on replacement checkbox 
		$('body').on('click', '.js-checkbox', function(event){
			event.preventDefault();
			if (typeof $(this).attr('data-js-checkbox-id') != 'undefined' && !$(this).hasClass('disabled'))
			{
				id = parseInt($(this).attr('data-js-checkbox-id'));
				formReplacements.jsCheckbox.change(id);
			}
		});
		
		// Trigger on replacement radio
		$('body').on('click', '.js-radio', function(event){
			event.preventDefault();
			if (typeof $(this).attr('data-js-radio-id') != 'undefined' && !$(this).hasClass('disabled'))
			{
				id = parseInt($(this).attr('data-js-radio-id'));
				formReplacements.jsRadio.change(id);
			}
		});
		
		// Trigger on replacement checkbox label
		// Click
		$('body').on('click', 'label[data-js-checkbox-id]', function(event){
			event.preventDefault();
			id = parseInt($(this).attr('data-js-checkbox-id'));
			if (!$('.js-checkbox[data-js-checkbox-id="'+id+'"]').hasClass('disabled'))
			{
				formReplacements.jsCheckbox.change(id);
			}
		});
		
		// Hover
		$('body').on("hover", 'label[data-js-checkbox-id]', function(e) {
			id = parseInt($(this).attr('data-js-checkbox-id'));
			if(e.type == "mouseenter") {
				$('.js-checkbox[data-js-checkbox-id="'+id+'"]').addClass('hover');
			}
			else if (e.type == "mouseleave") {
				$('.js-checkbox[data-js-checkbox-id="'+id+'"]').removeClass('hover');
			}
		});
		
		// Trigger on replacement radio label
		// Click
		$('body').on('click', 'label[data-js-radio-id]', function(event){
			event.preventDefault();
			id = parseInt($(this).attr('data-js-radio-id'));
			if (!$('.js-radio[data-js-radio-id="'+id+'"]').hasClass('disabled'))
			{
				formReplacements.jsRadio.change(id);
			}
		});
		
		// Hover
		$('body').on("hover", 'label[data-js-radio-id]', function(e) {
			id = parseInt($(this).attr('data-js-radio-id'));
			if(e.type == "mouseenter") {
				$('.js-radio[data-js-radio-id="'+id+'"]').addClass('hover');
			}
			else if (e.type == "mouseleave") {
				$('.js-radio[data-js-radio-id="'+id+'"]').removeClass('hover');
			}
		});
	}
}

var tabsMenu = {
		build:function(){
			tabsMenu.load();
			tabsMenu.setTriggers();
		},
		load:function()
		{
			
			if(window.location.hash)
			{
				hash = window.location.hash.substr(1);
				key = "tabsheet";
				hashparams = hash.split(':');
				id = hashparams[1];

				if(hash.indexOf(key) != -1)
				{

					$('.tabs .tab[data-tabs-id="'+hashparams[1]+'"]').each(function(){
					
						group = $(this).attr('data-tabs-group');
						
						$('.tabsheet[data-tabs-group="'+group+'"]').hide();
						$('.tabs .tab[data-tabs-group="'+group+'"]').removeClass('current');
						$('.tabsheet[data-tabs-group="'+group+'"][data-tabs-id="'+id+'"]').show();
						$('.tabs .tab[data-tabs-group="'+group+'"][data-tabs-id="'+id+'"]').addClass('current');
					});
				}
			}
		
			$('.tabs .tab').click(function(event){
				event.preventDefault();
				group = $(this).attr('data-tabs-group');
				id = $(this).attr('data-tabs-id');
				
				$('.tabs .tab[data-tabs-group="'+group+'"]').removeClass('current');
				$(this).addClass('current');
				
				$('.tabsheet[data-tabs-group="'+group+'"]').hide();
				
				$('.tabsheet[data-tabs-group="'+group+'"][data-tabs-id="'+id+'"]').show();
			});
		},
		setTriggers:function(){
			$('.tabs .tab').click(function(event){
				event.preventDefault();
				group = $(this).attr('data-tabs-group');
				id = $(this).attr('data-tabs-id');
				
				$('.tabs .tab[data-tabs-group="'+group+'"]').removeClass('current');
				$(this).addClass('current');
				
				$('.tabsheet[data-tabs-group="'+group+'"]').hide();
				
				$('.tabsheet[data-tabs-group="'+group+'"][data-tabs-id="'+id+'"]').show();
			});
		}
	}
/*
$('[data-basket-action="show"]').delay(400).fadeIn(400);
				$('.application-basket').slideUp(400);
*/
var hierarchyList = {
	build: function() {
		hierarchyList.load();
		hierarchyList.setTriggers();
	},
	setTriggers: function() {
		$('body').on('click', '[hierarchy-list-action]', function(event) {
			event.preventDefault();
			if($(this).attr('hierarchy-list-action') === 'closed') {
				$(this).attr('hierarchy-list-action', 'open');
				$(this).children().filter('ul').slideDown(200);
				$(this).removeClass('closed');
				$(this).addClass('open');

			} else {
				$(this).attr('hierarchy-list-action', 'closed');
				$(this).children().filter('ul').slideUp(200);
				$(this).removeClass('open');
				$(this).addClass('closed');
			}
			
		});
	},
	load: function () {
		$('.hierarchy-list .lvl-1 > li').each(function() {
			if( $(this).attr('hierarchy-list-action') === 'open') {
				$(this).attr('hierarchy-list-action', 'closed')
				$(this).removeClass('open');
				$(this).addClass('closed');
			}
		});
	} 
}

applicationBasket.build();
formReplacements.build();
tabsMenu.build();
hierarchyList.build();


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