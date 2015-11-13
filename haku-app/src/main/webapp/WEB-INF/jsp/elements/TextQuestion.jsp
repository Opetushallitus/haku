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
<c:set var="styleBaseClass" value="${element.inline ? 'form-row' : 'form-item'}"/>
<c:set var="displayAsInputMaxChars" value="${element.inline ? 80 : 100}"/>
<div class="${styleBaseClass}">
    <haku:label element="${element}" styleBaseClass="${styleBaseClass}"/>
    <div class="${styleBaseClass}-content">
        <c:if test="${!element.inline}">
            <haku:help element="${element}"/>
        </c:if>
        <div class="field-container-text">
            <c:choose>
                <c:when test="${empty element.attributes.size || element.attributes.size <= displayAsInputMaxChars}">
                    <input type="text" ${element.attributeString} id="${element.id}" name="${element.id}" <haku:placeholder titled="${element}"/> <haku:value value='${answers[element.id]}'/> />
                </c:when>
                <c:otherwise>
                    <textarea cols="80" rows="4" ${element.attributeString} id="${element.id}" name="${element.id}"
                            <haku:placeholder titled="${element}"/>><c:out value="${answers[element.id]}"/></textarea>
                </c:otherwise>
            </c:choose>
            <haku:errorMessage id="${element.id}" additionalClass="margin-top-1"/>
        </div>
        <c:if test="${element.inline}">
            <haku:help element="${element}"/>
        </c:if>
    </div>
    <div role="presentation" class="clear"></div>
    <haku:viewChilds element="${element}"/>
</div>
