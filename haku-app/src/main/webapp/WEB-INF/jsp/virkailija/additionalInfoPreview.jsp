<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="virkailija" tagdir="/WEB-INF/tags/virkailija" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
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
<c:set var="additionalInfo" value="${it.application.additionalInfo}" scope="request"/>
<fieldset>
    <legend class="h3"><fmt:message key="virkailija.lisakysymys.otsikko"/></legend>
    <hr/>
    <sec:authorize access="hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_CRUD')">
    <virkailija:EditButton url="${pageContext.request.contextPath}/virkailija/hakemus/${oid}/additionalInfo"
                           application="${it.application}"/>
    </sec:authorize>
    <table class="form-summary-table width-50">
        <tbody>
        <c:forEach var="data" items="${additionalInfo}">
            <tr>
                <td class="label"><c:out value='${data.key}' escapeXml="true"/></td>
                <td><c:out value='${data.value}' escapeXml="true"/></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</fieldset>
