<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:out value="${language.title}"/>&nbsp;
<c:forEach var="option" items="${element.languageOptions}">
    <c:if test="${(categoryData[language.id] eq option.value)}">
        <c:out value="${option.title}"/>
    </c:if>
</c:forEach>