<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page isErrorPage="true" %>
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
<fmt:setBundle basename="messages" scope="session"/>
<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="request"/>
<!DOCTYPE HTML>
<html>
<head>
    <meta charset="utf-8"/>
    <title>Opintopolku.fi</title>
    <link rel="stylesheet" href="${contextPath}/resources/css/oppija.css" type="text/css">
    <link href="https://fonts.googleapis.com/css?family=PT+Sans+Narrow:700|PT+Serif:400italic" rel="stylesheet" type="text/css">
</head>
<body class="front-page" style="margin:10px 50px;">
<header>
    <div class="logo-bg">
        <div class="container">
            <a id="home-link" href="/"><img src="${contextPath}/resources/img/Opintopolku_FI_logo.png" alt="Opintopolku.fi"/></a>

        </div>
    </div>
</header>
<p>Tapahtui odottamaton virhe. Pahoittelemme tapahtunutta.</p>

<p>Palaa takaisin <a href="/">opintopolku.fi-palveluun</a> ja hae koulutukseen uudelleen.</p>

<p>Mikäli etenit jo hakulomakkeen lähettämiseen saakka, etkä ehtinyt saada tulostetta hakulomakkeestasi, voit pyytää sitä Opintopolun neuvontapalvelusta:</p>

<p>neuvonta@opintopolku.fi</p>

<p>puhelin: 02 9533 1010</p>

Ohjaus- ja neuvontapalvelut<br/>
Opetushallitus<br/>
PL 380<br/>
00531 Helsinki<br/>
<footer style="width: 100%;bottom: 0; position: fixed; float: right">${it.timestamp} &nbsp;${it.error_id}</footer>
<!-- Piwik -->
<script src="${contextPath}/resources/piwik/piwik.js" type="text/javascript"></script>
 <!-- End Piwik Code -->
</body>
</html>
