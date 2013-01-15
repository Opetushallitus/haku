<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
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

<table class="preference-sort">
    <tbody>
    <c:forEach var="child" items="${element.children}" varStatus="status">
        <tr>
            <td class="index">
                <c:out value="${child.title}"/>
                <br/>
                <c:if test="${not status.first}">
                    <button class="up sort" data-id="${child.id}" data-target="${element.children[status.index - 1].id}"
                            type="button">
                            <span>
                                <span><c:out value="${element.moveUpLabel}"/></span>
                            </span>
                    </button>
                    <br/>
                </c:if>
                <c:if test="${not status.last}">
                    <button class="down sort" data-id="${child.id}"
                            data-target="${element.children[status.index + 1].id}" type="button">
                            <span>
                                <span><c:out value="${element.moveDownLabel}"/></span>
                            </span>
                    </button>
                </c:if>
            </td>
            <td>
                <c:set var="index" value="${status.count}" scope="request"/>
                <c:set var="sortableItem" value="${child}" scope="request"/>
                <jsp:include page="${sortableItem.type}.jsp"/>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<script type="text/javascript">
    var sortabletable_settings = {
        contextPath: "${pageContext.request.contextPath}",
        applicationPeriodId: "${it.hakemusId.applicationPeriodId}",
        formId: "${it.hakemusId.formId}",
        vaiheId: "${it.vaihe.id}",
        teemaId: "${it.parent.id}"
    }
</script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/javascript/sortabletable.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/javascript/preferencerow.js"></script>
