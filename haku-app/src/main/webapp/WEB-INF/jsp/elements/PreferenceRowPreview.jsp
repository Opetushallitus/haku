<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="/WEB-INF/tld/functions.tld" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
<fmt:setBundle basename="form_messages" scope="session"/>
<c:set value="${element.id}-Opetuspiste" var="textInputId" scope="page"/>
<c:set value="${element.id}-Koulutus" var="selectInputId" scope="page"/>
<c:set value="${element.id}-Koulutus-id" var="selectHiddenInputId" scope="page"/>
<c:set value="${element.id}-Koulutus-id-aoIdentifier" var="aoIdentifier" scope="page"/>
<c:if test="${not empty categoryData[selectHiddenInputId]}">
    <tr>
        <td class="padding-top-3"><span class="margin-right-5 bold"><c:out value="${index}"/></span></td>
        <td class="sublabel padding-top-3"><haku:i18nText value="${element.learningInstitutionLabel}"/></td>
        <td class="bold padding-top-3"><c:out value="${categoryData[textInputId]}"/></td>
    </tr>
    <tr>
        <td></td>
        <td class="sublabel"><haku:i18nText value="${element.educationLabel}"/></td>
        <td class="bold"><c:out value="${categoryData[selectInputId]}"/>
            <c:if test="${virkailijaPreview}">
                &nbsp;[<c:out value="${categoryData[aoIdentifier]}"/>]
            </c:if>
        </td>
    </tr>
    <c:if test="${not empty fn:children(element, categoryData)}">
        <tr>
            <td></td>
            <td class="sublabel"><fmt:message key="form.hakutoiveet.kysymykset"/></td>
            <td>
                <table class="additional-questions-table width-100">
                    <tbody>
                    <haku:viewChilds element="${element}"/>
                    </tbody>
                </table>
            </td>
        </tr>
    </c:if>
</c:if>
