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
<c:set var="phase" value="${it.element}" scope="request"/>
<c:set var="form" value="${it.form}" scope="request"/>
<c:set var="oid" value="${it.oid}" scope="request"/>
<c:set var="application" value="${it.application}" scope="request"/>
<c:set var="categoryData" value="${it.application.vastauksetMerged}" scope="request"/>
<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="page"/>
<c:set var="errorMessages" value="${it.errorMessages}" scope="request"/>
<html>
<head>

    <haku:meta/>
    <link rel="stylesheet" href="${contextPath}/resources/jquery-ui-theme/jquery-ui-1.8.23.custom.css" type="text/css">
    <link href="${contextPath}/resources/css/oppija.css" type="text/css" rel="stylesheet">
    <link href="${contextPath}/resources/css/virkailija.css" type="text/css" rel="stylesheet">
    <script src="${contextPath}/resources/jquery/jquery.min.js" type="text/javascript"></script>
    <script src="${contextPath}/resources/jquery/jquery-ui-1.8.23.custom.min.js" type="text/javascript"></script>
    <script src="${contextPath}/resources/jquery/jquery.ui.datepicker-fi.js" type="text/javascript"></script>
    <script src="${contextPath}/resources/jquery/xdr.js"></script>
    <script src="${contextPath}/resources/javascript/rules.js" type="text/javascript"></script>
    <script src="${contextPath}/resources/javascript/master.js" type="text/javascript"></script>
    <script type="text/javascript" src="/virkailija-raamit/apply-raamit.js"></script>
    <title><fmt:message key="virkailija.otsikko"/></title>

    <haku:ie9StyleFix/>
</head>
<body>
<c:set var="preview" value="${phase.preview}" scope="request"/>


<div id="viewport">
    <div id="overlay">
        <c:choose>
            <c:when test="${preview}">

                <div class="popup-dialog-wrapper" id="confirmPassivation" style="z-index:1000;display:none;">
                    <span class="popup-dialog-close">&#8203;</span>

                    <div class="popup-dialog">
                        <span class="popup-dialog-close">&#8203;</span>

                        <div class="popup-dialog-header">
                            <h3><fmt:message key="virkailija.hakemus.passivoi.hakemus.varmistus"/></h3>
                        </div>
                        <div class="popup-dialog-content">
                            <form method="post" action="${contextPath}/virkailija/hakemus/${application.oid}/passivate">
                                <p><fmt:message key="virkailija.hakemus.passivoi.hakemus.viesti"/></p>
                                <textarea name="passivation-reason" ></textarea>
                                <button name="nav-send" value="true" data-po-hide="confirmPassivation">
									<span>
										<span><fmt:message key="lomake.send.confirm.no"/></span>
									</span>
                                </button>
                                <button id="submit_confirm" class="primary set-right" name="nav-send" type="submit"
                                        value="true">
									<span>
										<span><fmt:message key="lomake.send.confirm.yes"/></span>
									</span>
                                </button>
                                <div class="clear"></div>
                            </form>
                        </div>
                    </div>
                </div>
            </c:when>
        </c:choose>

    </div>
</div>
<div id="wrapper" class="virkailija">

    <virkailija:headerButtons oid="${application.oid}" preview="${preview}" />

    <div class="grid16-16">
        <h3><c:out value="${categoryData['Etunimet']}" escapeXml="true"/>&nbsp;<c:out
                value="${categoryData['Sukunimi']}" escapeXml="true"/></h3>
        <table class="margin-top-2">
            <tr>
                <haku:infoCell key="virkailija.hakemus.hakemusnro" value="${application.oid}"/>
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
                <haku:infoCell key="virkailija.hakemus.puhelin" value="${categoryData['matkapuhelinnumero']}"/>
            </tr>
            <tr>
                <haku:infoCell key="virkailija.hakemus.henkilotunnus" value="${categoryData['Henkilotunnus']}"/>
                <haku:infoCell key="virkailija.hakemus.oppijanumero" value="${application.personOid}"/>
                <haku:infoCell key="virkailija.hakemus.sahkoposti" value="${categoryData['Sähköposti']}"/>
            </tr>
            <tr>
                <td></td>
                <td></td>
                <haku:infoCell key="virkailija.vaihe.aidinkieli" value="${categoryData['aidinkieli']}"/>
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

            </section>

        </div>
    </section>
</div>
</body>
</html>
