<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="f" uri="/WEB-INF/tld/functions.tld" %>

<fmt:setBundle basename="messages" scope="application"/>

<c:set var="preview" value="${it.preview}" scope="request"/>
<c:set var="form" value="${it.form}" scope="request"/>
<c:set var="application" value="${it.application}" scope="request"/>
<c:set var="oid" value="${it.oid}" scope="request"/>
<c:set var="answers" value="${it.application.vastauksetMerged}" scope="request"/>
<c:set var="phase" value="preview" scope="request"/>
<c:set var="print" value="false" scope="request"/>
<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="request"/>
<c:set var="errorMessages" value="${it.errorMessages}" scope="request"/>

<%--
<c:forEach var="phase" items="${form.children}">
    <c:set var="element" value="${phase}" scope="request"/>
    <haku:viewChilds element="${element}"/>
</c:forEach>
--%>

<c:set var="virkailijaPreview" value="true" scope="request"/>

<html>
<head>
    <haku:meta/>
    <haku:icons/>
    <link rel="stylesheet" href="${contextPath}/resources/jquery-ui-theme/jquery-ui-1.8.23.custom.css" type="text/css">
    <link href="${contextPath}/resources/css/oppija.css" type="text/css" rel="stylesheet">
    <link href="${contextPath}/resources/css/virkailija.css" type="text/css" rel="stylesheet">
    <link href="${contextPath}/resources/css/hakemus.css" type="text/css" rel="stylesheet"/>
    <script src="${contextPath}/resources/jquery/jquery.min.js" type="text/javascript"></script>
    <script src="${contextPath}/resources/jquery/jquery-ui-1.8.23.custom.min.js" type="text/javascript"></script>
    <script src="${contextPath}/resources/jquery/jquery.ui.datepicker-trans.js" type="text/javascript"></script>
    <script src="${contextPath}/resources/javascript/underscore-min.js" type="text/javascript"></script>
    <script src="${contextPath}/resources/javascript/bacon.min.js" type="text/javascript"></script>
    <script src="${contextPath}/resources/jquery/xdr.js"></script>
    <script src="${contextPath}/resources/javascript/rules.js" type="text/javascript"></script>
    <script src="${contextPath}/resources/javascript/master.js" type="text/javascript"></script>
    <script src="${contextPath}/resources/jquery/jquery.cookie.js" type="text/javascript"></script>
    <script src="${contextPath}/resources/jquery/jquery.hotkeys.js" type="text/javascript"></script>
    <script src="${contextPath}/resources/javascript/virkailija/application.js" type="text/javascript"></script>
    <script src="${contextPath}/resources/javascript/virkailija/tabs.js" type="text/javascript"></script>
    <script type="text/javascript" src="/virkailija-raamit/apply-raamit.js"></script>
    <title><fmt:message key="virkailija.otsikko"/></title>

    <haku:ie9StyleFix/>

</head>
<body>
<script type="text/javascript">
    var page_settings = {
        contextPath: "${pageContext.request.contextPath}",
        applicationOid: "${oid}",
        lang: "${requestScope['fi_vm_sade_oppija_language']}",
        preview: "${preview}"
    }
</script>
<haku:messages messages="${errorMessages}" additionalClass="warning" form="${it.form}"/>
<div class="form">
    <c:forEach var="child" items="${form.children}">
        <c:set var="element" value="${child}" scope="request"/>
        <jsp:include page="../elements/${child.type}Preview.jsp"/>
    </c:forEach>
</div>
</body>
</html>
