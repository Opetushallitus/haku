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
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<fmt:setBundle basename="messages"/>
<div class="grid16-4">
    <nav class="subnavigation">
        <ul class="menu-level-2">
            <li><a href="personalservice"><fmt:message key="oma.palvelu"/></a></li>
            <li><a href="personalguide"><fmt:message key="oma.omaopas"/></a></li>
            <li><a href="messages"><fmt:message key="oma.viestit"/></a></li>
            <li><a href="applicationperiods"><fmt:message key="oma.haut"/></a></li>
            <ul class="menu-level-3">
                <li><a href="applications"><fmt:message key="oma.haut.hakemukset"/></a></li>
                <li><a href="results"><fmt:message key="oma.haut.tulokset"/></a></li>
                <li><a href="accept"><fmt:message key="oma.haut.vastaanotto"/></a></li>
                <li><a href="guidance"><fmt:message key="oma.haut.jalkiohjaus"/></a></li>
            </ul>
            <li><a href="details"><fmt:message key="oma.tiedot"/></a></li>
            <li><a href="credits"><fmt:message key="oma.suoritukset"/></a></li>
            <li><a href="notes"><fmt:message key="oma.muistiinpanot"/></a></li>
        </ul>
    </nav>
</div>
