<%@ tag description="Header button: VRK, TOR, etc." body-content="empty" pageEncoding="UTF-8" %>
<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ attribute name="oid" required="true" type="java.lang.String" %>
<%@ attribute name="preview" required="true" type="java.lang.Boolean" %>
<%@ attribute name="applicationSystem" required="false" type="fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="page"/>
<div class="grid16-10">
    <c:choose>
        <c:when test="${preview}">
            <a id="back" href="${contextPath}/virkailija/hakemus#useLast" class="button small back"></a>
        </c:when>
        <c:otherwise>
            <a id="back" href="${contextPath}/virkailija/hakemus/${oid}/" class="button small back"></a>
        </c:otherwise>
    </c:choose>
    <sec:authorize access="hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_CRUD')">
    <c:choose>
        <c:when test="${!application.passive}">
            <c:if test="${it.virkailijaDeleteAllowed}">
                <a href="#" id="passivateApplication" data-po-show="confirmPassivation" class="button small "><fmt:message
                        key="virkailija.hakemus.passivoi.hakemus"/></a>
            </c:if>
        </c:when>
        <c:otherwise>
            <a href="#" id="activateApplication" data-po-show="confirmActivation" class="button small "><fmt:message
                    key="virkailija.hakemus.aktivoi.hakemus"/></a>
        </c:otherwise>
    </c:choose>
    <c:choose>
        <c:when test="${it.postProcessAllowed and empty application.studentOid and not empty application.personOid}">
            <a href="#" id="addStudentOid" data-po-show="addStudentOid" class="button small">
                <fmt:message key="virkailija.hakemus.lisaa.oppijanumero" />
            </a>
        </c:when>
    </c:choose>
    <c:if test="${it.postProcessAllowed}">
        <a href="#" id="postProcessApplication" data-po-show="postProcessApplicationDialog" class="button small">
            <fmt:message key="virkailija.hakemus.postProcess" />
        </a>
    </c:if>
    </sec:authorize>
    <a href="${contextPath}/virkailija/hakemus/${oid}/print" class="button small print" target="_blank"><fmt:message
    key="lomake.valmis.button.tulosta"/></a>
</div>
<div class="grid-right-16-6">
    <span><haku:i18nText value="${applicationSystem.name}" /></span>
</div>
<div class="grid16-16">

    <div class="margin-vertical-2">
        <div class="float-left">
        <c:if test="${not empty it.previousApplication}">
            <a href="#" id="previousApplication">&lt;&nbsp;Edellinen (${it.previousApplicant})</a>
        </c:if>
        </div>

        <div class="float-right">
        <c:if test="${not empty it.nextApplication}">
            <a href="#" id="nextApplication">(${it.nextApplicant})&nbsp;Seuraava&nbsp;&gt;</a>
        </c:if>
        </div>

        <div class="align-center margin-auto width-25">
        <c:if test="${not empty it.selectedApplication}">
            ${it.currentApplication} / ${it.applicationCount}
        </c:if>
        </div>
    </div>

</div>
