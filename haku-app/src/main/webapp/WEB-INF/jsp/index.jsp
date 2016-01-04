<%@ page session="false"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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

<!DOCTYPE html>
<html>
<fmt:setLocale value="es_ES"/>
<fmt:setBundle basename="messages" scope="application"/>
<jsp:include page="top/head.jsp"/>
<body>
<div>
    <div>
        <sec:authorize var="loggedIn" access="isAuthenticated()"/>
        <c:choose>
            <c:when test="${loggedIn}">
                <ul>
                    <li><a href="${contextPath}/user/logout">Kirjaudu ulos</a></li>
                </ul>
            </c:when>
            <c:otherwise>
                <ul>
                    <li><a href="${contextPath}/user/login">Kirjaudu sisään</a></li>
                </ul>
            </c:otherwise>
        </c:choose>
        <div>
            <ul>
                <li><a href="lomake/">Lomakkeet</a></li>
                <sec:authorize access="hasRole('ROLE_APP_HAKEMUS_READ_UPDATE')">
                    <li><a href="${contextPath}/virkailija/hakemus">Hakemusten käsittely</a></li>
                </sec:authorize>
                <sec:authorize access="hasRole('ROLE_APP_HAKEMUS_CRUD')">
                    <li><a href="${contextPath}/lomakkeenhallinta/ALL">Admin - Generoi lomakkeet</a></li>
                    <li><a href="${contextPath}/virkailija/hakemus">Hakemusten käsittely</a></li>
                    <li><a href="/">Oppijan verkkopalvelu</a></li>
                </sec:authorize>
            </ul>
        </div>
    </div>
</div>
</body>
</html>
