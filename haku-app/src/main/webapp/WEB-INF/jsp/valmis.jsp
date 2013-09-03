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
<c:set var="categoryData" value="${it.application.vastauksetMerged}" scope="request"/>
<c:set var="form" value="${it.form}" scope="request"/>
<c:set var="application" value="${it.application}" scope="request"/>
<c:set var="discretionaryAttachmentAOIds" value="${it.discretionaryAttachmentAOIds}" scope="request"/>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
    <meta charset="utf-8"/>
    <script src="${contextPath}/resources/jquery/jquery.min.js" type="text/javascript"></script>
    <script src="${contextPath}/resources/jquery/xdr.js"></script>
    <link rel="stylesheet" href="${contextPath}/resources/css/oppija.css" type="text/css">
    <link rel="stylesheet" href="${contextPath}/resources/jquery-ui-theme/jquery-ui-1.8.23.custom.css" type="text/css">
    <title><haku:i18nText value="${form.i18nText}"/></title>
</head>
<body>
<div id="viewport">
    <div id="overlay"></div>
    <div id="site">

        <header id="siteheader">

        </header>

        <section id="page">

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

                    <h3 class="h2"><fmt:message key="lomake.valmis.hakemuksesionvastaanotettu"/></h3>

                    <p class="application-number">
                        <fmt:message key="lomake.valmis.hakulomakenumerosi"/>: <span class="number"><c:out
                            value="${application.oid}"/></span>
                    </p>

                    <c:if test="${(not empty categoryData['Sähköposti'])}">
                    <p>
                        <fmt:message key="lomake.valmis.sinulleonlahetettyvahvistussahkopostiisi"/>: <c:out
                            value="${categoryData['Sähköposti']}"/>
                    </p>
                    </c:if>

                    <p>
                        <fmt:message key="lomake.valmis.p1"/>
                    </p>

                    <p>
                        <fmt:message key="lomake.valmis.p2"/>
                    </p>

                    <p>
                        <fmt:message key="lomake.valmis.p3"/>
                    </p>

                    <p>
                        <a href="${contextPath}/lomake/${application.applicationSystemId}/tulostus/${application.oid}" class="button small print" target="_blank"><fmt:message
                        key="lomake.valmis.button.tulosta"/></a>
                    </p>

                    <div class="clear"></div>
                    <jsp:include page="./print/discretionaryAttachments.jsp"/>
                    <hr/>

                    <c:if test="${categoryData['preference1_urheilijan_ammatillisen_koulutuksen_lisakysymys'] or categoryData['preference2_urheilijan_ammatillisen_koulutuksen_lisakysymys'] or categoryData['preference3_urheilijan_ammatillisen_koulutuksen_lisakysymys'] or categoryData['preference4_urheilijan_ammatillisen_koulutuksen_lisakysymys'] or categoryData['preference5_urheilijan_ammatillisen_koulutuksen_lisakysymys']}">
                        <h3><fmt:message key="lomake.valmis.haeturheilijana.header"/></h3>
                        <p>
                             <fmt:message key="lomake.valmis.haeturheilijana"/>
                        </p>
                        <p>
                            <a href="http://www.noc.fi/huippu-urheilu/tukipalvelut/opinto-ja_uraohjaus/urheilijoiden_opiskelumahdollisu/" target="_blank">
                                http://www.noc.fi/huippu-urheilu/tukipalvelut/opinto-ja_uraohjaus/urheilijoiden_opiskelumahdollisu/
                            </a>
                        </p>
                        <hr/>
                    </c:if>
                    <h3><fmt:message key="lomake.valmis.muutoksentekeminen"/></h3>

                    <p>
                        <fmt:message key="lomake.valmis.muutoksentekeminen.p1"/>
                    </p>

                    <p>
                        <fmt:message key="lomake.valmis.muutoksentekeminen.p2"/>
                    </p>

                    <p>
                        <fmt:message key="lomake.valmis.muutoksentekeminen.p3"/>
                    </p>

                    <div class="clear"></div>
                    <hr/>

                    <h3><fmt:message key="lomake.valmis.palaute"/></h3>

                    <p>
                        Anna palautetta palvelun toiminnasta vastaamalla lyhyeen kyselyyn. Voit vastata kyselyyn
                        26.6.2012 asti.
                    </p>

                    <p>
                        <a href="https://opintopolku.fi/palaute" target="_blank">https://opintopolku.fi/palaute</a>
                    </p>

                    <div class="clear"></div>

            </section>
        </section>
    </div>
</div>
</body>
</html>
