<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
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
<fmt:setBundle basename="messages" scope="session"/>
<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="page"/>
<c:set var="categoryData" value="${it.categoryData}" scope="request"/>
<c:set var="form" value="${it.form}" scope="request"/>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
    <meta charset="utf-8"/>
    <link rel="stylesheet" href="${contextPath}/resources/css/oppija.css" type="text/css">
    <link rel="stylesheet"
          href="${contextPath}/resources/jquery-ui-theme/jquery-ui-1.8.23.custom.css"
          type="text/css">
    <title><haku:i18nText value="${form.i18nText}"/></title>
    <script src="${contextPath}/resources/jquery/jquery.min.js"></script>
    <script src="${contextPath}/resources/jquery/jquery-ui-1.8.23.custom.min.js"></script>
    <script src="${contextPath}/resources/javascript/rules.js"></script>
    <script src="${contextPath}/resources/javascript/master.js"></script>
</head>
<body>
<div id="viewport">
    <div id="overlay"></div>
    <div id="site">

        <header id="siteheader">

        </header>

        <section id="page">

            <section id="pageheader" class="grid16-16">

                <nav class="main-navigation">
                    <ul class="navigation">
                        <li class="home"><a href="index.html">Etusivu</a></li>
                        <li><a href="lukio.html">Lukio</a></li>
                        <li><a href="#">Ammatillinen koulutus</a></li>
                        <li><a href="#">Ammattikorkeakoulu</a></li>
                        <li><a href="#">Yliopisto</a></li>
                        <li><a href="#">Täydennyskoulutus</a></li>
                        <li><a href="#">Opintojen valinta</a></li>
                    </ul>
                </nav>

            </section>
            <div class="clear"></div>

            <section class="content-container">


                <div class="grid16-16">

                    <h1><fmt:message key="form.title"/></h1>

                    <h2><haku:i18nText value="${form.i18nText}"/></h2>

                    <ul class="form-steps">
                        <c:forEach var="phase" items="${form.children}" varStatus="status">
                            <li><span><span class="index">${status.count}</span><haku:i18nText
                                    value="${phase.i18nText}"/> &gt;</span></li>
                        </c:forEach>
                        <li>
                            <a class="current"><span class="index"><c:out
                                    value="${fn:length(form.children) + 1}"/></span><fmt:message
                                    key="lomake.valmis"/></a>
                        </li>
                    </ul>
                    <div class="clear"></div>
                </div>
                <div class="clear"></div>

                <div class="form" data-form-step-id="7">
                    <img src="${contextPath}/static-html/content/Valmis-Kuva1.jpg" title="" alt=""
                         class="set-right"/>

                    <h3 class="h2"><fmt:message key="lomake.valmis.hakemuksesionvastaanotettu"/></h3>

                    <p class="application-number">
                        <fmt:message key="lomake.valmis.hakulomakenumerosi"/>: <span class="number"><c:out
                            value="${it.applicationNumber}"/></span>
                    </p>

                    <c:if test="${(not empty categoryData['Sähköposti'])}">
                    <p>
                        <fmt:message key="lomake.valmis.sinulleonlahetettyvahvistussahkopostiisi"/>: <c:out
                            value="${categoryData['Sähköposti']}"/>
                    </p>
                    </c:if>

                    <p>
                        Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam nisi nisl, dignissim id
                        molestie non, vehicula eu risus. Donec eu magna neque, eget sodales lacus. Vivamus eget enim
                        justo, sed consectetur enim. Curabitur nisl erat, egestas ut facilisis vel, interdum ac risus.
                    </p>

                    <button class="print"><span><span><fmt:message key="lomake.valmis.button.tulosta"/></span></span>
                    </button>
                    <button class="pdf"><span><span><fmt:message key="lomake.valmis.button.tallennapdf"/></span></span>
                    </button>


                    <div class="clear"></div>
                    <hr/>

                    <img src="${contextPath}/static-html/content/Valmis-Kuva2.jpg" title="" alt=""
                         class="set-left"/>

                    <h3>Muutoksen tekeminen</h3>

                    <p>
                        Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam nisi nisl, dignissim id
                        molestie non, vehicula eu risus. Donec eu magna neque, eget sodales lacus. Vivamus eget enim
                        justo, sed consectetur enim. Curabitur nisl erat, egestas ut facilisis vel, interdum ac risus.
                    </p>

                    <p>
                        Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam nisi nisl, dignissim id
                        molestie non, vehicula eu risus. Donec eu magna neque, eget sodales lacus. Vivamus eget enim
                        justo, sed consectetur enim. Curabitur nisl erat, egestas ut facilisis vel, interdum ac risus.
                    </p>

                    <div class="clear"></div>
                    <hr/>

                    <img src="${contextPath}/static-html/content/Valmis-Kuva3.jpg" title="" alt=""
                         class="set-right"/>

                    <h3>Palautekysely</h3>

                    <p>
                        Anna palautetta palvelun toiminnasta vastaamalla lyhyeen kyselyyn. Voit vastata kyselyyn
                        26.6.2012 asti.
                    </p>

                    <p>
                        <a href="#">Siirry palautekyselyyn</a>
                    </p>

                    <p>
                        Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam nisi nisl, dignissim id
                        molestie non, vehicula eu risus. Donec eu magna neque, eget sodales lacus. Vivamus eget enim
                        justo, sed consectetur enim. Curabitur nisl erat, egestas ut facilisis vel, interdum ac risus.
                    </p>

                    <div class="clear"></div>

            </section>
        </section>
    </div>
</div>
</body>
</html>
