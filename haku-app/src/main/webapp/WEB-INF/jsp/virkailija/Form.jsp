<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="virkailija" tagdir="/WEB-INF/tags/virkailija" %>
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
<fmt:setBundle basename="messages" scope="session"/>
<c:set var="preview" value="${it.preview}" scope="request"/>
<c:set var="form" value="${it.form}" scope="request"/>
<c:set var="oid" value="${it.oid}" scope="request"/>
<c:set var="application" value="${it.application}" scope="request"/>
<c:set var="applicationSystem" value="${it.applicationSystem}" scope="request"/>
<c:set var="answers" value="${it.application.vastauksetMerged}" scope="request"/>
<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="request"/>
<c:set var="errorMessages" value="${it.errorMessages}" scope="request"/>
<html>
<head>
    <haku:meta/>
    <haku:icons contextPath="${contextPath}"/>
    <link rel="stylesheet" href="${contextPath}/resources/jquery-ui-theme/jquery-ui-1.8.23.custom.css" type="text/css">
    <link href="${contextPath}/resources/css/oppija.css" type="text/css" rel="stylesheet">
    <link href="${contextPath}/resources/css/virkailija.css" type="text/css" rel="stylesheet">
    <script src="${contextPath}/resources/jquery/jquery.min.js" type="text/javascript"></script>
    <script src="${contextPath}/resources/jquery/jquery-ui-1.8.23.custom.min.js" type="text/javascript"></script>
    <script src="${contextPath}/resources/jquery/jquery.ui.datepicker-fi.js" type="text/javascript"></script>
    <script src="${contextPath}/resources/jquery/xdr.js"></script>
    <script src="${contextPath}/resources/javascript/rules.js" type="text/javascript"></script>
    <script src="${contextPath}/resources/javascript/master.js" type="text/javascript"></script>
    <script src="${contextPath}/resources/javascript/jquery.cookie.js" type="text/javascript"></script>
    <script src="${contextPath}/resources/javascript/virkailija.js" type="text/javascript"></script>
    <script type="text/javascript" src="/virkailija-raamit/apply-raamit.js"></script>
    <title><fmt:message key="virkailija.otsikko"/></title>

    <haku:ie9StyleFix/>

</head>
<body>
    <c:if test="${not empty it.applicationList}">
    <script type="text/javascript">
        var previousApplication  = '${it.previousApplication}';
        var nextApplication  = '${it.nextApplication}';
    </script>
    <form method="POST" id="open-applications"
            action="${pageContext.request.contextPath}/virkailija/hakemus/multiple">
        <input type="hidden" name="applicationList" id="applicationList" value="${it.applicationList}"/>
        <input type="hidden" name="selectedApplication" id="selectedApplication" />
    </form>
    </c:if>

<div id="viewport">
    <div id="overlay">
        <c:choose>
            <c:when test="${preview}">
                <c:choose>
                    <c:when test="${application.passive}">
                        <jsp:include page="confirmActivation.jsp"/>
                    </c:when>
                    <c:otherwise>
                        <c:if test="${it.virkailijaDeleteAllowed}">
                            <jsp:include page="confirmPassivation.jsp"/>
                        </c:if>
                    </c:otherwise>
                </c:choose>
                <c:choose>
                    <c:when test="${empty application.studentOid}">
                        <jsp:include page="addStudentOid.jsp" />
                    </c:when>
                </c:choose>
                <c:if test="${it.postProcessAllowed}">
                    <jsp:include page="postProcess.jsp" />
                </c:if>
            </c:when>
        </c:choose>
    </div>

    <div id="wrapper" class="virkailija">

        <virkailija:headerButtons oid="${application.oid}" preview="${preview}" applicationSystem="${applicationSystem}"/>

        <div class="grid16-16">
            <h3><c:out value="${answers['Etunimet']}" escapeXml="true"/>&nbsp;<c:out
                    value="${answers['Sukunimi']}" escapeXml="true"/></h3>
            <table class="margin-top-2">
                <tr>
                    <haku:infoCell key="virkailija.hakemus.hakemusnro" value="${application.oid}" cellId="infocell_oid"/>
                    <c:if test="${application.state eq 'ACTIVE'}">
                        <fmt:message key="virkailija.hakemus.tila.voimassa" var="msg"/>
                    </c:if>
                    <c:if test="${application.state eq 'PASSIVE'}">
                        <fmt:message key="virkailija.hakemus.tila.peruttu" var="msg"/>
                    </c:if>
                    <c:if test="${application.state eq 'INCOMPLETE'}">
                        <fmt:message key="virkailija.hakemus.tila.puutteellinen" var="msg"/>
                    </c:if>
                    <haku:infoCell key="virkailija.hakemus.hakemuksen.tila" value='${msg}'/>
                    <haku:infoCell key="virkailija.hakemus.puhelin" value="${answers['matkapuhelinnumero1']}"/>
                </tr>
                <tr>
                    <haku:infoCell key="virkailija.hakemus.henkilotunnus" value="${answers['Henkilotunnus']}"/>
                    <haku:infoCell key="virkailija.hakemus.henkilonumero" value="${application.personOid}" cellId="infocell_henkilonumero"/>
                    <haku:infoCell key="virkailija.hakemus.sahkoposti" value="${answers['Sähköposti']}"/>
                </tr>
                <tr>
                    <td></td>
                    <haku:infoCell key="virkailija.hakemus.oppijanumero" value="${application.studentOid}" cellId="infocell_oppijanumero"/>
                    <haku:infoCell key="virkailija.vaihe.aidinkieli" value="${answers['aidinkieli']}"/>
                </tr>

            </table>
        </div>

        <section class="grid16-16 margin-top-2">

            <div class="tabs">
                <a href="#" data-tabs-group="applicationtabs" data-tabs-id="application"
                   class="tab current"><span>Hakemus</span></a>
            </div>

            <div class="tabsheets">
                <section id="application" class="tabsheet" data-tabs-group="applicationtabs" data-tabs-id="application"
                         style="display: block">
                    <haku:messages messages="${errorMessages}" additionalClass="warning"/>
                    <c:choose>
                        <c:when test="${preview}">
                            <c:set var="virkailijaPreview" value="true" scope="request"/>
                            <div class="form">
                                <c:forEach var="child" items="${form.children}">
                                    <c:set var="element" value="${child}" scope="request"/>
                                    <jsp:include page="../elements/${child.type}Preview.jsp"/>
                                </c:forEach>
                                <jsp:include page="./additionalInfoPreview.jsp"/>
                                <jsp:include page="./notes.jsp"/>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <c:set var="virkailijaEdit" value="true" scope="request" />
                            <form id="form-${it.element.id}" class="form" method="post">
                                <c:forEach var="child" items="${it.element.children}">
                                    <c:set var="element" value="${child}" scope="request"/>
                                    <jsp:include page="../elements/${child.type}.jsp"/>
                                </c:forEach>
                                <button class="save" name="vaiheId" type="submit" value="${it.element.id}">
                                    <span><span><fmt:message key="lomake.button.save"/></span></span>
                                </button>
                            </form>
                        </c:otherwise>
                    </c:choose>

                    <hr/>

                </section>

            </div>
        </section>
    </div>
    <div class="clear"></div>
</div>
</body>
</html>
