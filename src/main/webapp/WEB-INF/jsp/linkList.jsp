<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE HTML>
<html lang="fi">
    <head>
        <META http-equiv="Content-Type" content="text/html;charset=UTF-8">
        <meta charset="utf-8" />
        <link rel="stylesheet" href="/haku/resources/css/styles.css" type="text/css">
        <title></title>
    </head>
    <body>
        <ul>
            <c:forEach var="item" items="${linkList}">
                <li><a id="${item}" href="${path}${item}">${item}</a></li>
            </c:forEach>
        </ul>
    </body>
</html>
