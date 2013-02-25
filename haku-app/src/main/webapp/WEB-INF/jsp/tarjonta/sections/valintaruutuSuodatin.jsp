<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
<fmt:setBundle basename="messages"/>
<c:if test="${ not empty filters[param.name]}">
    <fieldset class="form-item">
        <legend class="h3 form-item-label"><fmt:message key="tarjonta.haku.${param.name}"/></legend>
        <div class="form-item-content">
            <c:forEach var="filter" items="${filters[param.name]}">
                <c:set var="selected" value=""/>
                <c:forEach var="paramValue" items="${paramValues[param.name]}">
                    <c:if test="${filter eq paramValue}">
                        <c:set var="selected" value="checked='checked'"/>
                    </c:if>
                </c:forEach>
                <div class="field-container-checkbox">
                    <input class="suodatin" type="checkbox" name='${param.name}' value='${filter}'
                           id="${param.name}_${filter}" ${selected}/>
                    <label for="${param.name}_${filter}"><c:out value="${filter}"/></label>
                </div>
            </c:forEach>
        </div>
        <div class="clear"></div>
    </fieldset>
</c:if>

