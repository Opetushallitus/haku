$(document).ready(function(){
/* Master.js begins */


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