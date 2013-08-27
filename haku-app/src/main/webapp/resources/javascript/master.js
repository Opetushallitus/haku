var relatedRule = {

    getChildrenAndAppendToDom: function (childIds, domElement) {
        var url = document.URL.split("?")[0];
        for (var id in childIds) {
            $.get(url + '/' + childIds[id],
                function (data) {
                    domElement.append(data);
                }
            );
        }
    },

    changeState: function ($this, ruleChilds, childIds, expression) {
        if ($this.is(':checkbox') && $this.is(':checked')) {
            if ($.trim(ruleChilds.html()) === "") {
                this.getChildrenAndAppendToDom(childIds, ruleChilds);
            }
        }
        else if (!$this.is(':checkbox') && $this.val().search(expression) !== -1) {
            if ($.trim(ruleChilds.html()) === "") {
                this.getChildrenAndAppendToDom(childIds, ruleChilds);
            }
        } else {
            ruleChilds.html("");
        }
    }
}

var complexRule = {
    init: function (ruleData) {
        for (index in ruleData.variables) {

            var question = $("[name=\"" + ruleData.variables[index] + "\"]");
            if (question.length) {
                var events = $._data(question[0], "events");
                if (events) {
                    if (events.change.length < 1 && events.change[0].data.ruleId != ruleData.ruleId) {
                        question.on('change ', ruleData, complexRule.refreshView);
                    }
                } else {
                    question.on('change ', ruleData, complexRule.refreshView);
                }
            }
        }
    },

    refreshView: function (event) {
        console.log("refresh view " + event.type);
        var ruleData = event.data;
        var url = document.URL.split("?")[0] + '/' + ruleData.ruleId;
        $.ajax({
            type: 'POST',
            url: url,
            async: false,
            data: $("form.form").serialize(),

            success: function (data, textStatus, jqXHR) {
                console.log(textStatus);
                $("#" + ruleData.ruleId).replaceWith(data);
            },
            error: function (e, ts, et) {
                console.log("refresh view error" + ts);
            }
        });
    }
}
