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

<div id="${element.id}" class="related-question-rule-class"
     data-selector="${ f:toNameSelectorString(element.relatedElementId)}">
    <script type="text/javascript">
        var ${fn:replace(element.id, '-', '_')} = {
            childIds : [<c:forEach var="child" items="${element.children}" varStatus="status">"${child.id}"${not status.last ? ', ' : ''}</c:forEach>],
            ruleChildSelector : "#${element.id} .rule-childs",
            expression : "${element.expression}",
            relatedSelector : "${ f:toNameSelectorString(element.relatedElementId)}"
        };
        var ${fn:replace(element.id, '-', '_')}_func = function (event) {
            var $this = $(this);
            var settings = ${fn:replace(element.id, '-', '_')};
            relatedRule.changeState($this, $(settings.ruleChildSelector), settings.childIds, settings.expression);
        };
        (function () {
            $("${ f:toNameSelectorString(element.relatedElementId)}").change(${fn:replace(element.id, '-', '_')}_func);
        })();
    </script>
    <div class="rule-childs clear">
        <haku:viewChilds element="${element}"/>
    </div>
</div>
