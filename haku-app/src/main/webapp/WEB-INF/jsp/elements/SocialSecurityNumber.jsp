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

<c:set var="ssnElement" value="${element}"/>

<c:set var="element" value="${ssnElement.ssn}" scope="request"/>

<c:set var="styleBaseClass" value="${element.inline ? 'form-row' : 'form-item'}"/>
<div class="${styleBaseClass}">
    <haku:label element="${element}" styleBaseClass="${styleBaseClass}"/>
    <div class="${styleBaseClass}-content">
        <div class="field-container-text">
            <c:choose>
                <c:when test="${virkailijaEdit and not empty answers[element.id]}">
                    <input disabled="disabled" value="<c:out value='${answers[element.id]}'/>"/>
                    <input name="${element.id}" type="hidden" value="<c:out value='${answers[element.id]}'/>"/>
                </c:when>
                <c:otherwise>
                    <input ${element.attributeString} value="<c:out value='${answers[element.id]}'/>"/>
                </c:otherwise>
            </c:choose>
            <span id="sex">
                <c:if test="${answers[ssnElement.sexId] eq ssnElement.maleOption.value}">
                    <haku:i18nText value="${ssnElement.maleOption.i18nText}"/>
                </c:if>
                <c:if test="${answers[ssnElement.sexId] eq ssnElement.femaleOption.value}">
                    <haku:i18nText value="${ssnElement.femaleOption.i18nText}"/>
                </c:if>
            </span>
            <input id="${ssnElement.sexId}" name="${ssnElement.sexId}" value="<c:out value='${answers[ssnElement.sexId]}' />" type="hidden"/>
            <haku:errorMessage id="${element.id}" additionalClass="margin-top-1"/>
        </div>
        <haku:help element="${element}"/>
    </div>
    <div class="clear"></div>
</div>


<script>
    (function() {
        var ssnId = "<c:out value="${ssnElement.ssn.id}"/>",
            maleLabel = "<haku:i18nText value="${ssnElement.maleOption.i18nText}"/>",
            femaleLabel = "<haku:i18nText value="${ssnElement.femaleOption.i18nText}"/>",
            maleValue = "<c:out value="${ssnElement.maleOption.value}"/>",
            femaleValue = "<c:out value="${ssnElement.femaleOption.value}"/>",
            sexId = "<c:out value="${ssnElement.sexId}"/>";
        $("#" + ssnId).change(function() {
            var maleReg   = /\d{6}[-+aA]\d{2}[13579]\w/;
            var femaleReg = /\d{6}[-+aA]\d{2}[02468]\w/;
            if (maleReg.test($("#" + ssnId).val())) {
                $("#sex").html(maleLabel);
                $("#" + sexId).val(maleValue);
            } else if (femaleReg.test($("#" + ssnId).val())) {
                 $("#sex").html(femaleLabel);
                 $("#" + sexId).val(femaleValue);
            } else {
                 $("#sex").html("");
                 $("#" + sexId).val("");
            }
        });
    }());
</script>

