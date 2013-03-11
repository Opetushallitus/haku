<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
<!DOCTYPE html>
<fmt:setBundle basename="messages"/>
<c:set var="phase" value="${it.element}" scope="request"/>
<c:set var="form" value="${it.form}" scope="request"/>
<c:set var="oid" value="${it.oid}" scope="request"/>
<c:set var="application" value="${it.application}" scope="request"/>
<c:set var="categoryData" value="${it.application.vastauksetMerged}" scope="request"/>
<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="page"/>
<c:set var="errorMessages" value="${it.errorMessages}" scope="request"/>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
    <meta charset="utf-8"/>
    <link rel="stylesheet"
          href="${contextPath}/resources/jquery-ui-theme/jquery-ui-1.8.23.custom.css"
          type="text/css">
    <link href="${contextPath}/resources/css/virkailija.css" type="text/css" rel="stylesheet"/>
    <title>Opetushallitus</title>
    <script src="${contextPath}/resources/jquery/jquery.min.js"></script>
    <script src="${contextPath}/resources/jquery/jquery-ui-1.8.23.custom.min.js"></script>
    <script src="${contextPath}/resources/javascript/rules.js"></script>
    <script src="${contextPath}/resources/javascript/master.js"></script>

    <!--[if gte IE 9]>
    <style type="text/css">
        .tabs .tab span {
            filter: none;
        }
    </style>
    <![endif]-->
</head>

<body>

<div id="wrapper" class="virkailija">
    <header id="siteheader">

        <div class="primarylinks">
            <a href="#">Oppijan verkkopalvelu</a> &nbsp;
            <a href="#">Virkailijan työpöytä</a>
        </div>

        <div class="secondarylinks">
            <a href="${contextPath}/user/logout">Kirjaudu ulos</a> &nbsp;
            <a href="${contextPath}/j_spring_cas_security_logout">CAS Logout</a> &nbsp;
            <a href="#">Omat tiedot</a> &nbsp;
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

        <div class="clear"></div>
    </nav>

    <div id="breadcrumbs">
        <ul>
            <li><span><a href="#">Koulutustarjonta</a></span></li>
            <li><span><a href="#">Hakemuksen esikatselu</a></span></li>
        </ul>
    </div>

    <div class="grid16-16">
        <a href="${contextPath}/virkailija/hakemus" class="button small back"></a>
        <a href="#" class="button small disabled">Tee VRK haku</a>
        <a href="#" class="button small disabled">Tee TOR haku</a>
        <a href="#" class="button small disabled">Passivoi hakemus</a>
    </div>

    <div class="grid16-16">
        <h3><c:out value="${categoryData['Etunimet']}"/>&nbsp;<c:out value="${categoryData['Sukunimi']}"/></h3>
        <table class="width-50 margin-top-2">
            <tr>
                <td><span class="bold">Hakemusnumero: </span><c:out value="${application.oid}"/></td>
                <td><span class="bold">Hakemuksen tila: </span><c:out value="${application.state}"/></td>
                <td><span class="bold">Puhelin: </span><c:out value="${categoryData['matkapuhelinnumero']}"/></td>
            </tr>
            <tr>
                <td><span class="bold">Henkilötunnus: </span><c:out value="${categoryData['Henkilotunnus']}"/></td>
                <td><span class="bold">Oppijanumero: </span><c:out value="${application.personOid}"/></td>
                <td><span class="bold">Sähköposti: </span><c:out value="${categoryData['Sähköposti']}"/></td>
            </tr>
            <tr>
                <td></td>
                <td></td>
                <td><span class="bold">Äidinkieli: </span><c:out value="${categoryData['äidinkieli']}"/></td>
            </tr>

        </table>
    </div>

    <section class="grid16-16 margin-top-2">

        <div class="tabs">
            <a href="#" data-tabs-group="applicationtabs" data-tabs-id="hakemus"
               class="tab current"><span>Hakemus</span></a>
        </div>

        <div class="tabsheets">
            <section id="hakemus" class="tabsheet" data-tabs-group="applicationtabs" data-tabs-id="hakemus"
                     style="display: block">
                <haku:messages messages="${errorMessages}" additionalClass="warming"/>
                <c:set var="preview" value="${phase.preview}" scope="request"/>
                <c:choose>
                    <c:when test="${preview}">

                        <div class="form">

                            <c:forEach var="child" items="${phase.children}">
                                <c:set var="element" value="${child}" scope="request"/>
                                <c:set var="parentId" value="${form.id}.${phase.id}" scope="request"/>
                                <jsp:include page="../elements/${child.type}Preview.jsp"/>
                            </c:forEach>
                            <jsp:include page="./additionalInfoPreview.jsp"/>
                        </div>
                    </c:when>
                    <c:otherwise>

                        <form id="form-${phase.id}" class="form" method="post">
                            <c:forEach var="child" items="${phase.children}">
                                <c:set var="element" value="${child}" scope="request"/>
                                <c:set var="parentId" value="${form.id}.${phase.id}" scope="request"/>
                                <jsp:include page="../elements/${child.type}.jsp"/>
                            </c:forEach>
                            <button class="save" name="vaiheId" type="submit" value="${phase.id}">
                                <span><span><fmt:message key="lomake.button.save"/></span></span>
                            </button>
                        </form>

                    </c:otherwise>

                </c:choose>

                <hr/>

                <c:if test="${(preview)}">
                    <div>
                        <a href="${contextPath}/virkailija/hakemus" class="button small back"></a>
                        <a href="#" class="button small disabled">Tee VRK haku</a>
                        <a href="#" class="button small disabled">Tee TOR haku</a>
                        <a href="#" class="button small disabled">Passivoi hakemus</a>
                    </div>

                </c:if>

            </section>

        </div>
    </section>

    <footer id="footer" class="grid16-16">
        <div class="footer-container">
            <div class="grid16-8 footer-logo">
                <img src="${contextPath}/resources/img/logo-opetus-ja-kulttuuriministerio.png">
            </div>
            <div class="grid16-8 footer-logo">
                <img src="${contextPath}/resources/img/logo-oph.png">
            </div>
            <div class="clear"></div>
        </div>
    </footer>
    <div class="clear"></div>
</div>
</div>
</body>
</html>
