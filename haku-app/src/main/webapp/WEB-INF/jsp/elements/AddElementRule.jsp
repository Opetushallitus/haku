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
<div id="${element.id}" class="${styleBaseClass} repeatingElement">
    <div class="${styleBaseClass}-content addRemoveLinks" id="${element.id}-addRemoveLinks">
        <a id="${element.id}-link" class="addEl" href="#"><haku:i18nText value="${element.text}"/></a>
        <a id="${element.id}-undolink" class="removeEl" href="#"><fmt:message key="poista"/></a>
    </div>
    <c:if test="${not empty element.help}">
        <div class="margin-top-1 ${element.id}-removable" id="help-${element.id}">
            <small><haku:i18nText value="${element.help}"/></small>
        </div>
    </c:if>
    <div class="clear ${element.id}-removable"></div>

    <script type="text/javascript">
        $(function () {
            var el = $("#${element.id}");

            elementAdder.toggleAddRemoveButtons(el)

            $("#${element.id}-link").click(function (event) {
                event.preventDefault();
                var childIds =
                        [<c:forEach var="child" items="${element.children}" varStatus="status">
                            "${child.id}"${not status.last ? ', ' : ''}
                            </c:forEach>]
                var elChildren = $("#${element.id} .elementChildren");
                appendElChildren(childIds, 0, elChildren);
                $(".${element.id}-removable").remove();
            });

            $("#${element.id}-undolink").click(function (event) {
                event.preventDefault();
                el.find(".elementChildren").empty();
                elementAdder.toggleAddRemoveButtons(el)
            });

            function appendElChildren(childIds, index, children) {
                $.get(document.URL.split("?")[0] + '/' + childIds[index], function (data) {
                    children.append(data);
                    elementAdder.toggleAddRemoveButtons(el)
                    if (childIds.length - 1 > index) {
                        appendElChildren(childIds, ++index, children);
                    }
                });
            }
        });
    </script>
    <div class="elementChildren clear">
        <c:choose>
            <c:when test="${not empty answers[element.relatedElementId] and not empty answers[element.children[0].id]}">
                <haku:viewChilds element="${element}"/>
            </c:when>
        </c:choose>
    </div>
</div>
