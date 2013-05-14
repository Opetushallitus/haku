<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
    <legend class="${styleBaseClass}-label ${element.attributes['required'].value}"><haku:i18nText
            value="${element.i18nText}"/><haku:popup element="${element}" /></legend>
    <div class="${styleBaseClass}-content">
        <c:set var="value" value="${(empty value) ? categoryData[element.id] : value}"/>
        <c:forEach var="option" items="${element.options}" varStatus="status">
            <haku:errorMessage id="${option.id}"/>
            <div class="field-container-radio">
                <input type="radio" name="${element.id}"
                       value="${option.value}" ${(!empty disabled) ? "disabled=\"true\" " : " "} ${(value eq option.value) ? "checked=\"checked\" " : " "} ${option.attributeString}/>
                <label for="${option.id}"><haku:i18nText value="${option.i18nText}"/></label>
                <haku:help element="${option}"/>
                <haku:viewChilds element="${option}"/>
            </div>
        </c:forEach>
        <haku:errorMessage id="${element.id}"/>
        <haku:help element="${element}"/>
    </div>
    <div class="clear"></div>
    <haku:viewChilds element="${element}"/>
</fieldset>
