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
<fmt:setBundle basename="messages" scope="application"/>
<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="request"/>
<c:set var="preview" value="true" scope="request"/>
<c:set var="phaseCount" value="${fn:length(element.children)}" scope="request"/>
<c:set var="form" value="${element}" scope="request"/>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
    <meta charset="utf-8"/>
    <haku:icons/>
    <link rel="stylesheet" href="${contextPath}/resources/css/oppija.css" type="text/css">
    <!--[if lt IE 9]>
    <link href="${contextPath}/resources/css/ie.css" type="text/css" rel="stylesheet"/>
    <![endif]-->
    <link rel="stylesheet" href="${contextPath}/resources/jquery-ui-theme/jquery-ui-1.8.23.custom.css" type="text/css">
    <script src="${contextPath}/resources/jquery/jquery.min.js"></script>
    <script src="${contextPath}/resources/jquery/jquery-ui-1.8.23.custom.min.js"></script>
    <script src="${contextPath}/resources/jquery/jquery.ui.datepicker-trans.js"></script>
    <script src="${contextPath}/resources/jquery/jquery.html5-placeholder-shim.js" type="text/javascript"></script>
    <script src="${contextPath}/resources/javascript/underscore-min.js" type="text/javascript"></script>
    <script src="${contextPath}/resources/javascript/bacon.min.js" type="text/javascript"></script>
    <script src="${contextPath}/resources/jquery/xdr.js"></script>
    <script src="${contextPath}/resources/javascript/rules.js"></script>
    <script src="${contextPath}/resources/javascript/master.js"></script>
    <script src="${contextPath}/resources/javascript/oph_urls.js/index.js" type="text/javascript"></script>
    <script src="${contextPath}/resources/javascript/haku-app-web-url_properties.js" type="text/javascript"></script>

    <title><fmt:message key="lomake.opintopolku"/> - <fmt:message key="form.title"/> - <fmt:message
            key="lomake.esikatselu"/></title>
</head>
<body>
<div role="presentation" id="viewport">
    <%@include file="common/confirmDialog.jsp" %>
    <div role="presentation" id="site">
        <div role="presentation" id="sitecontent">
            <div aria-label='<fmt:message key="lomake.navigation.aria.label"/>' role="navigation" class="content">
                <a href="/"><fmt:message key="lomake.lisaakoulutuksia"/></a>

                <h1><haku:i18nText value="${element.i18nText}"/> - <fmt:message key="form.title"/>
                    <c:if test="${it.demoMode}">
                        <span class="demo-note">DEMO</span>
                    </c:if>
                </h1>
                <ul aria-label='<fmt:message key="lomake.vaiheet.aria.label"/>' class="form-steps">
                    <c:set var="pastPhases" value="true" scope="request"/>
                    <c:forEach var="phase" items="${element.children}" varStatus="status">
                        <li>
                            <c:if test="${pastPhases}">
                                <a id="nav-${phase.id}" href="${phase.id}">
                                    <span class="index">${status.count}</span><haku:i18nText value="${phase.i18nText}"/>&nbsp;&gt;
                                </a>
                            </c:if>
                        </li>
                    </c:forEach>
                    <li aria-label='<fmt:message key="lomake.esikatselu"/>'>
                        <a id="nav-esikatselu" class="current"><span class="index"><c:out value="${phaseCount + 1}"/></span><fmt:message key="lomake.esikatselu"/></a>
                    </li>
                    <li aria-label='<fmt:message key="lomake.valmis"/>'>
                        <span>
                            <span class="index"><c:out value="${phaseCount + 2}"/></span><fmt:message key="lomake.valmis"/>
                        </span>
                    </li>
                </ul>
                <div role="presentation" class="clear"></div>
            </div>

            <div role="main" class="form">
                <jsp:include page="../prev_next_buttons_preview.jsp">
                    <jsp:param name="notfocusable" value="true" />
                </jsp:include>
                <div aria-label='<fmt:message key="form.esikatselu.help.aria.label"/>' class="phase-help">
                    <div role="presentation" class="help-text">
                    <c:choose>
                        <c:when test="${it.demoMode}">
                            <fmt:message key="form.esikatselu.help.demo"/>
                        </c:when>
                        <c:otherwise>
                            <fmt:message key="form.esikatselu.help"/>
                        </c:otherwise>
                    </c:choose>
                    </div>
                </div>
                <div role="presentation" class="clear"></div>
                <c:forEach var="child" items="${element.children}">
                    <c:set var="element" value="${child}" scope="request"/>
                    <jsp:include page="./${child.type}Preview.jsp"/>
                </c:forEach>
                <jsp:include page="../prev_next_buttons_preview.jsp"/>
            </div>
        </div>
    </div>
</div>

<c:if test="${it.demoMode}">
    <jsp:include page="../demo/warning.jsp"/>
</c:if>
<jsp:include page="../session/timeout.jsp"/>

<!-- Piwik -->
<script src="${contextPath}/resources/piwik/piwik.js" type="text/javascript"></script>
<!-- End Piwik Code -->
</body>
</html>

