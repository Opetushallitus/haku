var haku = {
    triggerRule: function (element) {
        var elem = $('#' + element.id);
        elem.on('change', function submit(event) {
            elem.closest("form").submit();
        });
    }
};

window.elementAdder = {
    toggleAddRemoveButtons: function(el) {
        'use strict';

        var lastEl = rootEl().find('.repeatingElement:last');
        var secondToLastEl = lastEl.parents('.repeatingElement:first');

        hideAllButtons();

        if (!canAddMoreElementsToElement(lastEl)) {
            showRemoveForEl(lastEl);
        } else {
            showAddForEl(lastEl);
            showRemoveForEl(secondToLastEl);
        }

        function rootEl() {
            var rootEl = el.parents('.repeatingElement:last');
            if (rootEl.length === 0) {
                rootEl = el.parent();
            }
            return rootEl;
        }

        function hideAllButtons() {
            rootEl().find('.addRemoveLinks').removeClass('showRemove showAdd');
        }

        function canAddMoreElementsToElement(el) {
            return el.find('.elementChildren').children().length === 0;
        }

        function showRemoveForEl(el) {
            el.find('.addRemoveLinks:first').addClass('showRemove');
        }

        function showAddForEl(el) {
            el.find('.addRemoveLinks:first').addClass('showAdd');
        }
    }
};
