<%@ tag description="Theme's edit button" body-content="empty" pageEncoding="UTF-8" %>
<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="url" required="true" type="java.lang.String" %>
<%@ attribute name="application" required="true" type="fi.vm.sade.haku.oppija.hakemus.domain.Application" %>
<c:if test="${application.state eq null or application.state eq 'SUBMITTED' or application.state eq 'ACTIVE' or application.state eq 'INCOMPLETE' or application.state eq 'DRAFT'}">
    <a href="${url}">
        <button class="float-right edit-link">
            <span><span><fmt:message key="virkailija.lisakysymys.muokkaa"/></span></span>
        </button>
    </a>
</c:if>
