<%@ page session="false" %>
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
<tr>
    <c:choose>
        <c:when test="${element.showAsTextarea}">
            <td class="label" colspan="2">
                <a name="${element.id}"></a><span><haku:i18nText value="${element.i18nText}"/>:</span>
                <div class="textareaPreview"
                     style="white-space: pre-wrap; word-break: break-all; word-wrap: break-word"><c:out value="${answers[element.id]}"/></div>
            </td>
        </c:when>
        <c:when test="${element.inline or print}">
            <td class="label"><a name="${element.id}"></a><haku:i18nText value="${element.i18nText}"/></td>
            <td><c:out value="${answers[element.id]}"/></td>
        </c:when>
        <c:otherwise>
            <td class="label"><a name="${element.id}"></a><span><haku:i18nText value="${element.i18nText}"/>:</span>
            </td>
            <td><c:out value="${answers[element.id]}"/></td>
        </c:otherwise>
    </c:choose>
    <haku:viewChilds element="${element}"/>
</tr>
