<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:forEach var="grade" items="${element.gradeRange}">
    <c:if test="${(categoryData[gradeSelectId] eq grade.value)}">
        <c:out value="${grade.title}"/>
    </c:if>
</c:forEach>