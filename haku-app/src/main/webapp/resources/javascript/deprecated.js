


/*
    THIS FILE CONTAINS CURRENTLY UNUSED SCRIPTS, BUT MAY BE USED LATER ON
*/




/*
    var formReplacements = {
        settings : {
            checkboxAmount : 0,
            radioAmount : 0
        },
        build:function(){
            // Generate replacements, set triggers
            formReplacements.jsCheckbox.generate();
            formReplacements.jsRadio.generate();
            formReplacements.setDefaultTriggers();
            formReplacements.setApplicationSpecificTriggers();

            replaceCheckboxes = formReplacements.jsCheckbox.generate;
                    replaceRadios = formReplacements.jsRadio.generate;
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
                
                // Set focus and dispatch click event
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
        setDefaultTriggers:function(){
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
        },

        setApplicationSpecificTriggers: function() {

            //submit searchresultfilter on checkbox click
            $('body').on('click', '#hakusuodattimet .js-checkbox', function(event) {
                event.preventDefault();
                $('#hakusuodattimet').submit();
            });
        }
    }
    */

/*
    var comparisonTable = {

        educationColumns: {
            
            load: function() {
                this.visibleCount = 3;
                this.visibleStartIndex = 1; 
                this.columnCount = $('.compare-table thead tr.education td').length - 1;
                this.elementContent = $('.compare-table tbody');
                this.elementHeaders = $('.compare-table thead tr:last');
            },

            //how many columns visible
            getVisibleCount: function() {
                return this.visibleCount;
            },
            setvisibleCount: function(count) {
                this.visibleCount = count;
            },

            //which column is first to show
            getVisibleStartIndex: function() {
                return this.visibleStartIndex;
            },
            setvisibleStartIndex: function(index) {
                this.visibleStartIndex = index;
            },

            //how many education columns in users list alltogether
            getColumnCount: function() {
                return this.columnCount;
            },

            //return indexes for the columns that are visible
            getVisibleColumns: function() {
                var columns = [0]; //first column is visible by default
                for (var i = 0 ; i < this.getVisibleCount() ; ++i) {
                    if(this.getVisibleStartIndex() + i > this.getColumnCount()) {
                        break;
                    }
                    columns.push(this.getVisibleStartIndex() + i);
                }
                return columns;
            },

            //return indexes for the columns that are hidden
            getHiddenColumns: function() {
                var columns = [];
                for (var i = 0 ; i < this.getColumnCount() ; ++i) {
                    columns.push(i);
                }
                columns.splice(this.getVisibleStartIndex(),this.getVisibleCount());
                return columns;
            },

            //return headers for columns
            getColumnHeader: function(index) {
                if(typeof(index) !== 'number') {
                    return undefined;
                }
                return $(this.elementHeaders).children().eq(index + 1);
            },

            //return column elements by index 
            getColumnContents: function(index) {
                if(typeof(index) !== 'number') {
                    return undefined;
                }
                var elements = [];
                $(this.elementContent).children().each( function() {    
                    elements.push($(this).children().eq(index + 1));
                });
                return elements;
            },

            //return columns elements including header
            getColumnHeaderAndContents: function(index) {
                var header = this.getColumnHeader(index);
                var contents = this.getColumnContents(index);
                contents.push(header);
                return contents;
            },

            //hide column elements by index
            hideColumn: function(index) {
                var elements = $(this.getColumnHeaderAndContents(index));
                $(elements).each( function() {
                    $(this).fadeOut(100);
                });
            },

            showColumn: function(index) {
                var elements = $(this.getColumnHeaderAndContents(index));
                $(elements).each( function() {
                    $(this).fadeIn(100);
                });
            },

            //hide columns outside of range
            hideExtraColumns: function() {
                var columns = this.getHiddenColumns();
                for(var i = 0 ; i < columns.length ; ++i) {
                    this.hideColumn(columns[i]);
                }
            },

            showColumns: function() {
                var columns = this.getVisibleColumns();
                for (var i = 0 ; i > columns.length ; ++i) {
                    this.showColumn(columns[i]);
                }
            },

            nextPage: function() {
                if( (this.getVisibleStartIndex() + this.getVisibleCount()) > this.getColumnCount() ) {
                    this.setVisibleStartIndex(this.getColumnCount());
                } else {
                    this.setVisibleStartIndex(this.getVisibleStartIndex() + this.getVisibleCount());
                }
                this.updateView();
            },

            prevPage: function() {
                if( (this.getVisibleStartIndex() - this.getVisibleCount()) < 0 {
                    this.setVisibleStartIndex(0);
                } else {
                    this.setVisibleStartIndex(this.getVisibleStartIndex() - this.getVisibleCount());
                }
            },

            updateView: function() {
                this.hideColumns();
                this.showColumns();
            }

        }, //educationColumns

        build: function() {
            this.educationColumns.load();
            this.load();
            this.setTriggers();
        },

        load: function() {
            this.educationColumns.hideColumns();
        },  

        setTriggers: function() {
            
        }
    }
    */








    /*
    var simCheckbox = {
        replaceInput:function(){
            index = 0;
        
            $('input[type="checkbox"]').each(function(){
                id = index;
                html = '<span class="sim-checkbox" data-sim-checkbox-id="'+id+'">&nbsp;</span>';
                $(this).attr('data-sim-checkbox-id', id);
                $(this).before(html);
                $(this).hide();
                if($(this).prop('checked') == true || $(this).attr('checked') == true)
                {
                    $(this).attr('checked', 'checked');
                    $('.sim-checkbox[data-sim-checkbox-id="'+id+'"]').addClass('selected');
                    reqId = $(this).attr('data-required-checkbox');
                    reqSetting = $('input[data-sim-checkbox-id="'+id+'"]').attr('data-required-setting');
                    
                    if(reqSetting == 'reverse')
                    {
                        simCheckbox.requirementHandling(reqId, false);
                    }
                    else
                    {
                        simCheckbox.requirementHandling(reqId, true);
                    }
                }
                
                
                
                index = index+1;
            });
        },
        requirementHandling:function(id, checked){
            if(checked == true)
            {
                $('[data-require-checked="'+id+'"]').each(function(){
                    $(this).removeAttr('disabled');
                    $(this).removeClass('disabled');
                });
            }
            else
            {
                $('[data-require-checked="'+id+'"]').each(function(){
                    $(this).attr('disabled', 'disabled');
                    $(this).addClass('disabled');
                });
            }
        },
        build:function(){
            simCheckbox.replaceInput();
            simCheckbox.setTriggers();
        },
        setTriggers:function(){
            $('.sim-checkbox').click(function(event){
                event.preventDefault();
                
                if(typeof $(this).attr('data-sim-checkbox-id') != 'undefined')
                {
                    id = $(this).attr('data-sim-checkbox-id');
                    reqId = $('input[data-sim-checkbox-id="'+id+'"]').attr('data-required-checkbox');
                    reqSetting = $('input[data-sim-checkbox-id="'+id+'"]').attr('data-required-setting');
                    
                    if($(this).hasClass('selected'))
                    {
                        $(this).removeClass('selected');
                        $('input[data-sim-checkbox-id="'+id+'"]').removeAttr('checked');
                        if(reqId)
                        {
                            if(reqSetting == 'reverse')
                            {
                                simCheckbox.requirementHandling(reqId, true);
                            }
                            else
                            {
                                simCheckbox.requirementHandling(reqId, false);
                            }
                        }
                    }
                    else
                    {
                        $(this).addClass('selected');
                        $('input[data-sim-checkbox-id="'+id+'"]').attr('checked', 'checked');
                        if(reqId)
                        {
                            if(reqSetting == 'reverse')
                            {
                                simCheckbox.requirementHandling(reqId, false);
                            }
                            else
                            {
                                simCheckbox.requirementHandling(reqId, true);
                            }
                        }
                    }
                }
            });
        }
    }
*/