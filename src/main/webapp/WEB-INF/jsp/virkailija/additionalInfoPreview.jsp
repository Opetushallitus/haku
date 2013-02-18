<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>

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
<c:set var="additionalQuestions" value="${it.additionalQuestions}" scope="request"/>
<c:set var="additionalInfo" value="${it.application.additionalInfo}" scope="request"/>
<fieldset>
    <legend class="h3">Syötettävät tiedot</legend>
    <hr>
    <form method="get" action="${pageContext.request.contextPath}/virkailija/hakemus/${oid}/additionalInfo">
        <button class="float-right legend-align edit-link" type="submit">
            <span>
                <span>Muokkaa</span>
            </span>
        </button>
        <table class="form-summary-table width-50">
            <tbody>
                <c:forEach var="question" items="${additionalQuestions.allQuestions}">
                    <tr>
                        <td class="label"><c:out value='${question.key}'/></td>
                        <td>
                            <c:choose>
                                <c:when test="${question.type eq 'TOTUUSARVO'}">
                                    <c:if test="${additionalInfo[question.key] eq true}">
                                        <c:out value="Kyllä"/>
                                    </c:if>
                                    <c:if test="${additionalInfo[question.key] eq false}">
                                       <c:out value="Ei"/>
                                    </c:if>
                                </c:when>
                                <c:otherwise>
                                    <c:out value="${additionalInfo[question.key]}"/>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </form>
</fieldset>