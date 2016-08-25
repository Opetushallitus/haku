<%@ page session="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
<c:set var="radio" value="${element}" />
<c:set var="styleBaseClass" value="${radio.inline ? 'form-row' : 'form-item'}"/>
<fieldset class="${styleBaseClass}">
    <legend id="${radio.id}_legend" class="${styleBaseClass}-label ${radio.attributes['required']}">
        <haku:i18nText value="${radio.i18nText}" escape="false"/><haku:popup element="${radio}"/>
    </legend>
    <div class="${styleBaseClass}-content">
        <haku:help element="${radio}"/>
        <c:set var="value" value="${answers[radio.id]}"/>
        <c:forEach var="option" items="${radio.options}" varStatus="status">
            <c:choose>
                <c:when test="${empty value}">
                    <c:set var="checked" value="${option.defaultOption}" />
                </c:when>
                <c:otherwise>
                    <c:set var="checked" value="${value eq option.value}" />
                </c:otherwise>
            </c:choose>
            <haku:errorMessage id="${option.id}"/>
            <div role="presentation" class="field-container-radio">
                <input aria-labelledby="${radio.id}_legend ${radio.id}_${option.value}_label" type="radio" name="${radio.id}"
                       value="${option.value}" ${(!empty disabled) ? "disabled=\"true\" " : " "} ${checked ? "checked=\"checked\"" : ""} id="${radio.id}_${option.value}"  ${option.attributeString}/>
                <label id="${radio.id}_${option.value}_label" for="${radio.id}_${option.value}"><haku:i18nText value="${option.i18nText}" escape="false"/></label>
                <haku:help element="${option}"/>
                <haku:viewChilds element="${option}"/>
            </div>
        </c:forEach>
        <haku:errorMessage id="${radio.id}"/>
    </div>
    <div role="presentation" class="clear"></div>
</fieldset>
