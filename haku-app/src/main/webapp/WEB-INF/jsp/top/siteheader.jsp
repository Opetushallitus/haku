<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
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
<fmt:setBundle basename="messages" scope="session"/>
<header id="siteheader">
    <div class="header-container">
        <div class="actions">
            <sec:authorize var="loggedIn" access="isAuthenticated()"/>
            <c:choose>
                <c:when test="${loggedIn}">
                    <ul>
                        <li><a href="${contextPath}/user/logout">Kirjaudu ulos</a></li>
                    </ul>
                </c:when>
                <c:otherwise>
                    <ul>
                        <li><a href="#" data-popup-action="open">Kirjaudu sisään</a></li>
                        <li><a href="lomake/">Lomakkeet</a></li>
                    </ul>
                </c:otherwise>
            </c:choose>
            <ul>
                <sec:authorize access="hasRole('ROLE_APP_HAKEMUS_READ_UPDATE')">
                    <li><a href="${contextPath}/admin">Admin</a></li>
                </sec:authorize>
                <sec:authorize access="hasRole('ROLE_APP_HAKEMUS_CRUD')">
                    <li><a href="${contextPath}/virkailija/hakemus">Hakemusten käsittely</a></li>
                </sec:authorize>
            </ul>
            <div class="clear"></div>
        </div>
    </div>
    <div class="line clear"></div>

</header>



