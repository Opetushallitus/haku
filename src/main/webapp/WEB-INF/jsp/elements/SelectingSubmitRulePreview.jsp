<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="haku" uri="/WEB-INF/tags/functions.tld"%>

<c:set var="savedValue" value="${element}"/>
<c:set var="element" value="${element.childById[element.id]}" scope="request"/>
<jsp:include page="${element.type}Preview.jsp"/>
<c:set var="element" value="${savedValue}"/>
<c:set var="key" value="${element.id}"/>
<c:choose>
    <c:when test="${not empty categoryData[key]}">
        <c:forEach var="rule" items="${element.expressions}">
            <c:if test="${haku:evaluate(categoryData[key], rule.value.regex)}">
                <c:set var="value" value="${rule.value.option.value}" scope="request"/>
                <c:set var="element" value="${element.childById[element.target]}" scope="request"/>
                <c:set var="disabled" value="true" scope="request"/>
                <jsp:include page="${element.childById[element.target].type}Preview.jsp">
                    <jsp:param name="element" value="${element.childById[rule.key]}"/>
                    <jsp:param name="value" value="${value}"/>
                </jsp:include>
            </c:if>
        </c:forEach>
    </c:when>
    <c:otherwise>
        <c:set var="element" value="${element.childById[element.target]}" scope="request"/>
        <jsp:include page="${element.childById[element.target].type}Preview.jsp"/>
    </c:otherwise>

</c:choose>
