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
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
  ~ European Union Public Licence for more details.
  --%>

<!DOCTYPE HTML>
<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="page"/>
<html>
<head>
    <META http-equiv="Content-Type" content="text/html;charset=UTF-8">
    <meta charset="utf-8"/>
    <link href="${contextPath}/resources/css/oppija.css" type="text/css" rel="stylesheet"/>
    <title>admin</title>
</head>
<body>
<ul>
    <li><a href="${contextPath}/lomake/">Hakujen selaus</a></li>
    <li><a href="${contextPath}/admin/model">Admin - Lataa lomakkeet (json)</a></li>
    <li><a href="${contextPath}/lomakkeenhallinta">Admin - Generoi lomakkeet</a></li>
    <li><a href="/">Oppijan verkkopalvelu</a></li>
</ul>

<h3>Properties:</h3>
<c:forEach var="property" items="${it}">
    <p><b>${property.key}:</b> ${property.value}</p>
</c:forEach>

</body>
</html>
