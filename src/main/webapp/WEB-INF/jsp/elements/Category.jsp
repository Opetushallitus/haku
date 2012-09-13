<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<fieldset>
    <legend><c:out value="${element.title}"/></legend>
    <c:forEach var="child" items="${element.children}">
        <jsp:include page="./${child.type}.jsp">
            <jsp:param name="model" value="${child}"/>
            <jsp:param name="parentId" value="${element.id}"/>
        </jsp:include>
    </c:forEach>
</fieldset>
