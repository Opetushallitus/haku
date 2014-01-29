<%@ page session="false"%>
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
<select ${element.attributeString}>
    <c:if test="${not element.selected}">
        <option value="">&nbsp;</option>
    </c:if>
    <c:choose>
        <c:when test="${element.sortByText}">
            <c:forEach var="option" items="${element.optionsSortedByText[requestScope['fi_vm_sade_oppija_language']]}">
                <c:set value="${element.id}.${option.id}" var="optionId" scope="page"/>
                <option value="${option.value}" ${(answers[element.id] eq option.value or (answers[element.id] eq null and option.defaultOption)) ? "selected=\"selected\" " : " "}><haku:i18nText value="${option.i18nText}"/>&nbsp;</option>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <c:forEach var="option" items="${element.options}">
                <c:set value="${element.id}.${option.id}" var="optionId" scope="page"/>
                <option value="${option.value}" ${(answers[element.id] eq option.value or (answers[element.id] eq null and option.defaultOption)) ? "selected=\"selected\" " : " "}><haku:i18nText value="${option.i18nText}"/>&nbsp;</option>
            </c:forEach>
        </c:otherwise>
    </c:choose>
</select>
<script>
    $(document).ready(function () {
        if (${not (answers[element.id] eq null)}) {
            var element = $("#${element.id}");
            var row = element.closest('tr');
            row.removeAttr('hidden');
            row.find('*:disabled').attr("disabled", false);
            var group = element.closest('tr').attr('data-group');
        }
    })
</script>
<haku:errorMessage id="${element.id}" additionalClass="margin-top-1"/>
<haku:help element="${element}"/>
