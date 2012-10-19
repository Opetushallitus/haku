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

var popupWindow = {
	defaults :{
		height : 600,
		width : 400,
		resizable : 'yes',
		scrollbars : 'yes',
		toolbar : 'no',
		menubar : 'no',
		location : 'yes',
		directories : 'no',
		status : 'yes'
	},
	build:function(){
		popupWindow.setTriggers();
	},
	generate:function(url, name, settings){
		
		// Get default settings
		var popupSettings = popupWindow.defaults;
		
		// If settings are specified, override defaults
		if(typeof settings != 'undefined')
		{
			settings = settings.split(',');

			for (i in settings)
			{
				setting = settings[i].split('=');
				popupSettings[setting[0]] = setting[1];
			}
		}
		
		// Turn settings into settings string
		var settings = [];
		for (i in popupSettings)
		{
			settings.push(i+'='+popupSettings[i]);
		}
		settings = settings.join(',');

		var popup = window.open(url,name,settings);
		if (window.focus) {popup.focus()}
	},
	close:function(){
		
	},
	setTriggers:function(){
		$('body').on('click', '[data-popup="open"]', function(event){
			event.preventDefault();

				url = $(this).attr('href');
				name = $(this).attr('data-popup-name');
				settings = $(this).attr('data-popup-settings');
				popupWindow.generate(url, name, settings);
		});
		
		$('body').on('click', '[data-popup="close"]', function(event){
			event.preventDefault();
			window.close();
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
				$(this).before(html).css({'left':'-2em'});
				
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
			
			// Set focus
			input.focus();
			input.trigger('click');

			// Change checked status
			if (input.prop('checked') == true || input.attr('checked') == true) {
				replacement.addClass('selected');
			}
			else {
				replacement.removeClass('selected');
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
				$(this).before(html).css({'left':'-2em'});
				
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

			// Determine other radio fields in same set
			name = input.attr('name');
			other_inputs = $('input[name="'+name+'"]:not([data-js-radio-id="'+id+'"])');

			// Set unchecked status on other radio fields in the same set
			other_inputs.each(function(){
				this_id = $(this).attr('data-js-radio-id');
				$('.js-radio[data-js-radio-id="'+this_id+'"]').removeClass('selected');
				$(this).removeAttr('checked');
			});

			// Set checked status and focus
			replacement.addClass('selected');
			input.trigger('click').focus();
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
		// Click, hover
		$('body').on({
			click : function(event){
				event.preventDefault();
				id = parseInt($(this).attr('data-js-checkbox-id'));
				if (!$('.js-checkbox[data-js-checkbox-id="'+id+'"]').hasClass('disabled'))
				{
					formReplacements.jsCheckbox.change(id);
				}
			},
			hover : function(e) {
				id = parseInt($(this).attr('data-js-checkbox-id'));
				if(e.type == "mouseenter") {
					$('.js-checkbox[data-js-checkbox-id="'+id+'"]').addClass('hover');
				}
				else if (e.type == "mouseleave") {
					$('.js-checkbox[data-js-checkbox-id="'+id+'"]').removeClass('hover');
				}
			}
		}, 'label[data-js-checkbox-id]');

		
		// Trigger on replacement radio label
		// Click, hover
		$('body').on({
			click : function(event){
				event.preventDefault();
				id = parseInt($(this).attr('data-js-radio-id'));
				if (!$('.js-radio[data-js-radio-id="'+id+'"]').hasClass('disabled'))
				{
					formReplacements.jsRadio.change(id);
				}
			},
			hover : function(e) {
				id = parseInt($(this).attr('data-js-radio-id'));
				if(e.type == "mouseenter") {
					$('.js-radio[data-js-radio-id="'+id+'"]').addClass('hover');
				}
				else if (e.type == "mouseleave") {
					$('.js-radio[data-js-radio-id="'+id+'"]').removeClass('hover');
				}
			}
		}, 'label[data-js-radio-id]');
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

var loginPopup = {
	build: function() {
		$('.open-login-popup').on('click', function(event) {
			$('#login-popup').fadeIn(400);
			$('#login-popup').removeClass('display-none');
		});

		$('.close-login-popup').on('click', function(event) {
			$('#login-popup').addClass('display-none');
			$('#login-popup').fadeOut(400);
		});
	}
}

var dropDownMenu = {
	// initiate dropDownMenus
	build: function() {
		dropDownMenu.load();
		dropDownMenu.setTriggers();
	},
	// hide (display: none) ul dropdown navigations
	load: function() {
		$('.navigation > li > ul').hide();
		$('.sub-dropdown > ul').hide();
	},
	// set listener for dropdown navigations
	setTriggers: function() {

		$('.navigation > li').hover(navigationMouseOver, navigationMouseOut);
		$('.sub-dropdown').hover(dropdownMouseOver, dropdownMouseOut);

		// bring dropdown navigation visible on mouseover
		function navigationMouseOver() {
			if( $(this).children().filter('ul').length !== 0 ) {
				$(this).children().filter('ul').fadeIn(200);
			}
		}

		// hide dropdown navigation on mouseout
		function navigationMouseOut() {
			if( $(this).children().filter('ul').length !== 0 ) {
				$(this).children().filter('ul').fadeOut(200);
			}
		}

		//bring sub-dropdown navigation visible on mouseover
		function dropdownMouseOver() {
			$(this).children().filter('ul').fadeIn(200);
		}

		//hide sub-dropdown navigation on mouseout
		function dropdownMouseOut() {
			$(this).children().filter('ul').fadeOut(200);
		}
	}
}

applicationBasket.build();
formReplacements.build();
popupWindow.build();
tabsMenu.build();
hierarchyList.build();
loginPopup.build();
dropDownMenu.build();




/* Master.js ends */

});
