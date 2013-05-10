<%@tag description="popup" body-content="empty" pageEncoding="UTF-8" %>
<%@ taglib prefix="fn" uri="/WEB-INF/tld/functions.tld" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@attribute name="element" required="true" type="fi.vm.sade.oppija.lomake.domain.elements.Element" %>
<%@ tag trimDirectiveWhitespaces="true" %>
<c:if test="${not empty element.popup}">
    <jsp:include page="/WEB-INF/jsp/elements/${element.popup.type}.jsp"/>
</c:if>
