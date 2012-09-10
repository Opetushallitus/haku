<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<fieldset>
    <legend><c:out value="${element.title}"/></legend>
    <c:forEach var="child" items="${element.children}">
        <c:set var="element" value="${child}" scope="request"/>
        <jsp:include page="${child.type}.jsp"/>
    </c:forEach>
</fieldset>