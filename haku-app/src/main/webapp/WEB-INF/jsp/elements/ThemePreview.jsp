<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="/WEB-INF/tld/functions.tld" %>
<%@ taglib prefix="virkailija" tagdir="/WEB-INF/tags/virkailija" %>

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

<c:set var="showTheme" value="${false}"/>
<c:forEach var="child" items="${fn:allChildren(element)}">
    <c:if test="${!showTheme}">
        <c:set var="showTheme" value="${not empty categoryData[child.id]}"/>
    </c:if>
</c:forEach>

<c:if test="${showTheme or not (oid eq null)}">
    <fieldset>
        <legend class="h3"><haku:i18nText value="${element.i18nText}"/></legend>
        <hr>
        <c:forEach var="phase" items="${form.children}">
            <c:if test="${(not phase.preview)}">
                <c:forEach var="teema" items="${phase.children}">
                    <c:if test="${(teema.id eq element.id)}">
                        <c:choose>
                            <c:when test="${oid eq null}">
                                <c:set var="editUrl"
                                       value="${pageContext.request.contextPath}/lomake/${applicationSystemId}/${phase.id}"/>
                            </c:when>
                            <c:otherwise>
                                <c:set var="editUrl"
                                       value="${pageContext.request.contextPath}/virkailija/hakemus/${application.applicationSystemId}/${phase.id}/${oid}"/>
                            </c:otherwise>
                        </c:choose>
                        <c:if test="${it.virkailijaEditAllowed}">
                            <virkailija:EditButton url="${editUrl}" application="${application}"/>
                        </c:if>
                    </c:if>
                </c:forEach>
            </c:if>
        </c:forEach>
        <table class="form-summary-table width-80">
            <tbody>
            <haku:viewChilds element="${element}"/>
            </tbody>
        </table>
    </fieldset>
</c:if>
