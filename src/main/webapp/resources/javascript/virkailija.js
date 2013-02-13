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


    var applicationSearch = (function() {
        var self = this, $q = $('#entry'), $appState = $('#application-state'),
            $fetchPassive = $('#fetch-passive'), $appPreference = $('#application-preference'),
            $tbody = $('#application-table tbody:first'), $resultcount = $('#resultcount'),
            $applicationTabLabel = $('#application-tab-label');

        this.search = function() {

            $.getJSON(page_settings.contextPath + "/applications", {
                q : $q.val(),
                appState : $appState.val(),
                fetchPassive : $fetchPassive.prop("checked"),
                appPreference : $appPreference.val()
            }, function(data) {
                $tbody.empty();
                self.updateCounters(data.length);
                $(data).each(function(index, item) {
                    var henkilotiedot = item.answers.henkilotiedot;
                    $tbody.append('<tr><td><input type="checkbox"/></td><td>' +
                        henkilotiedot.Sukunimi + '</td><td>' +
                        henkilotiedot.Etunimet + '</td><td>' +
                        henkilotiedot.Henkilotunnus + '</td><td><a href="' +
                        page_settings.contextPath + '/virkailija/hakemus/' + item.oid + '/">' +
                        item.oid + '</a></td><td>' + item.state + '</td></tr>');
                });
            });
        },
        this.updateCounters = function(count) {
            $resultcount.empty().append(count);
            $applicationTabLabel.empty().append('Hakemukset ' +  count);
        },
        this.reset = function() {
            self.updateCounters(0);
            $tbody.empty();
            $q.val('');
            $appState.val('');
            $fetchPassive.prop("checked", false);
            $appPreference.val('');
        }
        return this;
    })();


	$('#search-applications').click(function(event){
        applicationSearch.search();
        return false;
    });

    $('#reset-search').click(function(event){
        applicationSearch.reset();
        return false;
    });
});