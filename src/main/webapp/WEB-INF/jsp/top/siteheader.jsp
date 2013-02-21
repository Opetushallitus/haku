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
        <div class="sitelogo">
            <a href="${pageContext.request.contextPath}">Sivuston logo</a>
            <a href="${contextPath}/test/addPerson">/test/addPerson</a>
        </div>

        <div class="actions">

            <sec:authorize var="loggedIn" access="isAuthenticated()"/>
            <c:choose>
                <c:when test="${loggedIn}">
                    <ul>
                        <li><a href="${contextPath}/user/logout">Kirjaudu ulos</a></li>
                        <li><a href="${contextPath}/j_spring_cas_security_logout">CAS Logout</a></li>
                        <li><a href="${contextPath}/me"><sec:authentication property="principal.username"/></a></li>
                    </ul>
                </c:when>
                <c:otherwise>
                    <ul>
                        <li><a href="#" data-popup-action="open">Kirjaudu sisään</a></li>
                        <li><a href="${contextPath}/user/postLogin?redirect=${pageContext.request.requestURL}">CAS Login</a></li>
                    </ul>
                </c:otherwise>
            </c:choose>
            <ul>
                <li><a href="http://www.sanasto.fi/">Sanasto</a></li>
                <li><a href="http://google.fi">Kysy neuvoa</a></li>
                <li><a href="http://www.koulutusnetti.fi/?path=hakuajat">Hakuajat</a></li>
            </ul>

            <ul>
                <li><a href="?lang=fi">Suomeksi</a></li>
                <li><a href="?lang=sv">På svenska</a></li>
                <li><a href="?lang=en">in English</a></li>
                <li><a href="#"><fmt:message key="oppija.header.label.mobile"/></a></li>
                <li><a href="#"><fmt:message key="oppija.header.label.textversion"/></a></li>
            </ul>

            <div class="clear"></div>
        </div>
    </div>
    <div class="line clear"></div>

</header>



