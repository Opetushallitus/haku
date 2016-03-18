<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="virkailija" tagdir="/WEB-INF/tags/virkailija" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
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
<fmt:setBundle basename="messages" scope="application"/>
<c:set var="preview" value="${it.preview}" scope="request"/>
<c:set var="form" value="${it.form}" scope="request"/>
<c:set var="oid" value="${it.oid}" scope="request"/>
<c:set var="application" value="${it.application}" scope="request"/>
<c:set var="applicationSystem" value="${it.applicationSystem}" scope="request"/>
<c:set var="answers" value="${it.application.vastauksetMerged}" scope="request"/>
<c:set var="overridden" value="${it.application.overriddenAnswers}" scope="request" />
<c:set var="applicationMeta" value="${it.application.meta}" scope="request" />
<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="request"/>
<c:set var="errorMessages" value="${it.errorMessages}" scope="request"/>
<html>
<head>
    <haku:meta/>
    <haku:icons/>
    <link rel="stylesheet" href="${contextPath}/resources/jquery-ui-theme/jquery-ui-1.8.23.custom.css" type="text/css">
    <link href="${contextPath}/resources/css/oppija.css" type="text/css" rel="stylesheet">
    <link href="${contextPath}/resources/css/virkailija.css" type="text/css" rel="stylesheet">
    <link href="${contextPath}/resources/css/hakemus.css" type="text/css" rel="stylesheet"/>
    <script src="${contextPath}/resources/jquery/jquery.min.js" type="text/javascript"></script>
    <script src="${contextPath}/resources/jquery/jquery-ui-1.8.23.custom.min.js" type="text/javascript"></script>
    <script src="${contextPath}/resources/jquery/jquery.ui.datepicker-trans.js" type="text/javascript"></script>
    <script src="${contextPath}/resources/javascript/underscore-min.js" type="text/javascript"></script>
    <script src="${contextPath}/resources/javascript/bacon.min.js" type="text/javascript"></script>
    <script src="${contextPath}/resources/jquery/xdr.js"></script>
    <script src="${contextPath}/resources/javascript/rules.js" type="text/javascript"></script>
    <script src="${contextPath}/resources/javascript/master.js" type="text/javascript"></script>
    <script src="${contextPath}/resources/jquery/jquery.cookie.js" type="text/javascript"></script>
    <script src="${contextPath}/resources/jquery/jquery.hotkeys.js" type="text/javascript"></script>
    <script src="${contextPath}/resources/javascript/virkailija/application.js" type="text/javascript"></script>
    <script src="${contextPath}/resources/javascript/virkailija/tabs.js" type="text/javascript"></script>
    <script type="text/javascript" src="/virkailija-raamit/apply-raamit.js"></script>
    <title><fmt:message key="virkailija.otsikko"/></title>

    <haku:ie9StyleFix/>

</head>
<body>
<script type="text/javascript">
    var page_settings = {
        contextPath: "${pageContext.request.contextPath}",
        applicationOid: "${oid}",
        lang: "${requestScope['fi_vm_sade_oppija_language']}",
        preview: "${preview}"
    }
</script>

<div id="viewport">

    <div id="overlay">
        <c:if test="${preview}">
            <c:choose>
                <c:when test="${application.passive}">
                    <jsp:include page="confirmActivation.jsp"/>
                </c:when>
                <c:otherwise>
                    <c:if test="${it.virkailijaDeleteAllowed}">
                        <jsp:include page="confirmPassivation.jsp"/>
                    </c:if>
                    <c:if test="${application.draft}">
                        <jsp:include page="confirmActivation.jsp"/>
                    </c:if>
                </c:otherwise>
            </c:choose>
            <c:choose>
                <c:when test="${it.postProcessAllowed and empty application.studentOid and not empty application.personOid}">
                    <jsp:include page="addStudentOid.jsp"/>
                </c:when>
            </c:choose>
            <c:if test="${it.postProcessAllowed}">
                <jsp:include page="postProcess.jsp"/>
            </c:if>
        </c:if>
    </div>
    <div id="wrapper" class="virkailija">

        <virkailija:headerButtons oid="${application.oid}" preview="${preview}"
                                  applicationSystem="${applicationSystem}"/>

        <div class="grid16-16">
            <h3><c:out value="${answers['Etunimet']}" />&nbsp;<c:out
                    value="${answers['Sukunimi']}" />
                <c:if test="${not empty overridden['Etunimet'] or not empty overridden['Sukunimi_user']}">
                    &nbsp;<span title="<c:out value="${overridden['Etunimet']}"/>&nbsp;<c:out
                        value="${overridden['Sukunimi']}"/>">[*]</span>
                </c:if>
                <c:if test="${not empty answers['turvakielto'] and answers['turvakielto']}">
                    <img src="${pageContext.request.contextPath}/resources/img/icon-notification-small.png" />
                    Turvakielto!
                </c:if>
            </h3>
            <table class="margin-top-2">
                <c:if test="${application.redoPostProcess eq 'DONE'}">
                    <fmt:message key="virkailija.hakemus.kasittely.done" var="redoProcessState"/>
                </c:if>
                <c:if test="${application.redoPostProcess eq 'NOMAIL'}">
                    <fmt:message key="virkailija.hakemus.kasittely.nomail" var="redoProcessState"/>
                </c:if>
                <c:if test="${application.redoPostProcess eq 'FULL'}">
                    <fmt:message key="virkailija.hakemus.kasittely.full" var="redoProcessState"/>
                </c:if>
                <c:if test="${application.redoPostProcess eq 'FAILED'}">
                    <fmt:message key="virkailija.hakemus.kasittely.fail" var="redoProcessState"/>
                </c:if>


                <c:if test="${application.state eq 'ACTIVE'}">
                    <fmt:message key="virkailija.hakemus.tila.voimassa" var="applicationState"/>
                </c:if>
                <c:if test="${application.state eq 'PASSIVE'}">
                    <fmt:message key="virkailija.hakemus.tila.peruttu" var="applicationState"/>
                </c:if>
                <c:if test="${application.state eq 'DRAFT'}">
                    <fmt:message key="virkailija.hakemus.tila.luonnos" var="applicationState"/>
                </c:if>
                <c:if test="${application.state eq 'INCOMPLETE'}">
                    <fmt:message key="virkailija.hakemus.tila.puutteellinen" var="applicationState"/>
                </c:if>

                <c:if test="${application.requiredPaymentState eq null}">
                    <c:set value="" var="paymentState"/>
                </c:if>
                <c:if test="${application.requiredPaymentState eq 'NOTIFIED'}">
                    <fmt:message key="virkailija.hakemus.maksun.tila.odottaa" var="paymentState"/>
                </c:if>
                <c:if test="${application.requiredPaymentState eq 'OK'}">
                    <fmt:message key="virkailija.hakemus.maksun.tila.maksettu" var="paymentState"/>
                </c:if>
                <c:if test="${application.requiredPaymentState eq 'NOT_OK'}">
                    <fmt:message key="virkailija.hakemus.maksun.tila.eitehda" var="paymentState"/>
                </c:if>

                <tr>
                    <haku:infoCell key="virkailija.hakemus.hakemusnro" value="${application.oid}"
                                   cellId="infocell_oid"/>

                    <c:choose>
                        <c:when test="${not empty answers['Henkilotunnus']}">
                            <haku:infoCell key="virkailija.hakemus.henkilotunnus" value="${answers['Henkilotunnus']}"/>
                        </c:when>
                        <c:otherwise>
                            <haku:infoCell key="virkailija.hakemus.henkilotunnus" value="${answers['syntymaaika']}"/>
                        </c:otherwise>
                    </c:choose>

                    <td>
                        <span class="bold"><fmt:message key="virkailija.hakemus.lahtokoulu"/>:</span>
                        <span><haku:i18nText value="${it.sendingSchool}"/>&nbsp;<c:out
                                value="${it.sendingClass}"/></span>
                    </td>
                </tr>
                <tr>
                    <haku:infoCell key="virkailija.hakemus.hakemuksen.tila" value='${applicationState}'
                                   cellId="infocell_hakemuksen_tila"/>

                    <haku:infoCell key="virkailija.hakemus.henkilonumero" value="${application.personOid}"
                                   cellId="infocell_henkilonumero"/>

                    <haku:infoCell key="virkailija.vaihe.aidinkieli" value="${answers['aidinkieli']}" id="aidinkieli" rootElement="${applicationSystem.form}"/>
                </tr>
                <tr>
                    <haku:infoCell key="virkailija.hakemus.kasittely" value='${redoProcessState}'/>

                    <haku:infoCell key="virkailija.hakemus.oppijanumero" value="${application.studentOid}"
                                   cellId="infocell_oppijanumero"/>

                    <haku:infoCell key="virkailija.hakemus.puhelin" value="${answers['matkapuhelinnumero1']}"/>
                </tr>
                <tr>
                    <haku:infoCell key="virkailija.hakemus.maksun.tila" value='${paymentState}' cellId="infocell_paymentstate"/>

                    <td>
                        Hakijan tiedot <a href="/suoritusrekisteri/#/muokkaa/${application.personOid}" target="_blank">suoritusrekisteriss&auml;</a></br>
                        Hakijan tiedot <a href="/authentication-henkiloui/html/index.html#/henkilo/${application.personOid}/?permissionCheckService=HAKU_APP" target="_blank">henkil&ouml;palvelussa</a>
                    </td>

                    <haku:infoCell key="virkailija.hakemus.sahkoposti" value="${answers['Sähköposti']}"/>
                </tr>
                <tr>
                    <fmt:formatDate value="${application.paymentDueDate}" pattern="dd.MM.yyyy HH:mm:ss" var="dueDate"/>
                    <haku:infoCell key="virkailija.hakemus.maksun.erapaiva" value='${dueDate}' cellId="infocell_paymentduedate"/>

                    <td colspan="2">&nbsp;</td>
                </tr>
            </table>
        </div>

        <section class="grid16-16 margin-top-2">

            <div class="tabs">
                <a href="#" data-tabs-group="applicationtabs" data-tabs-id="application" id="applicationTab"
                   class="tab current"><span>Hakemus</span></a>

                <a href="#" data-tabs-group="applicationtabs" data-tabs-id="valinta" id="valintaTab"
                   class="tab"><span>Valinta</span></a>

                <c:if test="${applicationSystem.kohdejoukkoUri eq 'haunkohdejoukko_12'}">
                <a href="#" data-tabs-group="applicationtabs" data-tabs-id="kelpoisuusliitteet" id="kelpoisuusliitteetTab"
                   class="tab"><span>Kk-haut: Kelpoisuus ja liitteet</span></a>
                </c:if>

            </div>

            <div class="tabsheets">
                <section id="application" class="tabsheet" data-tabs-group="applicationtabs" data-tabs-id="application"
                         style="display: block">
                    <jsp:include page="hakemusTab.jsp"/>
                </section>

                <section id="valinta" class="tabsheet" data-tabs-group="applicationtabs" data-tabs-id="valinta"
                         style="display: none">
                    <jsp:include page="valintaTab.jsp"/>
                </section>

                <c:if test="${applicationSystem.kohdejoukkoUri eq 'haunkohdejoukko_12'}">
                <section id="kelpoisuusliitteet" class="tabsheet" data-tabs-group="applicationtabs" data-tabs-id="kelpoisuusliitteet"
                         style="display: none">

                    <jsp:include page="kelpoisuusLiitteetTab.jsp"/>
                </section>
                </c:if>

            </div>
        </section>
    </div>
    <div class="clear"></div>
</div>
</body>
</html>
