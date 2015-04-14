<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="f" uri="/WEB-INF/tld/functions.tld" %>
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
<fmt:setBundle basename="messages" scope="application"/>
<c:set var="complete" value="true" scope="request"/>
<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="page"/>
<c:set var="answers" value="${it.application.vastauksetMerged}" scope="request"/>
<c:set var="form" value="${it.form}" scope="request"/>
<c:set var="application" value="${it.application}" scope="request"/>
<c:set var="discretionaryAttachmentAOIds" value="${it.discretionaryAttachmentAOIds}" scope="request"/>
<c:set var="higherEducationAttachments" value="${it.higherEducationAttachments}" scope="request"/>
<c:set var="applicationAttachments" value="${it.applicationAttachments}" scope="request"/>
<c:set var="completeElements" value="${it.applicationCompleteElements}" scope="request"/>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
    <meta charset="utf-8"/>
    <script src="${contextPath}/resources/jquery/jquery.min.js" type="text/javascript"></script>
    <script src="${contextPath}/resources/jquery/xdr.js"></script>
    <link rel="stylesheet" href="${contextPath}/resources/css/oppija.css" type="text/css">
    <!--[if lt IE 9]>
    <link href="${contextPath}/resources/css/ie.css" type="text/css" rel="stylesheet"/>
    <![endif]-->
    <link rel="stylesheet" href="${contextPath}/resources/jquery-ui-theme/jquery-ui-1.8.23.custom.css" type="text/css">
    <title><fmt:message key="lomake.opintopolku"/> - <fmt:message key="form.title"/> - <fmt:message
            key="lomake.valmis"/></title>
    <haku:icons/>
</head>
<body>
<div id="viewport">
    <div id="overlay"></div>
    <div id="site">

        <header id="siteheader">

        </header>

        <section id="page">

            <div role="presentation" class="clear"></div>

            <section class="content-container">


                <div class="grid16-16">

                    <h1><haku:i18nText value="${form.i18nText}"/> - <fmt:message key="form.title"/></h1>

                    <ul class="form-steps">
                        <c:forEach var="phase" items="${form.children}" varStatus="status">
                            <li><span><span class="index">${status.count}</span><haku:i18nText
                                    value="${phase.i18nText}"/> &gt;</span></li>
                        </c:forEach>
                        <li>
                            <span><span class="index">${fn:length(form.children) + 1}</span><fmt:message
                                    key="lomake.esikatselu"/> &gt;</span>
                        </li>
                        <li>
                            <a class="current"><span class="index"><c:out
                                    value="${fn:length(form.children) + 2}"/></span><fmt:message
                                    key="lomake.valmis"/></a>
                        </li>
                    </ul>
                    <div role="presentation" class="clear"></div>
                </div>
                <div role="presentation" class="clear"></div>

                <div class="form" data-form-step-id="7">

                    <h3 class="h2"><fmt:message key="lomake.valmis.hakemuksesionvastaanotettu"/></h3>

                    <p class="application-number">
                        <fmt:message key="lomake.valmis.hakulomakenumerosi"/>: <span class="number"><c:out
                            value="${ f:formatOid(application.oid)}"/></span>
                    </p>

                    <c:forEach var="cElement" items="${completeElements}">
                        <c:set var="element" value="${cElement}" scope="request"/>
                        <jsp:include page="/WEB-INF/jsp/valmis/${element.type}.jsp"/>
                    </c:forEach>

                </div>
            </section>
        </section>
    </div>
</div>
<!-- Piwik -->
<script src="${contextPath}/resources/piwik/piwik.js" type="text/javascript"></script>
<!-- End Piwik Code -->
</body>
</html>
