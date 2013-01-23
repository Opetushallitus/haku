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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<fmt:setBundle basename="messages"/>
<section class="content-container">

    <div class="grid16-12">
        <h1>Kuluvan hakukauden hakemukset (${fn:length(UserApplicationInfo)} kpl)</h1>
        <c:forEach var="info" items="${UserApplicationInfo}">
            <div><a href="${info.application.vaiheId}">${info.application.vaiheId}</a></div>
            <div><fmt:message key="oma.lomake.tila.vireilla.${info.pending}"/></div>
            <li class="${('1' eq info.application.vaiheId) ? "current" : ""}"><fmt:message
                    key="oma.hakemus.tila.lahetys"/></li>
            <li class="${('2' eq info.application.vaiheId) ? "current" : ""}"><fmt:message
                    key="oma.hakemus.tila.liitteet"/></li>
            <li class="${('3' eq info.application.vaiheId) ? "current" : ""}"><fmt:message
                    key="oma.hakemus.tila.valintakokeet"/></li>
            <li class="${('4' eq info.application.vaiheId) ? "current" : ""}"><fmt:message
                    key="oma.hakemus.tila.valintapaatos"/></li>
            <li class="${('5' eq info.application.vaiheId) ? "current" : ""}"><fmt:message
                    key="oma.hakemus.tila.vastaanotto"/></li>
            <li class="${('6' eq info.application.vaiheId) ? "current" : ""}"><fmt:message
                    key="oma.hakemus.tila.ilmoittautuminen"/></li>
        </c:forEach>

        <h1>Aiempien hakukausien hakemukset (${fn:length(prevPeriodApplications)} kpl)</h1>
        <c:forEach var="application" items="${prevPeriodApplications}">

        </c:forEach>
    </div>

</section>
