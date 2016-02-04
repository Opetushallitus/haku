<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<fmt:setBundle basename="messages" scope="application"/>

<div id="overlay-fixed">
    <div id="conflict-banner" class="notification warning timeout-banner">
        <h1><fmt:message key="virkailija.hakemus.konflikti.label"/></h1>
        <strong><fmt:message key="virkailija.hakemus.konflikti.text"/></strong><br/>
        <a href="${contextPath}/virkailija/hakemus/${oid}/" class="button small" onclick="return hideConflictBanner()"><fmt:message key="virkailija.hakemus.konflikti.button"/></a>
    </div>
</div>

<script type="text/javascript">
    function hideConflictBanner() {
        $('#overlay-fixed').hide();
        $('#conflict-banner').hide();
        return true;
    }
</script>