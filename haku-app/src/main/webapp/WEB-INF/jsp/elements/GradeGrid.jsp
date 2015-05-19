<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
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
<haku:errorMessage id="${element.id}" additionalClass="margin-top-1"/>
<haku:help element="${element}"/>

<table id="gradegrid-table" class="applicant-grades">
    <thead>
    <tr>
        <th colspan="6"><fmt:message key="lomake.component.gradegrid.gradesTitle"/></th>
    </tr>
    <tr>
        <td colspan="2" id="gradegrid-table-header-column2"><fmt:message key="lomake.component.gradegrid.subjectTitle"/></td>
        <c:choose>
            <c:when test="${element.extraColumn}">
                <td aria-label='<fmt:message key="lomake.component.gradegrid.commonSubjectColumnTitle"/>: <fmt:message key="lomake.component.gradegrid.subjectColumnTitle"/>' id="gradegrid-table-header-column3"><fmt:message key="lomake.component.gradegrid.commonSubjectColumnTitle"/></td>
            </c:when>
            <c:otherwise>
                <td id="gradegrid-table-header-column3"><fmt:message key="lomake.component.gradegrid.subjectColumnTitle"/></td>
            </c:otherwise>
        </c:choose>
        <c:if test="${element.extraColumn}">
            <td aria-label="<fmt:message key="lomake.component.gradegrid.optionalSubjectColumnTitle"/>: <fmt:message key="lomake.component.gradegrid.subjectColumnTitle"/>" id="gradegrid-table-header-column4"><fmt:message key="lomake.component.gradegrid.optionalSubjectColumnTitle"/></td>
            <td aria-label="<fmt:message key="lomake.component.gradegrid.second.optionalSubjectColumnTitle"/>: <fmt:message key="lomake.component.gradegrid.subjectColumnTitle"/>" id="gradegrid-table-header-column5"><fmt:message key="lomake.component.gradegrid.second.optionalSubjectColumnTitle"/></td>
            <td aria-label="<fmt:message key="lomake.component.gradegrid.third.optionalSubjectColumnTitle"/>: <fmt:message key="lomake.component.gradegrid.subjectColumnTitle"/>" id="gradegrid-table-header-column6"><fmt:message key="lomake.component.gradegrid.third.optionalSubjectColumnTitle"/></td>
        </c:if>
    </tr>
    </thead>
    <tbody>
    <haku:viewChilds element="${element}"/>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/javascript/gradegrid.js"></script>
    </tbody>
</table>
