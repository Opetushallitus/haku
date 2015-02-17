<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

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
        <c:when test="${empty answers[element.children[0].id]}">
            <div class="${styleBaseClass}-content" id="${element.id}-addRemoveLinks">
                <a id="${element.id}-link" href="#"><haku:i18nText value="${element.text}"/></a>
                <a id="${element.id}-undolink" style="display: none;" href="#"><fmt:message key="poista"/></a>
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
                        $("#${element.id}-link").hide();
                        $("#${element.id}-undolink").show();
                        $(".${element.id}-removable").remove();
                        <c:forEach var="prevRule" items="${element.previousRules}" varStatus="status">
                            $("#${prevRule}-addRemoveLinks").hide();
                        </c:forEach>
                    });
                    $("#${element.id}-undolink").click(function (event) {
                        event.preventDefault();
                        var childIds =
                                [<c:forEach var="child" items="${element.children}" varStatus="status">
                                    "${child.id}"${not status.last ? ', ' : ''}
                                    </c:forEach>]
                        $("#${element.id} .rule-childs").empty();
                        $("#${element.id}-link").show();
                        $("#${element.id}-undolink").hide();
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
            <c:when test="${not empty answers[element.relatedElementId] and not empty answers[element.children[0].id]}">
                <haku:viewChilds element="${element}"/>
            </c:when>
        </c:choose>
    </div>
</div>
