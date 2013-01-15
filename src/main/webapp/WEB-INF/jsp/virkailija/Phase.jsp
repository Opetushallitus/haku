<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
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
<c:set var="vaihe" value="${element}" scope="request"/>
<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="page"/>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
    <meta charset="utf-8"/>
    <!--
        <link rel="stylesheet" href="${contextPath}/resources/css/screen.css" type="text/css">
        -->
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
<div id="viewport">
    <div id="overlay" style="display: none;">
    </div>
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

            <div class="clear"></div>
        </nav>

        <div id="breadcrumbs">
            <ul>
                <li><span><a href="#">Koulutustarjonta</a></span></li>
                <li><span><a href="#">Hakemuksen esikatselu</a></span></li>
            </ul>
        </div>

        <div class="grid16-16">
            <a href="#" class="button small back"></a>
            <a href="#" class="button small">Tee VRK haku</a>
            <a href="#" class="button small disabled">Tee TOR haku</a>
            <c:if test="${applicationProcessState.status ne 'Peruttu'}">
                <form class="inline-block" method="post"
                      action="${pageContext.request.contextPath}/virkailija/hakemus/${oid}/applicationProcessState/CANCELLED/">
                    <button type="submit"><span><span>Passivoi hakemus</span></span></button>
                </form>
            </c:if>
        </div>

        <section class="grid16-16 margin-top-2">

            <div class="tabs">
                <a href="#" data-tabs-group="applicationtabs" data-tabs-id="hakemus"
                   class="tab current"><span>Hakemus</span></a>
                <a href="#" data-tabs-group="applicationtabs" data-tabs-id="lisatiedot" class="tab"><span>Kelpoisuus ja liitteet</span></a>
            </div>

            <div class="tabsheets">

                <section id="hakemus" class="tabsheet" data-tabs-group="applicationtabs" data-tabs-id="hakemus"
                         style="display: block;">

                    <h3><c:out value="${categoryData['Etunimet']}"/>&nbsp;<c:out
                            value="${it.categoryData['Sukunimi']}"/></h3>

                    <table class="width-50 margin-top-2">
                        <tr>
                            <td><span class="bold">Hakemusnumero: </span><c:out value="${oid}"/></td>
                            <td><span class="bold">Hakemuksen tila: </span><c:out
                                    value="${applicationProcessState.status}"/></td>
                        </tr>
                        <tr>
                            <td><span class="bold">Henkilötunnus: </span><c:out
                                    value="${categoryData['Henkilotunnus']}"/></td>
                            <td><span class="bold">Oppijanumero: </span>xxxx</td>
                        </tr>

                    </table>
                    <hr/>
                    <c:set var="preview" value="${vaihe.preview}" scope="request"/>
                    <c:choose>
                        <c:when test="${preview}">
                            <div class="form">
                                <c:forEach var="child" items="${vaihe.children}">
                                    <c:set var="element" value="${child}" scope="request"/>
                                    <c:set var="parentId" value="${form.id}.${vaihe.id}" scope="request"/>
                                    <jsp:include page="../elements/${child.type}Preview.jsp"/>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <form id="form-${vaihe.id}" class="form" method="post">
                                <c:forEach var="child" items="${vaihe.children}">
                                    <c:set var="element" value="${child}" scope="request"/>
                                    <c:set var="parentId" value="${form.id}.${vaihe.id}" scope="request"/>
                                    <jsp:include page="../elements/${child.type}.jsp"/>
                                </c:forEach>
                                <button class="save" name="vaiheId" type="submit"
                                        value="${applicationPhaseId}"><span><span><spring:message
                                        code="lomake.button.save"/></span></span></button>
                            </form>
                        </c:otherwise>
                    </c:choose>
                    <hr/>
                    <c:if test="${(preview)}">
                        <div class="grid16-16">
                            <a href="#" class="button small back"></a>
                            <a href="#" class="button small">Tee VRK haku</a>
                            <a href="#" class="button small disabled">Tee TOR haku</a>
                            <c:if test="${applicationProcessState.status ne 'Peruttu'}">
                                <form class="inline-block" method="post"
                                      action="${pageContext.request.contextPath}/virkailija/hakemus/${oid}/applicationProcessState/CANCELLED/">
                                    <button type="submit"><span><span>Passivoi hakemus</span></span></button>
                                </form>
                            </c:if>
                        </div>
                    </c:if>
                    <div class="clear"></div>
                </section>
            </div>
        </section>
        <footer id="footer" class="grid36-16">
            <div class="footer-container">
                <div class="grid16-8 footer-logo">
                    <img src="${contextPath}/content/logo-opetus-ja-kulttuuriministerio.png">
                </div>
                <div class="grid16-8 footer-logo">
                    <img src="${contextPath}/content/logo-oph.png">
                </div>
                <div class="clear"></div>
            </div>
        </footer>
        <div class="clear"></div>
    </div>
</div>
</body>
</html>
