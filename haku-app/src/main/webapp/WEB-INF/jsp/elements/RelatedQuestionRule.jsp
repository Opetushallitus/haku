<%@ page session="false"%>
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
<c:set var="answers" value="${it.answers}" scope="request"/>
<c:set var="nameSelectors" value="${ f:tochildIdList(element)}"/>
<c:set var="variables" value="${ f:setToList(element.variables)}"/>
<div id="${element.id}" class="related-question-rule-class">
    <script type="text/javascript">
        $(function () {
            var ruleData = {
                variables: [${variables}],
                ruleId: "${element.id}",
                childIds: [ ${nameSelectors} ]
            };
            complexRule.init(ruleData);
        });
    </script>
    <haku:viewChilds element="${element}"/>
</div>
<c:remove var="nameSelectors"/>
<c:remove var="variables"/>
