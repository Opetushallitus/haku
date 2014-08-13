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
            <textarea ${element.attributeString} <haku:placeholder titled="${element}"/>><c:out value="${answers[element.id]}"/></textarea>
        </div>
        <haku:errorMessage id="${element.id}"/>
        <haku:help element="${element}"/>
    </div>
    <div class="clear"></div>
    <haku:viewChilds element="${element}"/>
</div>
