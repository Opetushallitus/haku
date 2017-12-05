<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<%@ page session="false"%>
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

<c:if test="${officerUi}">
    <tr>
        <td colspan="8">
            <fmt:message key="lomake.component.gradegrid.muokkaaArvosanoja"/>
            <a href="/suoritusrekisteri/#/muokkaa/${it.application.personOid}" target="_blank">
                <fmt:message key="virkailija.hakemus.suoritusrekisterissa"/>
            </a>
        </td>
    </tr>
</c:if>

<tr>
    <td colspan="2"><a name="${element.id}"></a><fmt:message key="lomake.component.gradegrid.subjectTitle"/></td>
    <c:choose>
        <c:when test="${element.extraColumn}">
            <td><fmt:message key="lomake.component.gradegrid.commonSubjectColumnTitle"/></td>
        </c:when>
        <c:otherwise>
            <td><fmt:message key="lomake.component.gradegrid.subjectColumnTitle"/></td>
        </c:otherwise>
    </c:choose>
    <c:if test="${element.extraColumn}">
        <td><fmt:message key="lomake.component.gradegrid.optionalSubjectColumnTitle"/></td>
        <td><fmt:message key="lomake.component.gradegrid.second.optionalSubjectColumnTitle"/></td>
        <td><fmt:message key="lomake.component.gradegrid.third.optionalSubjectColumnTitle"/></td>
    </c:if>
</tr>
<haku:viewChilds element="${element}"/>

<script>
    $(document).ready(function () {
        $("tr[id|='additionalRow']").each(function () {
            if ($(this).find("td").filter(function () {
                return $.trim($(this).text());
            }).length !== 1 ) {
                $(this).show();
            } else {
                $(this).hide();
            }
        })
    });
</script>
