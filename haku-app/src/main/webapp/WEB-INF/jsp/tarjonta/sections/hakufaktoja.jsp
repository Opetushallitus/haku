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
    <h3><fmt:message key="tarjonta.hakufaktoja.otsikko"/></h3>
    <ul class="minimal">
        <li class="heading"><fmt:message key="tarjonta.hakufaktoja.haunnimi"/></li>
        <li class="emphasized"><c:out value="${it.searchResult['AOTitle']}"/></li>
        <li class="heading"><fmt:message key="tarjonta.hakufaktoja.hakukelpoisuus"/></li>
        <li class="emphasized"><c:out value="${it.searchResult['AOEligibilityRequirements']}"/></li>
        <li class="heading"><fmt:message key="tarjonta.hakufaktoja.valintakoe"/></li>
        <li class="emphasized"><fmt:formatDate type="date" value="${it.searchResult['AOExaminationStart']}"/></li>
        <li class="set-right"><fmt:message key="tarjonta.hakufaktoja.hakuaikaalkaa"/>&nbsp
            <fmt:formatDate type="date" value="${it.searchResult['tmpASStart']}"/></li>
    </ul>
    <form action="${pageContext.request.contextPath}/lomake/${it.searchResult['ASId']}/${it.searchResult['formId']}"
          method="post">
        <input type="hidden" name="preference1-Opetuspiste" value="${it.searchResult['LOPInstitutionInfoName']}"/>
        <input type="hidden" name="preference1-Opetuspiste-id" value="${it.searchResult['LOPId']}"/>
        <input type="hidden" name="preference1-Koulutus" value="${it.searchResult['AOTitle']}"/>
        <input type="hidden" name="preference1-Koulutus-id" value="${it.searchResult['AOId']}"/>
        <input type="hidden" name="preference1-Koulutus-educationDegree" value="${it.searchResult['AOEducationDegree']}"/>
        <input type="hidden" name="enabling-submit"/>
        <button type="submit">
            <span><span><fmt:message key="tarjonta.haekoulutukseen"/></span></span>
        </button>
    </form>
</div>
