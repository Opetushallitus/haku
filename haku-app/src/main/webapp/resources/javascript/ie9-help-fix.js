$( document ).ready(function() {
    var form = $('form.form');
    form.find('button.helplink').click(function () {
        alert('click');
        form.attr('action', this.value);
        form.attr('target', '_blank');
        form.submit();
        form.removeAttr('target');
        form.removeAttr('action');
    });
});
