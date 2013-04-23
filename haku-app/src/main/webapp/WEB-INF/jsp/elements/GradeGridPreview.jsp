<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
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

<fmt:setBundle basename="messages"/>
<tr>
    <th></th>
    <th colspan="2"><fmt:message key="lomake.component.gradegrid.gradesTitle"/></th>
</tr>
<tr>
    <td><fmt:message key="lomake.component.gradegrid.subjectTitle"/></td>
    <td></td>
    <td><fmt:message key="lomake.component.gradegrid.commonSubjectColumnTitle"/></td>
    <td><fmt:message key="lomake.component.gradegrid.optionalSubjectColumnTitle"/></td>
    <c:if test="${element.extraColumn}">
        <td><fmt:message key="lomake.component.gradegrid.second.optionalSubjectColumnTitle"/></td>
    </c:if>

</tr>
<haku:viewChilds element="${element}"/>

<script>
    $(document).ready(function () {
        $("tr[hidden]").show();
    })
</script>
