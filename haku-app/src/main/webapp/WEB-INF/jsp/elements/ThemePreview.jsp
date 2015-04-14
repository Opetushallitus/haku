<%@ page session="false" %>
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
<fieldset>
    <legend class="h3"><haku:i18nText value="${element.i18nText}"/></legend>
    <hr role="presentation">

    <c:set var="editUrl"
           value="${pageContext.request.contextPath}/virkailija/hakemus/${application.applicationSystemId}/${currentPhase.id}/${oid}"/>

    <c:if test="${it.phaseEditAllowed[currentPhase.id]}">
        <virkailija:EditButton url="${editUrl}" application="${application}"/>
    </c:if>

    <table class="form-summary-table width-80" id="${element.id}">
        <tbody>
        <haku:viewChilds element="${element}"/>
        </tbody>
    </table>
</fieldset>
