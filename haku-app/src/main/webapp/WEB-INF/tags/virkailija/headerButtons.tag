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
            <a href="${contextPath}/virkailija/hakemus/${application.oid}/" class="button small back"></a>
        </c:otherwise>
    </c:choose>
    <a href="#" class="button small disabled"><fmt:message key="virkailija.hakemus.vrk"/></a>
    <a href="#" class="button small disabled"><fmt:message key="virkailija.hakemus.tor"/></a>
    <c:choose>
        <c:when test="${!application.passive}">
            <a href="#" id="passivateApplication" data-po-show="confirmPassivation" class="button small "><fmt:message key="virkailija.hakemus.passivoi.hakemus"/></a>
        </c:when>
        <c:otherwise>
            <a href="#" class="button small disabled"><fmt:message key="virkailija.hakemus.passivoi.hakemus"/></a>
        </c:otherwise>
    </c:choose>
</div>
