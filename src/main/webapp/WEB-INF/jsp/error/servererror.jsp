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
<html lang="fi">
<head>
    <meta charset="utf-8"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/styles.css" type="text/css">
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
