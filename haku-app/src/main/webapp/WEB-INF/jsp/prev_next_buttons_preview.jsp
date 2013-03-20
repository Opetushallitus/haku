<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
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
<fmt:setBundle basename="messages"/>
<fmt:requestEncoding value="utf-8"/>
<c:set var="baseUrl"
       value="${pageContext.request.contextPath}/lomake/${hakemusId.applicationPeriodId}/${hakemusId.formId}"
       scope="page"/>
<c:forEach var="item" items="${form.children}" varStatus="status">
    <c:if test="${(not status.first) and (item eq vaihe)}">
        <div class="float-left">
            <form method="get" action="${baseUrl}/${tmpPrev.id}">
                <button class="left" name="phaseId" type="submit" value="${tmpPrev.id}-skip-validators">
                <span>
                    <span><fmt:message key="lomake.button.previous"/></span>
                </span>
                </button>
            </form>
        </div>
    </c:if>
    <c:choose>
        <c:when test="${(status.last) and (item eq vaihe)}">
            <div class="float-right">
                <button class="right" name="nav-send" data-po-show="areyousure" value="true">
                    <span>
                        <span><fmt:message key="lomake.button.send"/></span>
                    </span>
                </button>
            </div>
            <div class="clear"></div>
        </c:when>
    </c:choose>
    <c:set var="tmpPrev" value="${item}" scope="page"/>
</c:forEach>
<c:remove var="tmpPrev" scope="page"/>
