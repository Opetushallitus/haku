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

<c:set var="show" value="false"/>
<c:forEach var="key" items="${element.aoEducationDegreeKeys}">
    <c:if test="${categoryData[key] eq element.requiredEducationDegree}">
        <c:set var="show" value="true"/>
    </c:if>
</c:forEach>

<c:if test="${show eq 'true'}">
    <jsp:include page="./Theme.jsp"/>
</c:if>