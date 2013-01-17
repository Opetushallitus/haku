<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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

<!DOCTYPE html>
<html>
<head>
    <title>Opetushallitus </title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <link href="${pageContext.request.contextPath}/resources/css/oppija.css" type="text/css" rel="stylesheet"/>
</head>
<body>
<div id="viewport">
    <div id="overlay"></div>
    <div id="help-page">
        <section id="page">
            <h1><c:out value="${it.themeTitle}"/></h1>
            <c:forEach var="entry" items="${it.themeHelpMap}">
                <h3><c:out value="${entry.key}"/></h3>

                <p><c:out value="${entry.value}"/></p>
            </c:forEach>
        </section>
    </div>
</div>
</body>
</html>
