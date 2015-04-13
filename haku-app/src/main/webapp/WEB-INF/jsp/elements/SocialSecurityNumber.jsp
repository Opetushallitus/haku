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
<c:set var="styleBaseClass" value="${element.inline ? 'form-row' : 'form-item'}"/>
<div class="${styleBaseClass}">
    <haku:label element="${element}" styleBaseClass="${styleBaseClass}"/>
    <div class="${styleBaseClass}-content">
        <div class="field-container-text">
            <c:choose>
                <c:when test="${virkailijaEdit and not empty answers[element.id]}">
                    <input disabled="disabled" <haku:value value='${answers[element.id]}'/>/>
                    <input name="${element.id}" type="hidden" <haku:value value='${answers[element.id]}'/>/>
                </c:when>
                <c:otherwise>
                    <input ${element.attributeString} id="${element.id}" name="${element.id}" <haku:placeholder titled="${element}"/> <haku:value value='${answers[element.id]}'/> type="text"/>
                </c:otherwise>
            </c:choose>
            <span id="sex">
                <c:if test="${answers[element.sexId] eq element.maleOption.value}">
                    <haku:i18nText value="${element.maleOption.i18nText}"/>
                </c:if>
                <c:if test="${answers[element.sexId] eq element.femaleOption.value}">
                    <haku:i18nText value="${element.femaleOption.i18nText}"/>
                </c:if>
            </span>
            <haku:errorMessage id="${element.id}" additionalClass="margin-top-1"/>
        </div>
        <haku:help element="${element}"/>
    </div>
    <div role="presentation" class="clear"></div>
    <haku:viewChilds element="${element}"/>
</div>


<script>
    (function() {
        var ssnId = "<c:out value="${element.id}"/>",
            maleLabel = "<haku:i18nText value="${element.maleOption.i18nText}"/>",
            femaleLabel = "<haku:i18nText value="${element.femaleOption.i18nText}"/>";
        $("#" + ssnId).change(function() {
            var maleReg   = /\d{6}[-+aA]\d{2}[13579]\w/;
            var femaleReg = /\d{6}[-+aA]\d{2}[02468]\w/;
            if (maleReg.test($("#" + ssnId).val())) {
                $("#sex").html(maleLabel);
            } else if (femaleReg.test($("#" + ssnId).val())) {
                 $("#sex").html(femaleLabel);
            } else {
                 $("#sex").html("");
            }
        });
    }());
</script>

