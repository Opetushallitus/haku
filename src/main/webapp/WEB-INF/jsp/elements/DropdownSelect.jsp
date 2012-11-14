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
    <label class="${styleBaseClass}-label" for="label-${element.id}">${element.title}</label>

    <div class="${styleBaseClass}-content">
        <select ${element.attributeString}>
            <c:forEach var="option" items="${element.options}">
                <c:set value="${element.id}.${option.id}" var="optionId" scope="page"/>
                <option name="${optionId}"
                        value="${option.value}" ${(categoryData[element.id] eq option.value) ? "selected=\"selected\" " : " "} ${option.attributeString}>${option.title}</option>
            </c:forEach>
        </select>
        <small>${element.help}</small>
    </div>
    <div class="clear"></div>

    <haku:viewChilds element="${element}"/>
</div>




