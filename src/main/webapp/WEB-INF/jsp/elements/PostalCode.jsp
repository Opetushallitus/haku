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

<div class="form-row">
    <label id="label-${element.id}" for="${element.id}"
           class="form-row-label ${element.attributes['required'].value}"><c:out value="${element.title}"/></label>

    <div class="form-row-content">
        <div class="field-container-text">
            <input type="text" ${element.attributeString} value="${categoryData[element.id]}" class="postal-code"/><span
                class="required_field">${errorMessages[element.id]}</span>
            <input type="hidden" value="${categoryData['postitoimipaikka']}" name="postitoimipaikka"
                   class="post-office"/>
            <span class="post-office"><c:out value="${categoryData['postitoimipaikka']}"/></span>
        </div>
        <div id="help-${element.id}">
            <small><c:out value="${element.help}"/></small>
        </div>
    </div>
    <div class="clear"></div>
    <haku:viewChilds element="${element}"/>
</div>
<script type="text/javascript">
    var postalcode_settings = {
        applicationPeriodId : "${hakemusId.applicationPeriodId}",
        formId : "${hakemusId.formId}",
        vaiheId : "${vaihe.id}",
        teemaId : "${parent.id}",
        contextPath : "${pageContext.request.contextPath}"
    }
</script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/javascript/postalcode.js"></script>
