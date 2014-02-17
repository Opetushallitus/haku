<%@ tag description="Set defalut selected value page scoped variable selected_value" body-content="empty"
        pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ tag trimDirectiveWhitespaces="true" %>
<%@ attribute name="element" required="true" type="fi.vm.sade.haku.oppija.lomake.domain.elements.Element" %>

<c:set var="tmp" value="${answers[element.id]}" scope="page"/>
<c:if test="${tmp eq null && element.defaultValueAttribute != null && (not (requestScope[element.defaultValueAttribute] eq null))}">
    <c:set var="tmp" value="${fn:toUpperCase(requestScope[element.defaultValueAttribute])}"/>
</c:if>

<c:if test="${tmp eq null}">
    <c:forEach var="option" items="${element.options}">
        <c:if test="${option.defaultOption}">
            <c:set var="tmp" value="${option.value}"/>
        </c:if>
    </c:forEach>
</c:if>
<c:set var="selected_value" value="${tmp}" scope="request"/>

