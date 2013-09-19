<%@ tag description="Header button: VRK, TOR, etc." body-content="empty" pageEncoding="UTF-8" %>
<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ attribute name="oid" required="true" type="java.lang.String" %>
<%@ attribute name="preview" required="true" type="java.lang.Boolean" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="page"/>
<div class="grid16-16">
    <c:choose>
        <c:when test="${preview}">
            <a href="${contextPath}/virkailija/hakemus" class="button small back"></a>
        </c:when>
        <c:otherwise>
            <a href="${contextPath}/virkailija/hakemus/${oid}/" class="button small back"></a>
        </c:otherwise>
    </c:choose>
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
        <c:when test="${empty application.studentOid}">
            <a href="#" id="addStudentOid" data-po-show="addStudentOid" class="button small">
                <fmt:message key="virkailija.hakemus.lisaa.oppijanumero" />
            </a>
        </c:when>
    </c:choose>
    <a href="${contextPath}/virkailija/hakemus/${oid}/print" class="button small print" target="_blank"><fmt:message
    key="lomake.valmis.button.tulosta"/></a>
</div>
