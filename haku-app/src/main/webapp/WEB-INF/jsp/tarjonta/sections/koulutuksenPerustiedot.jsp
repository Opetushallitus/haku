<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
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
<fmt:setBundle basename="messages"/>
<div class="infobox">

    <h3><fmt:message key="tarjonta.koulutuksenperustiedot.otsikko"/></h3>

    <ul class="minimal">
        <li class="heading"><fmt:message key="tarjonta.koulutuksenperustiedot.koulutusala"/></li>
        <li class="emphasized"><c:out value="${it.searchResult['LOSEducationDomain']}"/></li>

        <li class="heading"><fmt:message key="tarjonta.koulutuksenperustiedot.opintoala"/></li>
        <li class="emphasized"><c:out value="${it.searchResult['LOSStydyDomain']}"/></li>

        <li class="heading"><fmt:message key="tarjonta.koulutuksenperustiedot.koulutusohjelma"/></li>
        <li class="emphasized"><c:out value="${it.searchResult['LOSName']}"/></li>

        <li class="heading"><fmt:message key="tarjonta.koulutuksenperustiedot.tutkinto"/></li>
        <li class="emphasized"><c:out value="${it.searchResult['LOSDegreeTitle']}"/></li>

        <li class="heading"><fmt:message key="tarjonta.koulutuksenperustiedot.tutkintonimike"/></li>
        <li class="emphasized"><c:out value="${it.searchResult['LOSQualification']}"/></li>

        <li class="heading"><fmt:message key="tarjonta.koulutuksenperustiedot.opintojenlaajuus"/></li>
        <li class="emphasized"><c:out value="${it.searchResult['LOSCredits']}"/>&nbsp;<fmt:message
                key="tarjonta.koulutuskuvaus.${it.searchResult['LOSCreditsUnit']}"/></li>
    </ul>
</div>
