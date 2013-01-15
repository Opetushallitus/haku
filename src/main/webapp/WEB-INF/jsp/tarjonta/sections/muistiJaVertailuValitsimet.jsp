<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
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
<div class="result-options set-right">
    <div class="field-container-checkbox left-intend-2" style="display: inline-block">
        <input type="checkbox" name="muistilistaan" value="${it.id}" id="muistilista_${it.id}"/>
        <label for="muistilista_${it.id}"><spring:message code="tarjonta.lisaamuistilistaan"/></label>
    </div>

    <div class="field-container-checkbox left-intend-2">
        <input type="checkbox" name="vertailulistaan" value="${it.id}" id="vertailulista_${it.id}"/>
        <label for="vertailulista_${it.id}"><spring:message code="tarjonta.lisaavertailulistaan"/></label>
    </div>
</div>
