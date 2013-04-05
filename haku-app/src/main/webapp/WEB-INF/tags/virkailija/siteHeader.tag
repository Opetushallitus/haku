<%@ tag description="breadcrumbs" body-content="empty" pageEncoding="UTF-8" %>
<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="page"/>
<header id="siteheader">
    <div class="primarylinks">
        <a href="${contextPath}"><fmt:message key="virkailija.haku.oppijan.verkkopalvelu"/></a> &nbsp;
        <a href="${contextPath}/virkailija/hakemus"><fmt:message key="virkailija.haku.virkailijan.tyopoyta"/></a>
    </div>
    <div class="secondarylinks">
        <a href="${contextPath}/user/logout"><fmt:message key="virkailija.haku.kirjaudu.ulos"/></a> &nbsp;
        <a href="${contextPath}/j_spring_cas_security_logout"><fmt:message key="virkailija.haku.kirjaudu.ulos.cas"/></a>
        &nbsp;
        <a href="#"><fmt:message key="virkailija.haku.omat.tiedot"/></a> &nbsp;
        <a href="#"><fmt:message key="virkailija.haku.viestinta"/></a> &nbsp;
        <a href="#"><fmt:message key="virkailija.haku.asiakaspalvelu"/></a> &nbsp;
        <a href="#"><fmt:message key="virkailija.haku.tukipalvelut"/></a>
    </div>
</header>
