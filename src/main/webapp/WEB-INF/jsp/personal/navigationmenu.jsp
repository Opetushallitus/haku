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
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<div class="grid16-4">
    <nav class="subnavigation">
        <ul class="menu-level-2">
            <li><a href="personalservice"><spring:message code="oma.palvelu"/></a></li>
            <li><a href="personalguide"><spring:message code="oma.omaopas"/></a></li>
            <li><a href="messages"><spring:message code="oma.viestit"/></a></li>
            <li><a href="applicationperiods"><spring:message code="oma.haut"/></a></li>
            <ul class="menu-level-3">
                <li><a href="applications"><spring:message code="oma.haut.hakemukset"/></a></li>
                <li><a href="results"><spring:message code="oma.haut.tulokset"/></a></li>
                <li><a href="accept"><spring:message code="oma.haut.vastaanotto"/></a></li>
                <li><a href="guidance"><spring:message code="oma.haut.jalkiohjaus"/></a></li>
            </ul>
            <li><a href="details"><spring:message code="oma.tiedot"/></a></li>
            <li><a href="credits"><spring:message code="oma.suoritukset"/></a></li>
            <li><a href="notes"><spring:message code="oma.muistiinpanot"/></a></li>
        </ul>
    </nav>
</div>
