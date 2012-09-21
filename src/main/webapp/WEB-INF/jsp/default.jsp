<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
    <meta charset="utf-8"/>
    <link rel="stylesheet" href="/haku/resources/css/test.css" type="text/css">
    <title>${form.title} - ${category.title}</title>
</head>
<body>

    <div class="header">
        <h3>Hakulomake</h3>
        <h4>Ammatillisen koulutuksen ja lukiokoulutuksen yhteishaku, syksy 2012</h4>
        <div class="application-navigation">
            <ol class="rounded-list">
                <c:forEach var="link" items="${form.navigation.children}" varStatus="status">
                    <li class="item">
                        <a id="nav-${link.id}" ${link.attributeString}>${link.value}</a>&nbsp;<c:if test="${not status.last}">></c:if>
                    </li>
                </c:forEach>
            </ol>
        </div>
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

</body>
</html>

