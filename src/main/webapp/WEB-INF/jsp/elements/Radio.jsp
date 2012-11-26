<%@ taglib uri="http://java.sun.com/jsp/jstl/core"
           prefix="c" %>
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
<fieldset class="${styleBaseClass}">
    <legend class="${styleBaseClass}-label ${element.attributes['required'].value}"><c:out
            value="${element.title}"/></legend>
    <div class="${styleBaseClass}-content">
        <span class="required_field">${errorMessages[element.id]}</span>
        <c:set var="value" value="${(empty value) ? categoryData[element.id] : value}"/>
        <c:forEach var="option" items="${element.options}" varStatus="status">
            <div class="field-container-radio">
                <c:set var="id" value="${option.id}"/>
                <input id="${id}" type="radio" name="${element.id}"
                       value="${option.value}" ${(!empty disabled) ? "disabled=\"true\" " : " "} ${(value eq option.value) ? "checked=\"checked\" " : " "} ${option.attributeString}/>
                <label for="${option.id}">${option.title}</label>

                <div id="help-${element.id}-${option.id}">
                    <small>${option.help}</small>
                </div>
            </div>
        </c:forEach>

    </div>
    <div class="clear"></div>
    <haku:viewChilds element="${element}"/>
</fieldset>
