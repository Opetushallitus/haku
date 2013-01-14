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

<div id="${element.id}">
    <c:set var="key" value="${element.relatedElementId}"/>
    <script type="text/javascript">
        (function() {
            $("[name=\"${key}\"]").change(function(event) {
                var childIds = [<c:forEach var="child" items="${element.children}" varStatus="status">"${child.id}"${not status.last ? ', ' : ''}</c:forEach>], ruleChilds = $("#${element.id} .rule-childs");
                if ($(this).val().search("${element.expression}") !== -1) {
                    if (ruleChilds.html().trim() === "") {
                        ruleData.getRuleChild(childIds, 0, ruleChilds);
                    }
                } else {
                    ruleChilds.html("");
                }
            });

            var ruleData = {
                getRuleChild : function(childIds, index, ruleChilds) {
                    $.get('${pageContext.request.contextPath}/lomake/${hakemusId.applicationPeriodId}/${hakemusId.formId}/' + childIds[index],
                            function(data) {
                                ruleChilds.append(data);
                                if (childIds.length - 1 > index) {
                                    ruleData.getRuleChild(childIds, ++index, ruleChilds);
                                } else {
                                    //replaceCheckboxes();
                                    //replaceRadios();
                                }
                            });
                }
            };
        })();
    </script>
    <div class="rule-childs clear">
        <c:choose>
            <c:when test="${not empty categoryData[key]}">
                <c:if test="${fn:evaluate(categoryData[key], element.expression)}">
                    <haku:viewChilds element="${element}"/>
                </c:if>
            </c:when>
        </c:choose>
    </div>
    <noscript>
        <input type="submit" id="enabling-submit" name="enabling-submit" value="Ok"/>
    </noscript>
</div>
