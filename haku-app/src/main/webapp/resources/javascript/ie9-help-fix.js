$( document ).ready(function() {
    var form = $('form.form');
    var button = form.find('button.helplink');
    button.attr('type', 'button');
    button.click(function () {
        form.attr('action', this.value);
        form.attr('target', '_blank');
        form.submit();
        form.removeAttr('target');
        form.removeAttr('action');
    });
});
