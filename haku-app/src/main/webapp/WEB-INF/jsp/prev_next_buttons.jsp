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

<c:forEach var="item" items="${form.children}" varStatus="status">
    <c:if test="${(item eq vaihe)}">
        <c:if test="${(not status.first)}">
            <div class="float-left">
                <button class="left" name="phaseId" type="submit" value="${tmpPrev.id}">
                <span>
                    <span><fmt:message key="lomake.button.previous"/></span>
                </span>
                </button>
            </div>
        </c:if>
        <c:if test="${status.last}">
            <div class="float-right">
                <button class="right" name="phaseId" type="submit" value="esikatselu">
                   <span>
                       <span><fmt:message key="lomake.button.preview"/></span>
                   </span>
                </button>
            </div>
            <div role="presentation" class="clear"></div>
        </c:if>
    </c:if>
    <c:if test="${(tmpPrev eq vaihe)}">
        <div class="float-right">
            <button class="right" name="phaseId" type="submit" value="${item.id}">
                <span>
                    <span><fmt:message key="lomake.button.next"/></span>
                </span>
            </button>
        </div>
        <div role="presentation" class="clear"></div>
    </c:if>
    <c:set var="tmpPrev" value="${item}" scope="page"/>
</c:forEach>
<c:remove var="tmpPrev" scope="page"/>
