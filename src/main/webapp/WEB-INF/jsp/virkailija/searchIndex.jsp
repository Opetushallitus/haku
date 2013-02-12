<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

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

<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="page"/>
<!doctype html>

<html lang="en" ng-app="virkailija">
<head>
    <title>Hakijatiedot</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <link href="${contextPath}/resources/css/virkailija.css" type="text/css" rel="stylesheet"/>
    <script src="${contextPath}/resources/jquery/jquery-1.8.0.min.js" type="text/javascript"></script>
    <script src="${contextPath}/resources/javascript/virkailija.js" type="text/javascript"></script>
</head>
<body>

<div id="viewport">
    <div id="overlay" style="display: none;"></div>
    <div id="wrapper">
        <header id="siteheader">
            <div class="primarylinks">
                <a href="#">Oppijan verkkopalvelu</a> &nbsp;
                <a href="#">Virkailijan työpöytä</a>
            </div>
            <div class="secondarylinks">
                <a href="#">Omat tiedot</a> &nbsp;
                <a href="#">Viestintä</a> &nbsp;
                <a href="#">Asiakaspalvelu</a> &nbsp;
                <a href="#">Tukipalvelut</a>
            </div>
        </header>
        <nav id="navigation" class="grid16-16">
            <ul class="level1">
                <li><a href="#" class=""><span>Organisaation tiedot</span></a></li>
                <li>
                    <a href="index.html" class="current"><span>Koulutustarjonta</span></a>
                    <ul class="level2">
                        <li><a href="#" class="">Koulutuksen tiedot</a></li>
                        <li><a href="#" class="">Koulutuksen toteutus ja hakukohde</a></li>
                        <li><a href="#" class="">Organisaation kuvailevat tiedot</a></li>
                        <li><a href="#" class="">Järjestämissopimukset</a></li>
                        <li><a href="#" class="">Järjestämisluvat</a></li>
                    </ul>
                </li>
                <li><a href="#" class=""><span>Valintaperusteet</span></a></li>
                <li><a href="#" class=""><span>Koulutussuunnittelu</span></a></li>
                <li><a href="#" class=""><span>Sisällönhallinta</span></a></li>
            </ul>
        </nav>

        <jsp:include page="searchOrg.jsp"/>
        <jsp:include page="searchForm.jsp"/>
        <jsp:include page="searchResults.jsp"/>
        <jsp:include page="footer.jsp"/>

        <div class="clear"></div>
    </div>
</div>

</body>
</html>
