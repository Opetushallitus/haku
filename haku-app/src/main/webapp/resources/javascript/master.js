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
    initEvents: function (expr, ruleData) {
        if (!expr) {
            return;
        }
        var type = expr.type;

        if (type == 'Variable') {
            //keypress paste focus textInput input
            $("[name=\"" + expr.value + "\"]").on('change ', ruleData, complexRule.refreshView);
        }
        this.initEvents(expr.left, ruleData);
        this.initEvents(expr.right, ruleData);
    },
    init: function (ruleData) {
        this.initEvents(ruleData.jsonExpr, ruleData);
    },


    evaluateExpression: function (expr) {
        if (!expr) {
            return false;
        }
        var type = expr.type;

        if (type == 'Value') {
            return expr.value;
        } else if (type == 'Variable') {
            return $("#" + expr.value).val();
        } else if (type == 'NotEquals') {
            return complexRule.evaluateExpression(expr.left) != complexRule.evaluateExpression(expr.right);
        } else if (type == 'Equals') {
            return complexRule.evaluateExpression(expr.left) == complexRule.evaluateExpression(expr.right);
        } else if (type == 'Or') {
            return complexRule.evaluateExpression(expr.left) || complexRule.evaluateExpression(expr.right);
        } else if (type == 'And') {
            return complexRule.evaluateExpression(expr.left) && complexRule.evaluateExpression(expr.right);
        } else {
            return false;
        }
    },

    refreshView: function (event) {
        console.log("refreshView");
        var ruleData = event.data;
        var selector = $(ruleData.ruleSelector);
        if (true) { //complexRule.evaluateExpression(ruleData.jsonExpr)
            console.log("true");
            if ($.trim(selector.html()) === "") {
                var url = document.URL.split("?")[0];
                console.log(url);
                for (var id in ruleData.childIds) {
                    var t = url + '/' + ruleData.childIds[id]
                    console.log("call : " + t )
                    $.ajax({
                        type: 'POST',
                        url: t,
                        data: $("form.form").serialize(),

                        success: function (data, textStatus, jqXHR) {
                            console.log(textStatus);
                            console.log(data);
                            selector.append(data);

                        },
                        error: function(e, ts, et) { alert(e); alert(ts) }
                    });
//                    $.get(url + '/' + ruleData.childIds[id],
//                        function (html) {
//                            selector.append(html);
//                        }
//                    );
                }
            }
        } else {
            console.log("false");
            selector.html("");
        }
    }
}
