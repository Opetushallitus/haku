<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
    <meta charset="utf-8"/>
    <link rel="stylesheet" href="/haku/resources/css/screen.css" type="text/css">
    <title>${form.title} - ${category.title}</title>
    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.8.23/jquery-ui.min.js"></script>
    <script src="/haku/resources/javascript/rules.js"></script>
    <script src="/haku/resources/javascript/master.js"></script>
</head>
<body>
    <div id="viewport">


        <div id="overlay">
        </div>

        <div id="site">

        <div id="sitecontent">

        <div class="content">
            <h1>Hakulomake</h1>
            <h2>Ammatillisen koulutuksen ja lukiokoulutuksen yhteishaku, syksy 2012</h2>
            <ul class="form-steps">
                <c:forEach var="link" items="${form.navigation.children}" varStatus="status">
                    <li><a id="nav-${link.id}" ${link.attributeString}
                        <c:if test="${link.id eq category.id}">class="current"</c:if>>
                        <span class="index">${status.count}</span>${link.value} <c:if test="${not status.last}">&gt;</c:if></a></li>
                </c:forEach>
            </ul>
            <div class="clear"></div>
        </div>

    <form id="form-${category.id}" class="form" method="post">

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
</div>
</body>
</html>

