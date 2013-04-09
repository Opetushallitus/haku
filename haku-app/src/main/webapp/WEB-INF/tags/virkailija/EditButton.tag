<%@ tag description="Theme's edit button" body-content="empty" pageEncoding="UTF-8" %>
<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ attribute name="url" required="true" type="java.lang.String" %>
<a href="${url}">
    <button class="float-right edit-link">
        <span><span><fmt:message key="virkailija.lisakysymys.muokkaa"/></span></span>
    </button>
</a>
