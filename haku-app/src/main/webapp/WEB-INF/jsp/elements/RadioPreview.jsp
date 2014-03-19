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
<c:remove var="value" scope="page"/>
<c:forEach var="option" items="${element.options}" varStatus="status">
    <c:if test="${(answers[element.id] eq option.value)}">
        <c:set var="value" value="${option}" scope="page"/>
    </c:if>
</c:forEach>
<tr>
    <c:choose>
        <c:when test="${element.inline or print}">
            <td class="label"><a name="${element.id}"></a><haku:i18nText value="${element.i18nText}"/></td>
            <td>
                <c:if test="${not empty value}">
                    <haku:i18nText value="${value.i18nText}"/>
                </c:if>
            </td>
        </c:when>
        <c:otherwise>
            <td class="label"><a name="${element.id}"></a><span><haku:i18nText value="${element.i18nText}"/>:</span></td>
            <td><haku:i18nText value="${value.i18nText}"/></td>
        </c:otherwise>
    </c:choose>
</tr>
<haku:viewChilds element="${element}"/>
