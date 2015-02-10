<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
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
<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="request"/>
<head>
    <title>Opintopolku.fi</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link href="${contextPath}/resources/css/oppija.css" type="text/css" rel="stylesheet"/>
    <link href="${contextPath}/resources/jquery/jquery-ui-theme/jquery-ui-1.8.23.custom.css" type="text/css"
          rel="stylesheet"/>
    <haku:icons/>
    <script src="${contextPath}/resources/jquery/jquery-1.8.0.min.js"
            type="text/javascript"></script>
    <script src="${contextPath}/resources/jquery/jquery-ui-1.8.23.custom.min.js"
            type="text/javascript"></script>
    <script src="${contextPath}/resources/javascript/underscore-min.js" type="text/javascript"></script>
    <script src="${contextPath}/resources/javascript/bacon.min.js" type="text/javascript"></script>
    <script src="${contextPath}/resources/javascript/master.js" type="text/javascript"></script>
</head>
