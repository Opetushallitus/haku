<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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

<!DOCTYPE html>
<fmt:setBundle basename="messages"/>
<c:set var="vaihe" value="${element}" scope="request"/>
<c:set var="errorMessages" value="${it.errorMessages}" scope="request"/>
<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="request"/>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
    <meta charset="utf-8"/>
    <link rel="stylesheet" href="${contextPath}/resources/css/oppija.css" type="text/css">
    <link rel="stylesheet" href="${contextPath}/resources/jquery-ui-theme/jquery-ui-1.8.23.custom.css" type="text/css">
    <script src="${contextPath}/resources/jquery/jquery.min.js"></script>
    <script src="${contextPath}/resources/jquery/jquery-ui-1.8.23.custom.min.js"></script>
    <script src="${contextPath}/resources/javascript/rules.js"></script>
    <script src="${contextPath}/resources/javascript/master.js"></script>
    <title><haku:i18nText value="${form.i18nText}"/> - <haku:i18nText value="${vaihe.i18nText}"/></title>
</head>
<body>
<div id="viewport">
    <div id="overlay">
	
			<div class="popover-wrapper" id="<!-- Id -->" style="z-index:1000;">
				<span class="popover-close">&#8203;</span>
				<div class="popover">
					<span class="popover-close">&#8203;</span>
					<div class="popover-header">
						<!-- Popover title -->
					</div>
					<div class="popover-content">
						<!-- Popover content -->
					</div>
				</div>
			</div>
	
    </div>
    <div id="site">
        <div id="sitecontent">
            <div class="content">
                <h1><fmt:message key="form.title"/></h1>

                <h2><haku:i18nText value="${form.i18nText}"/></h2>
                <ul class="form-steps">
                    <c:forEach var="phase" items="${form.children}" varStatus="status">
                        <li>
                            <a id="nav-${phase.id}" href="${phase.id}"
                               <c:if test="${phase.id eq vaihe.id}">class="current"</c:if>>
                                <span class="index">${status.count}</span><haku:i18nText value="${phase.i18nText}"/>&nbsp;&gt;
                            </a>
                        </li>
                    </c:forEach>
                    <li>
                        <span>
                            <span class="index"><c:out value="${fn:length(form.children) + 1}"/></span>
                            <fmt:message key="lomake.valmis"/>
                        </span>
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

