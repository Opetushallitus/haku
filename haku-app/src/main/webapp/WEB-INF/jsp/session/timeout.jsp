<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script src="${contextPath}/resources/javascript/sessiontimeout.js" type="text/javascript"></script>

<fmt:setBundle basename="messages" scope="application"/>

<div id="overlay-fixed">
    <div id="timeout-banner-expiring" class="notification warning timeout-banner">
        <h1><fmt:message key="session.vanhentumassa.label"/></h1>
        <strong><fmt:message key="session.vanhentumassa.text"/></strong><br/>
        <button type="button" onclick="refreshSession('${contextPath}')"><fmt:message key="session.vanhentumassa.button"/></button><br/>
    </div>

    <div id="timeout-banner-expired" class="notification warning timeout-banner">
        <h1><fmt:message key="session.vanhentunut.label"/></h1>
        <strong><fmt:message key="session.vanhentunut.text"/></strong>
    </div>
</div>