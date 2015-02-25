<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
<c:choose>
    <c:when test="${virkailijaEdit}">
        <jsp:include page="GradeGridOptionQuestionPreview.jsp" />
    </c:when>
    <c:otherwise>
        <haku:setSelectedValue element="${element}"/>
        <select ${element.attributeString} id="${element.id}" name="${element.id}">
            <c:if test="${not element.selected}">
                <option value="">&nbsp;</option>
            </c:if>
            <c:choose>
                <c:when test="${element.sortByText}">
                    <c:set var="options" value="${element.optionsSortedByText[requestScope['fi_vm_sade_oppija_language']]}"/>
                </c:when>
                <c:otherwise>
                    <c:set var="options" value="${element.options}"/>
                </c:otherwise>
            </c:choose>
            <haku:options options="${options}"/>
        </select>
        <c:remove var="selected_value" scope="request"/>
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
    </c:otherwise>
</c:choose>
