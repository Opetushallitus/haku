<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="id" required="true" type="java.lang.String" %>
<%@ attribute name="additionalClass" required="false" type="java.lang.String" %>
<%@ tag trimDirectiveWhitespaces="true" %>
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
<c:if test="${not empty errorMessages[id]}">
    <button onclick='$("#${id}-error").fadeOut()' type="button" id="${id}-error" class="notification warning ${additionalClass}"><haku:i18nText
            value="${errorMessages[id]}"/></button>
    <script>$("[name='${id}']").change(function () {
        $("#${id}-error").fadeOut();
    });</script>
</c:if>
