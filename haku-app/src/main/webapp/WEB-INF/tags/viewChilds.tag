<%@ tag description="viewChilds" body-content="empty" pageEncoding="UTF-8" %>
<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="fn" uri="/WEB-INF/tld/functions.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="element" required="true" type="fi.vm.sade.haku.oppija.lomake.domain.elements.Element" %>
<c:forEach var="child" items="${fn:children(element, answers)}">
    <c:set var="parent" value="${element}" scope="request"/>
    <c:set var="element" value="${child}" scope="request"/>
    <c:if test="${parent.type eq 'Phase'}">
        <c:set var="currentPhase" value="${parent}" scope="request"/>
    </c:if>
    <c:choose>
        <c:when test="${preview or print}">
            <jsp:include page="/WEB-INF/jsp/elements/${child.type}Preview.jsp"/>
        </c:when>
        <c:when test="${complete}">
            <jsp:include page="/WEB-INF/jsp/valmis/${child.type}.jsp"/>
        </c:when>
        <c:otherwise>
            <jsp:include page="/WEB-INF/jsp/elements/${child.type}.jsp"/>
        </c:otherwise>
    </c:choose>
</c:forEach>
