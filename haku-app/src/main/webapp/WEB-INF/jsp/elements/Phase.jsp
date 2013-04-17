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
<fmt:setBundle basename="messages" scope="session"/>
<c:set var="vaihe" value="${element}" scope="request"/>
<c:set var="errorMessages" value="${it.errorMessages}" scope="request"/>
<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="request"/>
<c:set var="preview" value="${vaihe.preview}" scope="request"/>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
    <meta charset="utf-8"/>
    <link rel="stylesheet" href="${contextPath}/resources/css/oppija.css" type="text/css">
    <link rel="stylesheet" href="${contextPath}/resources/jquery-ui-theme/jquery-ui-1.8.23.custom.css" type="text/css">
    <script src="${contextPath}/resources/jquery/jquery.min.js"></script>
    <script src="${contextPath}/resources/jquery/jquery-ui-1.8.23.custom.min.js"></script>
    <script src="${contextPath}/resources/jquery/jquery.ui.datepicker-fi.js"></script>
    <script src="${contextPath}/resources/jquery/jQuery.XDomainRequest.js"></script>
    <script src="${contextPath}/resources/javascript/rules.js"></script>
    <script src="${contextPath}/resources/javascript/master.js"></script>
    <script>
    	function pastPhase(phaseId) {
    		$('#form-${vaihe.id}').append('<input type="hidden" name="phaseId" value="'+phaseId+'-skip-validators" />');
    		$('#form-${vaihe.id}').submit();
    	}
    	$(document).ready(function() {
    		$("form input:text").each(function() {
    			$(this).keypress(function(event) {
    				if(event.keyCode == 13) {
    					event.preventDefault();
    					return false;
    				}
    				return true;
    			})
    		});
    	});
    </script>
    <title><haku:i18nText value="${form.i18nText}"/> - <haku:i18nText value="${vaihe.i18nText}"/></title>
</head>
<body>
<div id="viewport">
    <div id="overlay">
        <c:choose>
            <c:when test="${preview}">

                <div class="popover-wrapper" id="areyousure" style="z-index:1000;display:none;">
                    <span class="popover-close">&#8203;</span>

                    <div class="popover">
                        <span class="popover-close">&#8203;</span>

                        <div class="popover-header">
                            <fmt:message key="lomake.send.confirm.title"/>
                        </div>
                        <div class="popover-content">
                            <form method="post">
                                <p><fmt:message key="lomake.send.confirm.message"/></p>
                                <button name="nav-send" value="true" data-po-hide="areyousure">
									<span>
										<span><fmt:message key="lomake.send.confirm.no"/></span>
									</span>
                                </button>
                                <button id="submit_confirm" class="primary set-right" name="nav-send" type="submit"
                                        value="true">
									<span>
										<span><fmt:message key="lomake.send.confirm.yes"/></span>
									</span>
                                </button>
                                <div class="clear"></div>
                            </form>
                        </div>
                    </div>
                </div>
            </c:when>
        </c:choose>

    </div>
    <div id="site">
        <div id="sitecontent">
            <div class="content">
                <h1><fmt:message key="form.title"/></h1>

                <h2><haku:i18nText value="${form.i18nText}"/></h2>
                <ul class="form-steps">
                    <c:set var="pastPhases" value="true" scope="request"/>
                    <c:forEach var="phase" items="${form.children}" varStatus="status">
                        <li>
                        	<c:if test="${pastPhases}">
	                            <a id="nav-${phase.id}" href="javascript:pastPhase('${phase.id}')"
	                               <c:if test="${phase.id eq vaihe.id}">class="current"</c:if>>
	                                <span class="index">${status.count}</span><haku:i18nText value="${phase.i18nText}"/>&nbsp;&gt;
	                            </a>
                            </c:if>
                            <c:if test="${!pastPhases}">
                            	<span>
                            	<span class="index">${status.count}</span><haku:i18nText value="${phase.i18nText}"/>&nbsp;&gt;
                            	</span>
                                <%--a id="nav-nav-${phase.id}" href="${phase.id}">
                                <span class="index">${status.count}</span><haku:i18nText value="${phase.i18nText}"/>&nbsp;&gt;
                                </a --%>
                            </c:if>
                            <c:if test="${(pastPhases && phase.id eq vaihe.id)}">
                                <c:set var="pastPhases" value="false"/>
                            </c:if>
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

