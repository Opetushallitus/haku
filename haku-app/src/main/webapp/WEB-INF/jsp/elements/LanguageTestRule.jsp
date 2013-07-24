<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="f" uri="/WEB-INF/tld/functions.tld" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%--
  ~ Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
  ~
  ~ This program is free software:  Licensed under the EUPL, Version 1.1 or - as
  ~ soon as they will be approved by the European Commission - subsequent versions
  ~ of the EUPL (the "Licence");
  ~
  ~ You may not use this work except in compliance with the Licence.
  ~ You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
  ~
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ European Union Public Licence for more details.
  --%>

<div id="${element.id}" class="related-question-rule-class">

    <script type="text/javascript">

        $(document).ready(function() {

            var onOppiaineChange = function() {

                var ai = $("#PK_AI_OPPIAINE, #LK_AI_OPPIAINE").val();
                var a1 = $("#PK_A1_OPPIAINE, #LK_A1_OPPIAINE").val();
                var a2 = $("#PK_A2_OPPIAINE, #LK_A2_OPPIAINE").val();
                var a1Grade = $("#PK_A1, #LK_A1").val();
                var a2Grade = $("#PK_A2, #LK_A2").val();
                var values = { "ai" : ai, "a1" : a1, "a2" : a2, "a1Grade" : a1Grade, "a2Grade" : a2Grade };
                var str = [];
                for(var param in values) {
                    str.push(encodeURIComponent(param) + "=" + encodeURIComponent(values[param]));
                }

                var url = document.URL.split("?")[0];
                url = url + '/${element.id}/languageTest?' + str.join("&");

                $.get(url, function(data) {
                        var needed = data.needed;
                        var ruleChildSelector =  $("#${element.id} .rule-childs");
                        childIds = [<c:forEach var="child" items="${element.children}" varStatus="status">"${child.id}"${not status.last ? ', ' : ''}</c:forEach>];
                        if (needed) {
                            if ($.trim(ruleChildSelector.html()) === "") {
                                relatedRule.getChildrenAndAppendToDom(childIds, ruleChildSelector);
                            }
                        } else {
                            ruleChildSelector.html("");
                        }
                });
            };


            $("#PK_AI_OPPIAINE, #LK_AI_OPPIAINE, "+
            "#PK_A1_OPPIAINE, #LK_A1_OPPIAINE, #PK_A1, #LK_A1, "+
            "#PK_A2_OPPIAINE, #LK_A2_OPPIAINE, #PK_A2, #LK_A2").change(onOppiaineChange);

            onOppiaineChange();
        });
    </script>

    <div class="rule-childs clear">
    </div>
</div>