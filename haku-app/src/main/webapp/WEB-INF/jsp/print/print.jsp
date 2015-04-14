<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="f" uri="/WEB-INF/tld/functions.tld" %>
<!DOCTYPE html>

<fmt:setBundle basename="messages" scope="application"/>

<c:set var="form" value="${it.form}" scope="request"/>
<c:set var="application" value="${it.application}" scope="request"/>
<c:set var="answers" value="${it.application.vastauksetMerged}" scope="request"/>
<c:set var="phase" value="${it.phase}" scope="request"/>
<c:set var="print" value="true" scope="request"/>
<c:set var="server" value="${fn:replace(pageContext.request.requestURL, pageContext.request.requestURI, pageContext.request.contextPath)}" scope="request"/>
<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="request"/>

<c:set var="applicationAttachments" value="${it.applicationAttachments}" scope="request"/>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
    <meta charset="utf-8"/>
    <haku:icons/>
    <link href="${server}/resources/css/hakemus-print.css" type="text/css" rel="stylesheet"/>
    <script src="${contextPath}/resources/jquery/jquery.min.js" type="text/javascript"></script>
    <script src="${contextPath}/resources/jquery/xdr.js"></script>
    <script src="${contextPath}/resources/javascript/printfix.js"></script>
    <title><fmt:message key="lomake.opintopolku"/> - <fmt:message key="form.title"/> - <fmt:message
            key="lomake.tulostus"/></title>
</head>
<body>
<header>
    <h1><haku:i18nText value="${form.i18nText}"/></h1>

    <h2><c:out value="${answers['Etunimet']}" escapeXml="true"/>&nbsp;<c:out value="${answers['Sukunimi']}"
                                                                             escapeXml="true"/></h2>

    <p><fmt:message key="lomake.tulostus.vastaanotettu"/>&nbsp;
        <time><fmt:formatDate value="${application.received}" pattern="dd.MM.yyyy HH:mm"/></time>
    </p>
    <div class="application-number"><fmt:message key="virkailija.hakemus.hakemusnro"/>&nbsp;<c:out
            value="${f:formatOid(application.oid)}" escapeXml="true"/></div>
</header>
<c:forEach var="phase" items="${form.children}">
    <c:set var="element" value="${phase}" scope="request"/>
    <haku:viewChilds element="${element}"/>
</c:forEach>

<c:forEach var="additionalInformationElement" items="${it.additionalInformationElements}">
    <hr role="presentation">
    <c:set var="element" value="${additionalInformationElement}" scope="request"/>
    <haku:viewChilds element="${element}"/>
</c:forEach>

<jsp:include page="../print/applicationAttachments.jsp"/>

<hr role="presentation" class="strong">
<footer>
    <address>
        <c:forEach var="alatunnisterivi" items="${it.alatunnisterivit}" varStatus="status">
            <haku:i18nText value="${alatunnisterivi}" />
            <c:if test="${(not status.last)}"><br/></c:if>
        </c:forEach>
    </address>
</footer>
</body>
</html>
