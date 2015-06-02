<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="f" uri="/WEB-INF/tld/functions.tld" %>

<fmt:setBundle basename="messages" scope="application"/>

<c:set var="form" value="${it.form}" scope="request"/>
<c:set var="application" value="${it.application}" scope="request"/>
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

<haku:messages messages="${errorMessages}" additionalClass="warning" form="${it.form}"/>
<div class="form">
    <c:forEach var="child" items="${form.children}">
        <c:set var="element" value="${child}" scope="request"/>
        <jsp:include page="../elements/${child.type}Preview.jsp"/>
    </c:forEach>
</div>
