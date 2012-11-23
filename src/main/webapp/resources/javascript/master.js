

$(document).ready(function(){
	/* Master.js begins */
	var defaultActions = {	
		build: function() {
			if($.browser.msie){
			   $('html').addClass('ie' + $.browser.version.substring(0,1));
			}

			$('body').on('click', 'a[href="#"]', function(event){
			    event.preventDefault();
			});

			$('body').on('click', '.button.disabled', function(event){
			    event.preventDefault();
			});


			$('.itemtree .collapser').click(function(event){
			    event.preventDefault();
			    
			    if($(this).parent().hasClass('collapsed'))
			    {
			        $(this).parent().removeClass('collapsed');
			    }
			    else
			    {
			        $(this).parent().addClass('collapsed');
			    }
			});

			$('.collapsible').each(function(){
			    html = '<span class="collapser">&#8203;</span>';
			    $(this).prepend(html);
			});

			$('.collapsible .collapser').click(function(event){
			    event.preventDefault();
			    
			    if($(this).parent().hasClass('collapsed'))
			    {
			        $(this).parent().removeClass('collapsed');
			    }
			    else
			    {
			        $(this).parent().addClass('collapsed');
			    }
			});


			$('#sidebar .expander').click(function(event){
			    event.preventDefault();
			    if($(this).parents('#sidebar').hasClass('expand'))
			    {
			        $(this).parents('#sidebar').removeClass('expand');
			    }
			    else
			    {
			        $(this).parents('#sidebar').addClass('expand');
			    }
			});


			$('input[data-get-random-id]').blur(function(){
			    randomnumber=Math.floor(Math.random()*10000000000);

			    id = $(this).attr('data-get-random-id');
			    $('[data-set-random-id="'+id+'"]').text(randomnumber);
			    
			});
		}

	}


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
			
			//add overlay div for login-popup dynamically
			var overlayDiv = '<div class="popup-overlay display-none"></div>';
			$('#site').before(overlayDiv);

			//trigger listeners to open and close login-popup
			$('.open-login-popup').on('click', showPopup);
			$('.close-login-popup').on('click', hidePopup);
			$('.popup-overlay').on('click', hidePopup);

			//show popup
			function showPopup(event) {
				$('#login-popup').fadeIn(400).removeClass('display-none');
				$('.popup-overlay').fadeIn(400).removeClass('display-none');
			}

			//hide popup
			function hidePopup(event) {
				$('#login-popup').fadeOut(400).addClass('display-none');
				$('.popup-overlay').fadeOut(400).addClass('display-none');
			}
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

	var tableRowHighlight = {
		build: function() {
			tableRowHighlight.setTriggers();
		},

		//change highlight when user clicks checkbox or label attached to it
		setTriggers: function() {
			
			$('.application-table tbody .js-checkbox').on('click', function() {
				if($(this).parents('tr').hasClass('table-highlight')) {
					$(this).parents('tr').removeClass('table-highlight');

				} else {
					$(this).parents('tr').addClass('table-highlight');
				}
			});

			$('.application-table tbody .js-checkbox').siblings().filter('label').on('click', function() {
				if($(this).parents('tr').hasClass('table-highlight')) {
					$(this).parents('tr').removeClass('table-highlight');

				} else {
					$(this).parents('tr').addClass('table-highlight');
				}
			});
		},

		//set or remove highlight
		changeHighlight: function() {
			if($(this).parents('tr').hasClass('table-highlight')) {
				$(this).parents('tr').removeClass('table-highlight');

			} else {
				$(this).parents('tr').addClass('table-highlight');
			}
		}
	}

	// sort rows of tables tbody alphabetically
	var tableSorter = {

		build: function() {
			tableSorter.tableSorter();
		},

		tableSorter: function() {
			$('table td.sortable, table td.sortAscending, table td.sortDescending').click(function() {
				// variables
				var currentTable, rowList, sorterColumnIndex, newList, sortingOrder;

				// figure out wanted sorting order from class names
				sortingOrder = $(this).attr('class');
				if (sortingOrder === "sortable" || sortingOrder === "sort-ascending") {
					$(this).removeClass("sortable").removeClass("sort-ascending").addClass("sort-descending");
				} else {
					$(this).removeClass("sort-descending").addClass("sort-ascending");
				}

				// get index for the column that is used for sorting
				sorterColumnIndex = $(this).prevAll().length;

				// get the table that was clicked and retrieve its rows
				currentTable = $(this).parents('table');
				rowList = $(currentTable).find('tbody tr');

				//sort and rearrange tablerows accordingly
				if(sortingOrder === "sort-ascending" || sortingOrder === "sortable") {
					$(currentTable).find('tbody tr').sort(sortDescending).appendTo($(currentTable).find('tbody'));
				} else {
					$(currentTable).find('tbody tr').sort(sortAscending).appendTo($(currentTable).find('tbody'));
				}

				//function for descending sort
				function sortDescending(a,b) {
					return $(a).children().eq(sorterColumnIndex).text().toUpperCase() > $(b).children().eq(sorterColumnIndex).text().toUpperCase() ? 1 : -1;
				}
				
				//function for ascending sort
				function sortAscending(a,b) {
					return $(a).children().eq(sorterColumnIndex).text().toUpperCase() < $(b).children().eq(sorterColumnIndex).text().toUpperCase() ? 1 : -1;
				}
			});
		}
	}

	

	var scrollHelpPage = {
		build: function() {
			scrollHelpPage.setTriggers();
		},

		setTriggers: function() {
			
			$(".scrollList [scrollFromIndex]").click(function() {
				var scrollIndex = $(this).attr('scrollFromIndex');

			     $('html, body').animate({
			         scrollTop: $('[scrollToIndex="'+scrollIndex+'"]').offset().top
			     }, 1000);
			 });
		}
	}




    var fieldInfo = {
        load:function(){
            $('input[title]').each(function(){
                label = $(this).attr('title');
                $(this).val(label);
                $(this).addClass('blurred');
            });
            
            $('input[data-field-preset]').each(function(){
                label = $(this).attr('data-field-preset');
                $(this).val(label);
                $(this).addClass('blurred');
            });
        },
        build:function(){
            fieldInfo.load();
            fieldInfo.setTriggers();
        },
        setTriggers:function(){
            $('input[title]').focus(function(){
                if($(this).val() == $(this).attr('title'))
                {
                    $(this).val('');
                    $(this).removeClass('blurred');
                }
            });
            
            $('input[title]').blur(function(){
                if($(this).val() == '')
                {
                    $(this).val($(this).attr('title'));
                    $(this).addClass('blurred');
                }
            });
            
            $('input[data-field-preset]').focus(function(){
                if($(this).val() == $(this).attr('data-field-preset'))
                {
                    $(this).removeClass('blurred');
                }
            });
            
            $('input[data-field-preset]').blur(function(){
                if($(this).val() == $(this).attr('data-field-preset'))
                {
                    $(this).addClass('blurred');
                }
            });
        }
    }


    var overlayPopup = {
        setPopup:function(id){
        
            popup_height = $('#popup > .popup-content[data-popup-id="'+id+'"]').height();
            popup_width = $('#popup > .popup-content[data-popup-id="'+id+'"]').width();
            $('#popup').css({'width': popup_width+'px', 'height': popup_height+'px'});
        
            window_height = $(window).height();
            window_scrollTop = $(window).scrollTop();
            popup_height = $('#popup').height();

            
            window_offset = (window_height-popup_height)/2+window_scrollTop;
            $('#popup').offset({top:window_offset});


            
        },  
        popupAction:function(param, url){
        
            if(param.indexOf('close') != -1)
            {
                $('#overlay').hide();
            }
            else if(param.indexOf('open') != -1)
            {
                params = param.split(':');
                id = params[1];
                
                $('#popup > .popup-content').hide();
                $('#popup > .popup-content[data-popup-id="'+id+'"]').show();
                $('#popup > .popup-content[data-popup-id="'+id+'"]').find('[data-popup-url]').attr('href', url);

                $('#overlay').show();
                overlayPopup.setPopup(id);
            }
            else if(param.indexOf('goto') != -1)
            {
            
            
            }
        },
        build:function(){
            overlayPopup.setTriggers();
        },
        setTriggers:function(){
            $('#overlay .close').click(function(event){
                event.preventDefault();
                $('#overlay').hide();
            });
            

            
            $('[data-popup-action]').click(function(event){
                event.preventDefault();
                if (!$(this).hasClass('disabled'))
                {
                    params = $(this).attr('data-popup-action');
                    url = $(this).attr('href');
                    
                    overlayPopup.popupAction(params, url);
                }
            });
            
            

        }
    }

    var selectFilter = {
        build:function(){
            selectFilter.setTriggers();
        },
        setTriggers:function(){
            $('select[data-filter-group]').change(function(){
                group = $(this).attr('data-filter-group');
                id = $(this).attr('value');
                
                if($('[data-filter-group="'+group+'"][data-filter-id="'+id+'"]').length > 0)
                {
                    $('[data-filter-group="'+group+'"]').hide();
                    $('select[data-filter-group="'+group+'"]').show();
                    $('[data-filter-group="'+group+'"][data-filter-id="'+id+'"]').show();
                }
            });
        }
    }
    
    var tagSet = {
        emptySet:function(id){
            $('[data-tags-require-set="'+id+'"]')
            if($('[data-tags-require-set="'+id+'"]'))
            {
                if($('.tagset[data-tagset-id="'+id+'"]').find('.tag').length)
                {
                    hasTags = true;
                }
                else
                {
                    hasTags = false
                }
                
                $('[data-tags-require-set="'+id+'"]').each(function(){
                    if(hasTags == true)
                    {
                        $(this).removeClass('disabled');
                    }
                    else
                    {
                        $(this).addClass('disabled');
                    }
                });
            }
        },
        generateTag:function(id, value){

            if(value != '')
            {
                html = '<span class="tag">'+value+'<span class="remove">&#8203;</span></span>';
                $('.tagset[data-tagset-id]').append(html);
                $('input.text[data-tagset-id="'+id+'"]').val('');
                tagSet.emptySet(id);
            }
        },
        build:function(){
            tagSet.setTriggers();
        },
        setTriggers:function(){
            $('body').on('click', '.tagset .tag .remove', function(event){
                event.preventDefault();
                id = $(this).closest('.tagset').attr('data-tagset-id');
                $(this).parent().remove();
                tagSet.emptySet(id);


            });
        
            $('.button[data-tagset-id]').click(function(event){
                event.preventDefault();
                id = $(this).attr('data-tagset-id');
                
                value = $('input.text[data-tagset-id="'+id+'"]').val();
                tagSet.generateTag(id, value);
                
            });
            
            $('a[data-tagset-id]').click(function(event){
                event.preventDefault();
                id = $(this).attr('data-tagset-id');
                
                value = $(this).text();
                tagSet.generateTag(id, value);
                
                
            });
        }
    }
    

    var orgMenu = {
        build:function(){
            
            orgMenu.setTriggers();
        },
        setTriggers:function(){
            $('.orgmenu .category').click(function(event){
                event.preventDefault();
                if($(this).parent().hasClass('closed'))
                {
                    $(this).parent().removeClass('closed');
                }
                else
                {
                    $(this).parent().addClass('closed');
                }
            });
        }
    }
    
    
    
    
    var folderTree = {
        generateFolders:function(){
            folder_html = '<span class="folder">&#8203;</span>';
            file_html = '<span class="file">&#8203;</span>';
            
            $('.foldertree').each(function(){
                $(this).find('li').each(function(){
                    if($(this).children('ul').length)
                    {
                        $(this).prepend(folder_html);
                    }
                    else
                    {
                        $(this).prepend(file_html);
                    }
                    
                    
                });
            });
            
        },
        build:function(){
            folderTree.generateFolders();
            folderTree.setTriggers();
        },
        setTriggers:function(){
            $('.foldertree .folder').click(function(){
                if($(this).parent().hasClass('closed'))
                {
                    $(this).parent().removeClass('closed');
                }
                else
                {
                    $(this).parent().addClass('closed');
                }
            });
        }
    }


    
    var fieldDate = {
        generateLinks:function(){
            index = 0;
        
            $('input.text.date').each(function(){
                id = index;
                if($(this).hasClass('small'))
                {
                    html = '<span class="datepicker small" data-date-field-id="'+id+'">&#8203;</span>';
                }
                else
                {
                    html = '<span class="datepicker" data-date-field-id="'+id+'">&#8203;</span>';
                }
                $(this).attr('data-date-field-id', id);
                $(this).after(html);
                index = index+1;
            });
        },
        build:function(){
            fieldDate.generateLinks();
            fieldDate.setTriggers();
        },
        setTriggers:function(){
    
            $('input.text.date').datepicker({dateFormat: 'dd/mm/yy'});
            
            $('input.text.date').focus(function(){
                if(typeof $(this).attr('data-date-field-id') != 'undefined')
                {
                    id = $(this).attr('data-date-field-id');
                    
                    $('.datepicker[data-date-field-id="'+id+'"]').addClass('active');
                }
            });
        
            $('input.text.date').blur(function(){
                if(typeof $(this).attr('data-date-field-id') != 'undefined')
                {
                    id = $(this).attr('data-date-field-id');
                    
                    $('.datepicker[data-date-field-id="'+id+'"]').removeClass('active');
                }
            });
        
            $('.datepicker').click(function(event){
                event.preventDefault();
                
                if(typeof $(this).attr('data-date-field-id') != 'undefined')
                {
                    id = $(this).attr('data-date-field-id');
                    
                    if($(this).hasClass('active'))
                    {
                        $('input[data-date-field-id="'+id+'"]').blur();
                    }
                    else
                    {
                        $('input[data-date-field-id="'+id+'"]').focus();
                    }
                }
            });
        }
    }
    

    var tabsMenu = {
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
        build:function(){
            tabsMenu.load();
            tabsMenu.setTriggers();
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



    var actionMenu = {
        
        components : {
            header : '<ul class="actionmenu">',
            item : '<li><a href="[link]">[name]</a></li>',
            footer : '</ul>'
        },
        generateLinks:function(){
            $('.add-actionmenu').each(function(){
                if(typeof $(this).attr('data-actionmenu-links') != 'undefined')
                {
                    html = '<span class="actionmenu-icon">&nbsp;</span>';
                    $(this).prepend(html);
                }
            });
        
            $('.itemtree li').each(function(){

                if(typeof $(this).attr('data-actionmenu-links') != 'undefined')
                {
                    html = '<span class="actionmenu-icon">&nbsp;</span>';
                    

                    if($(this).children('.sim-checkbox'))
                    {
                        $(this).children('.sim-checkbox').after(html);
                    }
                    else
                    {
                        $(this).prepend(html);
                    }

                }
            });
        },
        generateMenu:function(link){
            
            attr = $(link).parent().attr('data-actionmenu-links');
            
            if(typeof attr != 'undefined')
            {
                var links;
                items = '';
                links = attr.split(',');
                
                for (i in links)
                {
                    linkdata = links[i].split(';');
                    thisItem = actionMenu.components.item;
                    thisItem = thisItem.replace('[link]',linkdata[0]);
                    thisItem = thisItem.replace('[name]',linkdata[1]);
                    items = items+thisItem;
                    
                    
                }
                
                html = actionMenu.components.header+items+actionMenu.components.footer;
            }
            else
            {
                items = '<li><a href="#">Poista</a></li>';
                items = items+'<li><a href="#">Näytä hakukohteet</a></li>';
                
                html = '<ul class="actionmenu">'+items+'</ul>';
            }
            $(link).append(html);
        },  
        deleteMenu:function(link){
            $(link).children('ul').remove();
        },
        build:function(){
            actionMenu.generateLinks();
            actionMenu.setTriggers();
        },
        setTriggers:function(){
            
            $('.actionmenu-icon').hover(function(){
                $(this).addClass('hover');
                actionMenu.generateMenu(this); 
            }, function(){
                $(this).removeClass('hover');
                actionMenu.deleteMenu(this);
            });
        }
    }


    var itemTree = {
        build:function(){
            itemTree.setTriggers();
        },
        setChildren:function(item){
            
            children = $(item).parent().children('ul').children('li').children('.sim-checkbox');
            
            if($(item).hasClass('selected'))
            {
                children.addClass('selected');
            }
            else
            {
                children.removeClass('selected');
                $('.itemtree .selectall').removeClass('selected');
            }
            
        },
        setRequired:function(id){
            
            if($('[data-require-itemtree-select="'+id+'"]').length > 0)
            {
                if($('.itemtree[data-itemtree-id="'+id+'"]').find('.sim-checkbox.selected').length > 0)
                {
                    $('[data-require-itemtree-select="'+id+'"]').find('a').removeClass('disabled');
                }
                else
                {
                    $('[data-require-itemtree-select="'+id+'"]').find('a').addClass('disabled');
                }
            }
        },
        setParent:function(item){
        
            parent = $(item).parents('.itemtree > li');
            children = parent.children('ul').children('li').children('.sim-checkbox');
            
            hasSelected = false;
            hasUnselected = false;
            
            children.each(function(){
                if($(this).hasClass('selected'))
                {
                    hasSelected = true;
                }
                else
                {
                    hasUnselected = true;
                }
            });
            
            // Partail
            if(hasSelected == true && hasUnselected == true)
            {
                parent.children('.sim-checkbox').addClass('partail');
                parent.children('.sim-checkbox').removeClass('selected');
                $('.itemtree .selectall').removeClass('selected');
            }
            // Complete
            else if(hasSelected == true && hasUnselected == false)
            {
                parent.children('.sim-checkbox').removeClass('partail');
                parent.children('.sim-checkbox').addClass('selected');
            }
            // Empty
            else
            {
                parent.children('.sim-checkbox').removeClass('partail');
                parent.children('.sim-checkbox').removeClass('selected');
                $('.itemtree .selectall').removeClass('selected');
            }
        },
        setTriggers:function(){
        
        
            $('.itemtree > li > .sim-checkbox').click(function(event){
                event.preventDefault();
                if($(this).hasClass('selectall'))
                {
                    if($(this).hasClass('selected'))
                    {
                        $(this).removeClass('selected');
                    }
                    else
                    {
                        $(this).parents('.itemtree').find('.sim-checkbox').removeClass('partail');
                        $(this).parents('.itemtree').find('.sim-checkbox').addClass('selected');
                    }
                }
                else
                {
                    if($(this).hasClass('selected') || $(this).hasClass('partail'))
                    {
                        $(this).removeClass('selected');
                        $(this).removeClass('partail');
                        
                        itemTree.setChildren(this);
                    }
                    else
                    {
                        $(this).addClass('selected');
                        itemTree.setChildren(this);
                    }
                }
                id = $(this).parents('.itemtree').attr('data-itemtree-id');
                itemTree.setRequired(id);
            });

            $('.itemtree ul .sim-checkbox').click(function(event){
                event.preventDefault();
                
                if($(this).hasClass('selected'))
                {
                    $(this).removeClass('selected');
                    itemTree.setParent(this);
                }
                else
                {
                    $(this).addClass('selected');
                    itemTree.setParent(this);
                }
                id = $(this).parents('.itemtree').attr('data-itemtree-id');
                itemTree.setRequired(id);
            });
        }
    }

    var hidable = {
    	build: function() {
    		$('.hider-button').on('click', function(event){
    			var hidableElement = $(this).siblings().eq(0);
    			if( hidableElement.hasClass('hidden') === true ) {
    				hidableElement.slideDown(200);
    				hidableElement.removeClass('hidden').addClass('visible');
    				$(this).css('background-image', 'url(../resources/img/arrow-down.png)');
    			} else if ( hidableElement.hasClass('visible') === true ) {
    				hidableElement.slideUp(200);
    				hidableElement.removeClass('visible').addClass('hidden');
    				$(this).css('background-image', 'url(../resources/img/arrow-right.png)');
    			}
    		});
    	}
    }

    defaultActions.build();
    applicationBasket.build();
	popupWindow.build();
	tabsMenu.build();
	hierarchyList.build();
	loginPopup.build();
	dropDownMenu.build();
	tableRowHighlight.build();
	tableSorter.build();
	scrollHelpPage.build();
    selectFilter.build();
    orgMenu.build();
    fieldInfo.build();
    overlayPopup.build();
    tagSet.build();
    folderTree.build();
    fieldDate.build();
    actionMenu.build();
    itemTree.build();
    tabsMenu.build();
    hidable.build();

/* Master.js ends */

});



