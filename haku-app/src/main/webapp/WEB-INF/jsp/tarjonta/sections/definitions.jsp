<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
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
<fmt:setBundle basename="messages"/>
<div class="definitions">
    <div class="set-right">
        <table class="set-right">
            <tr>

                <td class="term"><fmt:message key="tarjonta.definitions.edellisenävuonnahakhyv"/></td>
                <td class="description">${it.searchResult['AOLastYearTotalApplicants']}/${it.searchResult['tmpAOLastYearQualified']}</td>
            </tr>
            <tr>
                <td class="term"><fmt:message key="tarjonta.definitions.maksimipistemäärä"/></td>
                <td class="description">${it.searchResult['AOLastYearMaxScore']}</td>
            </tr>
            <tr>
                <td class="term"><fmt:message key="tarjonta.definitions.alinhyväksyttypistemäärä"/>&nbsp; 2011</td>
                <td class="description">${it.searchResult['AOLastYearMinScore']}</td>
            </tr>
            <%--
            <tr>
                <td class="term"><fmt:message key="tarjonta.definitions.alinhyväksyttypistemäärä"/> ????</td>
                <td class="description">???</td>
            </tr>
            <tr>
                <td class="term"><fmt:message key="tarjonta.definitions.alinhyväksyttypistemäärä"/> ????</td>
                <td class="description">???</td>
            </tr>
            --%>
        </table>
    </div>
</div>
