$(document).ready(function(){


// Organisation search 
// Handle presentation of organisation search form and results

	var orgSearch = {
		settings : {
			listenTimeout : 1000
		},
		build:function(){
			orgSearch.set.listHeight();
			orgSearch.listen.dialogDimensions();
		},
		listen : {
			dialogDimensions:function(){
				// Listen for changes in organisation search height, 
				// and adjust search results list's height accordinly
			
				height = $('#orgsearch').height();
				width = $('#orgsearch').outerWidth(true);
				setTimeout(function(){
					if(height != $('#orgsearch').height())
					{
						orgSearch.set.listHeight();
					}
					if(width != $('#orgsearch').outerWidth(true))
					{
						orgSearch.set.tableCellWidth();
					}
					orgSearch.listen.dialogDimensions();
				}, orgSearch.settings.listenTimeout);
			}
		},
		set : {
			listHeight:function(){
				// Set organisation search result list to fill remaining vertical space
				height = $('#orgsearch').height();
				form_height = $('#orgsearch .orgsearchform').outerHeight(true);
				list_height = height-form_height;
				$('#orgsearch .orgsearchlist').css({'height':list_height+'px'});
			},
			tableCellWidth:function(){
				// Set organisation search dialog's parenting table cell width
				width = $('#orgsearch').outerWidth(true);
				$('#orgsearch').parent('td').css({'width':width+'px'});
			}
		}
	}
	
	orgSearch.build();
	
	
});