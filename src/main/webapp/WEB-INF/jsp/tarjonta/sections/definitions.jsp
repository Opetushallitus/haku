<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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

<div class="definitions">
    <div class="set-right">
        <table class="set-right">
            <tr>

                <td class="term"><spring:message code="tarjonta.definitions.edellisenävuonnahakhyv"/></td>
                <td class="description">${searchResult['AOLastYearTotalApplicants']}/${searchResult['tmpAOLastYearQualified']}</td>
            </tr>
            <tr>
                <td class="term"><spring:message code="tarjonta.definitions.maksimipistemäärä"/></td>
                <td class="description">${searchResult['AOLastYearMaxScore']}</td>
            </tr>
            <tr>
                <td class="term"><spring:message code="tarjonta.definitions.alinhyväksyttypistemäärä"/>&nbsp; 2011</td>
                <td class="description">${searchResult['AOLastYearMinScore']}</td>
            </tr>
            <%--
            <tr>
                <td class="term"><spring:message code="tarjonta.definitions.alinhyväksyttypistemäärä"/> ????</td>
                <td class="description">???</td>
            </tr>
            <tr>
                <td class="term"><spring:message code="tarjonta.definitions.alinhyväksyttypistemäärä"/> ????</td>
                <td class="description">???</td>
            </tr>
            --%>
        </table>
    </div>
</div>
