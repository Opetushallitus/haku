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
	change:function(params){
		if (params == 'next' || params == 'previous')
		{
			if (params == 'next')
			{
				
			}
			else
			{
				
			}
		}
		else if ()
		{
		
		}
	},
	setTriggers:function(){
		$('button[data-form-step-action]').click(function(event){
			event.preventDefault();
			$(this).attr('data-form-step-action');

		});
	}
	}
	
	
	
	
}
}

protoFunctions.build();


/* Master.js ends */
});