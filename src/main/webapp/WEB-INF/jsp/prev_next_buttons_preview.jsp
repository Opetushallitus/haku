<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
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

<c:set var="baseUrl"
       value="${pageContext.request.contextPath}/lomake/${hakemusId.applicationPeriodId}/${hakemusId.formId}"
       scope="request"/>
<c:choose>
    <c:when test="${vaihe.hasPrev}">
        <form method="get" action="${baseUrl}/${vaihe.prev.id}">
            <div class="set-left">
                <button class="left" name="nav-prev" type="submit" value="true"><span><span><spring:message
                        code="lomake.button.previous"/></span></span></button>
            </div>
        </form>
    </c:when>
</c:choose>
<div class="set-right">
    <c:choose>
        <c:when test="${vaihe.hasNext}">
            <form method="get" action="${baseUrl}/${vaihe.next.id}">
                <div class="set-left">
                    <button class="right" name="nav-next" type="submit" value="true"><span><span><spring:message
                            code="lomake.button.next"/></span></span></button>
                </div>
            </form>
        </c:when>
        <c:otherwise>
            <form method="post" action="${baseUrl}/send">
                <div class="set-left">
                    <button class="right" name="nav-send" type="submit" value="true"><span><span><spring:message
                            code="lomake.button.send"/></span></span></button>
                </div>
            </form>
        </c:otherwise>
    </c:choose>
</div>
<div class="clear"></div>
