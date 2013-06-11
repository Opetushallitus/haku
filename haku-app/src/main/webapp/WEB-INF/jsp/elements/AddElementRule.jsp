<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>

<%--
  ~ Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
  ~
  ~ This program is free software:  Licensed under the EUPL, Version 1.1 or - as
  ~ soon as they will be approved by the European Commission - subsequent versions
  ~ of the EUPL (the "Licence");
  ~
  ~ You may not use this work except in compliance with the Licence.
  ~ You may obtain a copy of the Licence at: http:// www.osor.eu/eupl/
  ~
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ European Union Public Licence for more details.
  --%>

<c:set var="styleBaseClass" value="form-row"/>
<div id="${element.id}" class="${styleBaseClass}">
    <c:choose>
        <c:when test="${empty categoryData[element.relatedElementId]}">
            <div class="${styleBaseClass}-content ${element.id}-removable">
                <a id="${element.id}-link" href="#"><haku:i18nText value="${element.text}"/></a>
            </div>
            <c:if test="${not empty element.help}">
                <div class="margin-top-1 ${element.id}-removable" id="help-${element.id}">
                    <small><haku:i18nText value="${element.help}"/></small>
                </div>
            </c:if>
            <div class="clear ${element.id}-removable"></div>

            <script type="text/javascript">
                (function () {
                    $("#${element.id}-link").click(function (event) {
                        event.preventDefault();
                        var childIds =
                                [<c:forEach var="child" items="${element.children}" varStatus="status">
                                    "${child.id}"${not status.last ? ', ' : ''}
                                    </c:forEach>]
                        var ruleChilds = $("#${element.id} .rule-childs");
                        ruleData.getRuleChild(childIds, 0, ruleChilds);
                        $(".${element.id}-removable").remove();
                    });
                    var ruleData = {
                        getRuleChild: function (childIds, index, ruleChilds) {
                            $.get(document.URL.split("?")[0] + '/' + childIds[index],
                                    function (data) {
                                        ruleChilds.append(data);
                                        if (childIds.length - 1 > index) {
                                            ruleData.getRuleChild(childIds, ++index, ruleChilds);
                                        }
                                    }
                            );
                        }
                    };
                })();
            </script>
        </c:when>
    </c:choose>
    <div class="rule-childs clear">
        <c:choose>
            <c:when test="${not empty categoryData[element.relatedElementId]}">
                <haku:viewChilds element="${element}"/>
            </c:when>
        </c:choose>
    </div>
</div>
