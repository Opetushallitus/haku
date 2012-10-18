<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags"%>
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
        var url = "/haku/education/additionalquestion/${hakemusId.applicationPeriodId}/${hakemusId.formId}/${hakemusId.categoryId}/${parent.id}/${hakukohdeId}?preview=true";
        $.get(url, function(data) {
          $("#${element.id}-Koulutus").after(data);
        });
    }
</script>