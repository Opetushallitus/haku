<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags"%>
<c:set value="${element.id}-Opetuspiste" var="textInputId" scope="page"/>
<c:set value="${element.id}-Koulutus" var="selectInputId" scope="page"/>
<tr>
    <td class="label"><c:out value="${element.title}"/></td>
    <td class="sublabel"><c:out value="${element.learningInstitutionLabel}"/></td>
    <td><c:out value="${categoryData[textInputId]}"/></td>
</tr>
<tr>
    <td class="label"></td>
    <td class="sublabel"><c:out value="${element.educationLabel}"/></td>
    <td><c:out value="${categoryData[selectInputId]}"/></td>
</tr>