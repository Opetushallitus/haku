<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
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

<c:set value="${element.id}-Opetuspiste" var="textInputId" scope="page"/>
<c:set value="${element.id}-Koulutus" var="selectInputId" scope="page"/>
<c:set value="${element.id}-Koulutus-id" var="selectHiddenInputId" scope="page"/>
<c:set value="${categoryData[selectHiddenInputId]}" var="hakukohdeId" scope="page"/>
<tr id="${element.id}-Opetuspiste">
    <td class="label"><c:out value="${element.title}"/></td>
    <td class="sublabel"><c:out value="${element.learningInstitutionLabel}"/></td>
    <td><c:out value="${categoryData[textInputId]}"/></td>
</tr>
<tr id="${element.id}-Koulutus">
    <td class="label"></td>
    <td class="sublabel"><c:out value="${element.educationLabel}"/></td>
    <td><c:out value="${categoryData[selectInputId]}"/></td>
</tr>
<script type="text/javascript">
    if ("${hakukohdeId}") {
        var url = "/haku/education/additionalquestion/${hakemusId.applicationPeriodId}/${hakemusId.formId}/${vaihe.id}/${parent.id}/${hakukohdeId}?preview=true";
        $.get(url, function(data) {
            $("#${element.id}-Koulutus").after(data);
        });
    }
</script>
