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
  ~ You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
  ~
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ European Union Public Licence for more details.
  --%>
<c:choose>
    <c:when test="${print}">
        <tr>
            <td><haku:i18nText value="${element.i18nText}" escape="false"/></td>
            <td>
                <c:choose>
                    <c:when test="${(answers[element.id] eq element.value)}">
                        <fmt:message key="lomake.tulostus.kylla"/>
                    </c:when>
                    <c:otherwise>
                        <fmt:message key="lomake.tulostus.ei"/>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
    </c:when>
    <c:otherwise>
        <c:set var="styleBaseClass" value="${element.inline ? 'form-row' : 'form-item'}"/>
        <tr>
            <td colspan="2">
                <input type="checkbox" name="${element.id}"
                       disabled="disabled" ${(answers[element.id] eq element.value) ? "checked=\"checked\"" : ""} value="${element.value}"/>
                <label><a name="${element.id}"></a><haku:i18nText value="${element.i18nText}" escape="false"/></label>
            </td>
        </tr>
    </c:otherwise>
</c:choose>
<haku:viewChilds element="${element}"/>
