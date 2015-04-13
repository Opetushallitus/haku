<%@ page session="false" %>
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

<%-- set education specific additional questions for this theme --%>
<fieldset id="${element.id}">
    <legend class="h3"><haku:i18nText value="${element.i18nText}"/></legend>
    <hr role="presentation"/>
    <div class="theme-help">
        <div class="help-text"><haku:i18nText value="${element.help}"/></div>
        <button class="helplink" type="submit" formtarget="_blank" formaction="${element.id}/help">?</button>
        <div role="presentation" class="clear"></div>
    </div>
    <haku:viewChilds element="${element}"/>
</fieldset>
