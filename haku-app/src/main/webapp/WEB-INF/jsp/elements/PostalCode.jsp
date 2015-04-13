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
<div class="form-row" id="container-${element.id}">
    <haku:label element="${element}" styleBaseClass="form-row"/>
    <div class="form-row-content">
        <div class="field-container-text">
            <input type="text" id="${element.id}" ${element.attributeString} name="${element.id}" <haku:placeholder titled="${element}"/> <haku:value value='${answers[element.id]}'/>
                   class="postal-code"/>
            <span class="post-office"><haku:i18nText value="${element.data[answers[element.id]].i18nText}"/></span>
        </div>
        <haku:errorMessage id="${element.id}"/>
        <haku:help element="${element}"/>
    </div>
    <div role="presentation" class="clear"></div>
    <haku:viewChilds element="${element}"/>
</div>
<script type="text/javascript">
    var postalcode_settings = {
        id: "${element.id}"
    }
</script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/javascript/postalcode.js"></script>
