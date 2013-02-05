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

<!-- gradeSelectId has to be set -->
<div class="field-container-select">
    <select id="${gradeSelectId}" name="${gradeSelectId}" required = "required">
        <c:if test="${showEmptyOption}"><option></option></c:if>
        <c:forEach var="grade" items="${element.gradeRange}">
            <option value="${grade.value}" ${(categoryData[gradeSelectId] eq grade.value) ? "selected=\"selected\"" : ""}>
                <haku:i18nText value="${grade.i18nText}"/></option>
        </c:forEach>
    </select>
    <haku:errorMessage id="${gradeSelectId}"/>
</div>
