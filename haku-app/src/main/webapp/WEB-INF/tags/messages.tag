<%@ tag description="i18nText" body-content="empty" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="f" uri="/WEB-INF/tld/functions.tld" %>
<%@ attribute name="messages" required="true" type="java.util.Map" %>
<%@ attribute name="additionalClass" required="true" type="java.lang.String" %>
<%@ attribute name="form" required="true" type="fi.vm.sade.haku.oppija.lomake.domain.elements.Form" %>
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
<c:forEach var="message" items="${messages}">
    <c:set var="jumpTo" value="" />
    <c:set var="title" value="" />
    <c:forEach var="element" items="${f:allChildren(form)}">
        <c:if test="${element.id eq message.key}">
            <c:set var="jumpTo" value="${element.id}" />
            <c:catch var="e">
                <c:set var="title" value="${element.i18nText.translations[requestScope['fi_vm_sade_oppija_language']]}" />
            </c:catch>
        </c:if>
    </c:forEach>
    <div class="notification ${additionalClass}" title="${title}">
        <a href="#${jumpTo}"><haku:i18nText value="${message.value}"/></a>
    </div>
</c:forEach>

