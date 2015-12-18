<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
<html>
<head>
    <title><haku:i18nText value="${it.theme.i18nText}"/></title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <link href="${pageContext.request.contextPath}/resources/css/oppija.css" type="text/css" rel="stylesheet"/>
    <haku:icons/>
</head>
<body>
<div id="viewport">
    <div id="overlay"></div>
    <div id="help-page">
        <section id="page">
            <h1><haku:i18nText value="${it.theme.i18nText}"/></h1>
            <c:forEach var="entry" items="${it.listsOfTitledElements}">
                <c:if test="${not empty entry.verboseHelp.translations[requestScope['fi_vm_sade_oppija_language']]}">
                    <h3><haku:i18nText value="${entry.i18nText}"/></h3>

                    <p><haku:i18nText value="${entry.verboseHelp}" escape="false"/></p>
                </c:if>
            </c:forEach>
        </section>
    </div>
</div>
<!-- Piwik -->
<script src="${pageContext.request.contextPath}/resources/piwik/piwik.js" type="text/javascript"></script>
<!-- End Piwik Code -->
</body>
</html>
