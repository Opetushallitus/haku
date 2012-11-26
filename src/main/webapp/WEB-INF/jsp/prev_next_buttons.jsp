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

<div class="set-left">
    <c:choose>
        <c:when test="${vaihe.hasPrev}">
            <button class="left" name="vaiheId" type="submit" value="${vaihe.prev.id}"><span><span><spring:message
                    code="lomake.button.previous"/></span></span></button>
        </c:when>
    </c:choose>
    <c:if test="${not empty sessionScope['username']}">
        <button class="save" name="vaiheId" type="submit" value="${vaihe.id}"><span><span><spring:message
                code="lomake.button.saveasdraft"/></span></span></button>
    </c:if>
</div>
<div class="set-right">
    <c:choose>
        <c:when test="${vaihe.hasNext}">
            <button class="right" name="vaiheId" type="submit" value="${vaihe.next.id}">
            <span>
                <span>
                    <c:choose>
                        <c:when test="${vaihe.next.preview}">
                            <spring:message code="lomake.button.preview"/>
                        </c:when>
                        <c:otherwise>
                            <spring:message code="lomake.button.next"/>
                        </c:otherwise>
                    </c:choose>
                </span>
            </span>
            </button>
        </c:when>
        <c:otherwise>
            <button class="right" name="vaiheId" type="submit" value="${vaihe.id}"><span><span><spring:message
                    code="lomake.button.save"/></span></span></button>
        </c:otherwise>
    </c:choose>
</div>
<div class="clear"></div>
