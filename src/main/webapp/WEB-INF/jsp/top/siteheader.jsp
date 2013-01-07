<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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

<header id="siteheader">
    <div class="header-container">
        <div class="sitelogo">
            <a href="${pageContext.request.contextPath}">Sivuston logo</a>
        </div>

        <div class="actions">
            <c:choose>
                <c:when test="${not empty sessionScope['username']}">
                    <ul>
                        <li><a href="${pageContext.request.contextPath}/logout">Kirjaudu ulos</a></li>
                        <li><a href="${pageContext.request.contextPath}/me"><c:out value="${sessionScope['username']}"/>
                            <c:out value="${lastname}"/></a></li>
                    </ul>
                </c:when>
                <c:otherwise>
                    <ul>
                        <li><a href="#" data-popup-action="open">Kirjaudu sisään</a></li>
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
                <li><a href="#"><spring:message code="oppija.header.label.mobile"/></a></li>
                <li><a href="#"><spring:message code="oppija.header.label.textversion"/></a></li>
            </ul>

            <div class="clear"></div>
        </div>
    </div>
    <div class="line clear"></div>

</header>



