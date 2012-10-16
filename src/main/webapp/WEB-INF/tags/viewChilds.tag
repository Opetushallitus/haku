<%@tag description="viewChilds" body-content="empty" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@attribute name="element" required="true" type="fi.vm.sade.oppija.haku.domain.elements.Element"%>

<c:forEach var="child" items="${element.children}">
    <c:set var="parent" value="${element}" scope="request"/>
    <c:set var="element" value="${child}" scope="request"/>
    <jsp:include page="/WEB-INF/jsp/elements/${child.type}.jsp"/>
</c:forEach>