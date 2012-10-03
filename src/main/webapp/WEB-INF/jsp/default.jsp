<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
    <meta charset="utf-8"/>
    <link rel="stylesheet" href="/haku/resources/css/screen.css" type="text/css">
    <title>${form.title} - ${category.title}</title>
    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.8.1/jquery.min.js"></script>
    <script src="/haku/resources/javascript/rules.js"></script>
</head>
<body>
<div id="sitecontent">

    <div class="content">
        <h3>Hakulomake</h3>
        <h4>Ammatillisen koulutuksen ja lukiokoulutuksen yhteishaku, syksy 2012</h4>
        <ul class="form-steps">
            <c:forEach var="link" items="${form.navigation.children}" varStatus="status">
                <li><a id="nav-${link.id}" ${link.attributeString}
                    <c:if test="${link.id eq category.id}">class="current"</c:if>>
                    <span class="index">${status.count}</span>${link.value} <c:if test="${not status.last}">&gt;</c:if></a></li>
            </c:forEach>
        </ul>
        <div class="clear"></div>
    </div>

    <div class="container">
        <div class="application-content">
                <form id="form-${category.id}" method="post">

                    <jsp:include page="prev_next_buttons.jsp"/>

                    <c:forEach var="child" items="${category.children}">
                        <c:set var="element" value="${child}" scope="request"/>
                        <c:set var="parentId" value="${form.id}.${category.id}" scope="request"/>
                        <jsp:include page="elements/${child.type}.jsp"/>
                    </c:forEach>

                    <jsp:include page="prev_next_buttons.jsp"/>

                </form>
        </div>
    </div>
<div id="sitecontent">
</body>
</html>

