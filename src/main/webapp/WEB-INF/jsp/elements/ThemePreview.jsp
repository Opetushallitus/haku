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

<%-- set education specific additional questions for this theme --%>
<c:set var="additionalQuestionList" value="${additionalQuestions[element.id]}" scope="request"/>

<fieldset>
    <legend class="h3"><c:out value="${element.title}"/></legend>
    <hr>
    <c:forEach var="vaihe" items="${form.categories}">
        <c:if test="${(not vaihe.preview)}">
            <c:forEach var="teema" items="${vaihe.children}">
                <c:if test="${(teema.id eq element.id)}">
                    <form method="get"
                          action="${pageContext.request.contextPath}/lomake/${hakemusId.applicationPeriodId}/${hakemusId.formId}/${vaihe.id}">
                        <button class="set-right legend-align" type="submit">
                            <span>
                                <span>Muokkaa</span>
                            </span>
                        </button>
                    </form>
                </c:if>
            </c:forEach>
        </c:if>
    </c:forEach>
    <table class="form-summary-table">
        <tbody>
            <haku:viewChilds element="${element}"/>
        </tbody>
    </table>
</fieldset>
