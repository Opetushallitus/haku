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
<tr>
    <td>
        <fieldset class="${styleBaseClass}">
            <div class="field-container-checkbox">
                <input type="checkbox" name="${element.id}"
                       disabled="true" ${(categoryData[element.id] eq element.value) ? "checked=\"checked\"" : ""} value="${element.value}"/>
                <label for="${element.id}"><haku:i18nText value="${element.i18nText}"/></label>
            </div>
        </fieldset>
    </td>
</tr>
<haku:viewChilds element="${element}"/>
