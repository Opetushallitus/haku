<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>

    <title>Opetushallitus</title>

    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <link href="/haku/static-html/layout/css/screen.css" type="text/css" rel="stylesheet"/>
    <link href="/haku/static-html/layout/jquery/jquery-ui-theme/jquery-ui-1.8.23.custom.css" rel="stylesheet"
          type="text/css"/>
    <link href='http://fonts.googleapis.com/css?family=PT+Sans:400,700,400italic,700italic' rel='stylesheet'
          type='text/css'>
    <script src="/haku/static-html/layout/jquery/jquery-1.8.0.min.js" type="text/javascript"></script>
    <script src="/haku/static-html/layout/jquery/jquery-ui-1.8.23.custom.min.js" type="text/javascript"></script>
    <script src="/haku/static-html/layout/javascript/master.js" type="text/javascript"></script>

</head>

<body>

<div id="siteheader">

    <div class="sitelogo">
        <a href="index.html">Sivuston logo</a>
    </div>

    <div class="actions">

        <ul>
            <li><a href="#">Kirjaudu ulos</a></li>
            <li><a href="#">Mikko mallikas</a></li>
        </ul>

        <ul>
            <li><a href="#">Sanasto</a></li>
            <li><a href="#">Kysy neuvoa</a></li>
            <li><a href="#">Hakuajat</a></li>
        </ul>

        <ul>
            <li><a href="#">På svenska</a></li>
            <li><a href="#">in English</a></li>
            <li><a href="#">Mobiili</a></li>
            <li><a href="#">Tekstiversio</a></li>
        </ul>

        <div class="clear"></div>
    </div>

    <div class="line clear"></div>
</div>

<div id="sitecontent">

    <div class="navigation">
        <ul class="menu-level-1">
            <li class="home"><a href="index.html">Etusivu</a></li>
            <li><a href="#">Dolor sit</a></li>
            <li><a href="#">Consectetuer</a></li>
            <li><a href="#">Adipiscing elit</a></li>
            <li><a href="#">Quis nostrud</a></li>
        </ul>
    </div>

    <div class="content">
        <c:out value="${searchResult['name']}"/><br/>
        <c:out value="${searchResult['title']}"/><br/>
        <c:out value="${searchResult['description']}"/><br/>
        <c:out value="${searchResult['content']}"/><br/>
        <a href="${searchResult['url']}">Hae</a>
    </div>
</div>
</body>
</html>




