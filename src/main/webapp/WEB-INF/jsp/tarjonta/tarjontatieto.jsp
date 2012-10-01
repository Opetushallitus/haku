<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>

    <jsp:include page="head.jsp"/>

<body>

<jsp:include page="siteheader.jsp"/>

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
        <c:out value="${searchResult['nimi']}"/><br/>
        <c:out value="${searchResult['otsikko']}"/><br/>
        <c:out value="${searchResult['Kuvaus']}"/><br/>
        <c:out value="${searchResult['sisalto']}"/><br/>
        <a href="${searchResult['linkki']}">Hae</a>
    </div>
</div>
</body>
</html>




