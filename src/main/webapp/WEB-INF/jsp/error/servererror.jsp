<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE HTML>
<html lang="fi">
    <head>
        <meta charset="utf-8" />
        <link rel="stylesheet" href="/haku/resources/css/styles.css" type="text/css">
        <title>Virhe</title>
    </head>
    <body>
        Palvelu ei pystynyt käsittelemään pyyntöäsi!
        <fieldset>
            <legend><c:out value="${message}"/></legend>
            <pre><c:out value="${stackTrace}"/></pre>
        </fieldset>
    </body>
</html>
