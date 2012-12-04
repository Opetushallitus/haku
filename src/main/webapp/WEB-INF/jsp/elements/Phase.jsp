<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
<c:set var="vaihe" value="${element}" scope="request"/>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
    <meta charset="utf-8"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/screen.css" type="text/css">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/resources/jquery-ui-theme/jquery-ui-1.8.23.custom.css"
          type="text/css">
    <title>${form.title} - ${vaihe.title}</title>
    <script src="${pageContext.request.contextPath}/resources/jquery/jquery.min.js"></script>
    <script src="${pageContext.request.contextPath}/resources/jquery/jquery-ui-1.8.23.custom.min.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/rules.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/master.js"></script>
</head>
<body>
<div id="viewport">
    <div id="overlay">
    </div>
    <div id="site">
        <div id="sitecontent">
            <div class="content">
                <h1>Hakulomake</h1>

                <h2>Ammatillisen koulutuksen ja lukiokoulutuksen yhteishaku, syksy 2012</h2>
                <ul class="form-steps">
                    <c:forEach var="link" items="${form.navigation.children}" varStatus="status">
                        <li><a id="nav-${link.id}" ${link.attributeString}
                               <c:if test="${link.id eq vaihe.id}">class="current"</c:if>>
                            <span class="index">${status.count}</span>${link.value} <c:if
                                test="${not status.last}">&gt;</c:if></a></li>
                    </c:forEach>
                    <li><span><span class="index"><c:out value="${fn:length(form.navigation.children) + 1}"/></span>Valmis</span>
                    </li>
                </ul>
                <div class="clear"></div>
            </div>

            <c:set var="preview" value="${vaihe.preview}" scope="request"/>
            <c:choose>
                <c:when test="${preview}">
                    <div class="form">
                        <jsp:include page="../prev_next_buttons_preview.jsp"/>
                        <c:forEach var="child" items="${vaihe.children}">
                            <c:set var="element" value="${child}" scope="request"/>
                            <c:set var="parentId" value="${form.id}.${vaihe.id}" scope="request"/>
                            <jsp:include page="./${child.type}Preview.jsp"/>
                        </c:forEach>
                        <jsp:include page="../prev_next_buttons_preview.jsp"/>
                    </div>
                </c:when>
                <c:otherwise>
                    <form id="form-${vaihe.id}" class="form" method="post">
                        <jsp:include page="../prev_next_buttons.jsp"/>
                        <c:forEach var="child" items="${vaihe.children}">
                            <c:set var="element" value="${child}" scope="request"/>
                            <c:set var="parentId" value="${form.id}.${vaihe.id}" scope="request"/>
                            <jsp:include page="./${child.type}.jsp"/>
                        </c:forEach>
                        <jsp:include page="../prev_next_buttons.jsp"/>
                    </form>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>
</body>
</html>

