<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
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
<div class="infobox">
    <h3><spring:message code="tarjonta.hakufaktoja.otsikko"/></h3>
    <ul class="minimal">
        <li class="heading"><spring:message code="tarjonta.hakufaktoja.haunnimi"/></li>
        <li class="emphasized"><c:out value="${searchResult['AOTitle']}"/></li>
        <li class="heading"><spring:message code="tarjonta.hakufaktoja.hakukelpoisuus"/></li>
        <li class="emphasized"><c:out value="${searchResult['AOEligibilityRequirements']}"/></li>
        <li class="heading"><spring:message code="tarjonta.hakufaktoja.valintakoe"/></li>
        <li class="emphasized"><fmt:formatDate type="date" value="${searchResult['AOExaminationStartDate']}" /></li>
        <li class="set-right"><spring:message code="tarjonta.hakufaktoja.hakuaikaalkaa"/>&nbsp
        <fmt:formatDate type="date" value="${searchResult['tmpASStart']}" /></li>
    </ul>
    <form action="/haku/lomake/${searchResult['formPath']}" method="post">
        <input type="hidden" name="preference1-Opetuspiste" value="Koulu7"/>
        <input type="hidden" name="preference1-Opetuspiste-id" value="7"/>
        <input type="hidden" name="preference1-Koulutus" value="Hakukohde_7_0"/>
        <input type="hidden" name="preference1-Koulutus-id" value="7_0"/>
        <input type="hidden" name="enabling-submit"/>
        <button type="submit">
            <span><span><spring:message code="tarjonta.haekoulutukseen"/></span></span>
        </button>
    </form>
</div>
