<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="/WEB-INF/tld/functions.tld" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

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

<div id="${element.id}" class="related-question-rule-class"
     data-selector="${ fn:toNameSelectorString(element.relatedElementId)}">
    <script type="text/javascript">
        (function () {
            $("${ fn:toNameSelectorString(element.relatedElementId)}").change(function (event) {
                var childIds = [<c:forEach var="child" items="${element.children}" varStatus="status">"${child.id}"${not status.last ? ', ' : ''}</c:forEach>];
                var ruleChildren = $("#${element.id} .rule-childs");
                var expression = "${element.expression}";
                var $this = $(this);
                relatedRule.changeState($this, ruleChildren, childIds, expression);
            });
        })();
    </script>
    <div class="rule-childs clear">
        <haku:viewChilds element="${element}"/>
    </div>
</div>
