<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
  ~
  ~ This program is free software:  Licensed under the EUPL, Version 1.1 or - as
  ~ soon as they will be approved by the European Commission - subsequent versions
  ~ of the EUPL (the "Licence");
  ~
  ~ You may not use this work except in compliance with the Licence.
  ~ You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
  ~
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ European Union Public Licence for more details.
  --%>

<!DOCTYPE HTML>
<html>
<head>
    <META http-equiv="Content-Type" content="text/html;charset=UTF-8">
    <meta charset="utf-8"/>
    <link href="${pageContext.request.contextPath}/resources/css/screen.css" type="text/css" rel="stylesheet"/>
    <title>admin</title>
</head>
<body>
<ul>
    <li><a href="${pageContext.request.contextPath}/lomake/">Hakujen selaus</a></li>
    <li><a href="${pageContext.request.contextPath}/admin/upload">Admin - Lataa malli tiedostona</a></li>
    <li><a href="${pageContext.request.contextPath}/admin/edit">Admin - Muokkaa mallia</a></li>
    <li><a href="${pageContext.request.contextPath}/admin/model">Admin - Tämänhetkinen malli (json)</a></li>
    <li><a href="${pageContext.request.contextPath}/static-html/lomake.html">Staattinen lomake</a></li>
    <li><a href="${pageContext.request.contextPath}/static-html/index.html">Staattinen etusivu</a></li>
    <li><a href="${pageContext.request.contextPath}/static-html/vapaasanahaku.html">Vapaasanahaku staattinen ui</a></li>
    <li><a href="${pageContext.request.contextPath}">Oppijan verkkopalvelu</a></li>
    <li><a href="${pageContext.request.contextPath}/tarjontatiedot">Oppijan verkkopalvelu - Vapaasanahaku</a></li>
    <li><a href="${pageContext.request.contextPath}/static-html/add.html">Oppijan verkkopalvelu - Lataa aineisto</a>
    </li>
    <li><a href="${pageContext.request.contextPath}/admin/index/update">Oppijan verkkopalvelu - Päivitä hakuindeksi</a>
    </li>
    <li><a href="${pageContext.request.contextPath}/admin/index/drop">Oppijan verkkopalvelu - Tyhjennä hakuindeksi</a>
    </li>
</ul>

<h3>Properties:</h3>
<c:forEach var="property" items="${properties}">
    <p><b>${property.key}:</b> ${property.value}</p>
</c:forEach>

</body>
</html>
