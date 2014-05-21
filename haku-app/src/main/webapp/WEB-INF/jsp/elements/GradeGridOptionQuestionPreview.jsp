<%@ page session="false"%>
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
  ~ You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
  ~
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ European Union Public Licence for more details.
  --%>
<a name="${element.id}"></a>
<c:set var="osaaminen" value="${it.application.answers.osaaminen}" scope="request" />
<c:set var="kymppiKey" value="${element.id}_10" scope="page" />
<c:forEach var="option" items="${element.options}">
    <c:if test="${(answers[kymppiKey] eq option.value)}">
        <c:set var="kymppiValue" value="${option.i18nText}"/>
    </c:if>
    <c:if test="${(answers[element.id] eq option.value)}">
        <c:set var="gradeValue" value="${option.i18nText}"/>
    </c:if>
</c:forEach>

<c:choose>
    <c:when test="${not empty kymppiValue}">
        <haku:i18nText value="${kymppiValue}" /> (<haku:i18nText value="${gradeValue}"/>)
    </c:when>
    <c:otherwise>
        <haku:i18nText value="${gradeValue}" />
    </c:otherwise>
</c:choose>