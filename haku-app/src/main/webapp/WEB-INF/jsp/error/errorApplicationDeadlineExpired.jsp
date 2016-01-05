<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page isErrorPage="true" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<%--
  ~ Copyright (c) 2015 The Finnish Board of Education - Opetushallitus
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
  <fmt:setBundle basename="messages" scope="application"/>
  <c:set var="contextPath" value="${pageContext.request.contextPath}" scope="request"/>
  <!DOCTYPE HTML>
  <html>
  <head>
    <meta charset="utf-8"/>
    <title>Opintopolku.fi</title>
    <link rel="stylesheet" href="${contextPath}/resources/css/oppija.css" type="text/css">
    <link href="https://fonts.googleapis.com/css?family=PT+Sans+Narrow:700|PT+Serif:400italic" rel="stylesheet"
    type="text/css">
    <haku:icons/>
</head>
<body class="front-page" style="margin:10px 50px;">
    <header>
        <div class="logo-bg">
            <div class="container">
                <a id="home-link" href="/wp/fi/"><img src="${contextPath}/resources/img/Opintopolku_FI_logo.png"
                    alt="Opintopolku.fi"/></a>
            </div>
        </div>
    </header>

    <!-- suomeksi -->
    <h1>Hakuaika tähän hakuun on päättynyt. Tarkista hakuajat opintopolku.fi-palvelusta.</h1>
    <p>
        Palaa takaisin <a href="/wp/fi/">opintopolku.fi-palveluun</a>.
    </p>
    </p>

    <!-- på svenska -->
    <hr role="presentation" class="margin-top-2"/>
    <div class="logo-bg">
        <div class="container">
            <a id="home-link" href="/wp/sv/"><img src="${contextPath}/resources/img/Opintopolku_SV_logo.png" alt="Studieinfo.fi"/></a>
        </div>
    </div>

    <h1>Ansökningstiden i denna ansökan har gått ut. Kontrollera ansökningstiderna i Studieinfo.fi-tjänsten.</h1>
    <p>
        Gå tillbaka till <a href="/wp/sv/">studienfo.fi-tjänsten</a>.
    </p>


    <p>
    </p>

    <!-- in english -->
    <hr role="presentation" class="margin-top-2"/>
    <div class="logo-bg">
        <div class="container">
            <a id="home-link" href="/wp2/en/"><img src="${contextPath}/resources/img/Opintopolku_EN_logo.png" alt="Studyinfo.fi"/></a>
        </div>
    </div>

    <h1>The application period has ended. Check the exact application dates from studyinfo.fi.</h1>
    <p>
        Return to the <a href="/wp2/en/">studyinfo.fi –service</a>.
    </p>

    <footer style="width: 100%;bottom: 0; position: fixed; float: right">${it.timestamp} &nbsp;${it.error_id}</footer>
    <!-- Piwik -->
    <script src="${contextPath}/resources/piwik/piwik.js" type="text/javascript"></script>
    <!-- End Piwik Code -->
</body>
</html>
