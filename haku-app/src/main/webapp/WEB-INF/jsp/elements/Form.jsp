<%@ page session="false"%>
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

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
    <meta charset="utf-8"/>
    <haku:icons contextPath="${contextPath}"/>
    <link rel="stylesheet" href="${contextPath}/resources/css/oppija.css" type="text/css">
    <!--[if lt IE 9]>
    <link href="${contextPath}/resources/css/ie.css" type="text/css" rel="stylesheet"/>
    <![endif]-->
    <link rel="stylesheet" href="${contextPath}/resources/jquery-ui-theme/jquery-ui-1.8.23.custom.css" type="text/css">
    <script src="${contextPath}/resources/jquery/jquery.min.js"></script>
    <script src="${contextPath}/resources/jquery/jquery-ui-1.8.23.custom.min.js"></script>
    <script src="${contextPath}/resources/jquery/jquery.ui.datepicker-fi.js"></script>
    <script src="${contextPath}/resources/jquery/jquery.html5-placeholder-shim.js" type="text/javascript"></script>
    <script src="${contextPath}/resources/jquery/xdr.js"></script>
    <script src="${contextPath}/resources/javascript/rules.js"></script>
    <script src="${contextPath}/resources/javascript/master.js"></script>
    <script>
        function pastPhase(phaseId) {
            $('#form-${vaihe.id}').append('<input type="hidden" name="phaseId" value="' + phaseId + '" />');
            $('#form-${vaihe.id}').submit();
        }
        $(document).ready(function () {
            $("form input:text").each(function () {
                $(this).keypress(function (event) {
                    if (event.keyCode == 13) {
                        event.preventDefault();
                        return false;
                    }
                    return true;
                })
            });
        });
    </script>
    <title><fmt:message key="lomake.opintopolku"/> - <fmt:message key="form.title"/> - <fmt:message key="lomake.esikatselu"/></title>
</head>
<body>
<div id="viewport">
    <%@include file="common/confirmDialog.jsp" %>
    <div id="site">
        <div id="sitecontent">
            <div class="content">
                <a href="https://opintopolku.fi"><fmt:message key="lomake.lisaakoulutuksia"/></a>
                <h1><haku:i18nText value="${element.i18nText}"/> - <fmt:message key="form.title"/></h1>
                <ul class="form-steps">
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
                    <li>
                        <a class="current"><span class="index"><c:out
                                value="${fn:length(element.children) + 1}"/></span><fmt:message
                                key="lomake.esikatselu"/></a>
                    </li>
                    <li>
                        <span>
                            <span class="index"><c:out value="${fn:length(element.children) + 2}"/></span>
                            <fmt:message key="lomake.valmis"/>
                        </span>
                    </li>
                </ul>
                <div class="clear"></div>
            </div>

            <div class="form">
                <jsp:include page="../prev_next_buttons_preview.jsp"/>
                <div class="phase-help">
                    <div class="help-text"><fmt:message key="form.esikatselu.help" /></div>
                </div>
                <div class="clear"></div>
                <c:forEach var="child" items="${element.children}">
                    <c:set var="element" value="${child}" scope="request"/>
                    <jsp:include page="./${child.type}Preview.jsp"/>
                </c:forEach>
                <jsp:include page="../prev_next_buttons_preview.jsp"/>
            </div>
        </div>
    </div>
</div>
<!-- Piwik -->
<script src="${contextPath}/resources/piwik/piwik.js" type="text/javascript"></script>
 <!-- End Piwik Code -->
</body>
</html>

