<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
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
    <legend class="h3"><fmt:message key="virkailija.lisakysymys.otsikko"/></legend>
    <hr/>
    <form method="get" action="${pageContext.request.contextPath}/virkailija/hakemus/${oid}/additionalInfo">
        <button class="float-right edit-link" type="submit">
            <span>
                <span><fmt:message key="virkailija.lisakysymys.muokkaa"/></span>
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
                                    <fmt:message key="virkailija.lisakysymys.kylla"/>
                                </c:if>
                                <c:if test="${additionalInfo[question.key] eq false}">
                                    <fmt:message key="virkailija.lisakysymys.ei"/>
                                </c:if>
                            </c:when>
                            <c:otherwise>
                                <c:out value="${additionalInfo[question.key]}" escapeXml="true"/>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </c:forEach>
            <c:forEach var="data" items="${additionalInfo}">
                <c:if test="${additionalQuestions.questionMap[data.key] eq null}">
                    <tr>
                        <td class="label"><c:out value='${data.key}' escapeXml="true"/></td>
                        <td><c:out value='${data.value}' escapeXml="true"/></td>
                    </tr>
                </c:if>
            </c:forEach>
            </tbody>
        </table>
    </form>
</fieldset>
