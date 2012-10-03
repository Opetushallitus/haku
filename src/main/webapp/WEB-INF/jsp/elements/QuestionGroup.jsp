<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<fieldset>
    <legend class="h3"><c:out value="${element.title}"/></legend>
    <hr/>
    <a href="#" class="helplink">?</a>
    <div><c:out value="${element.help}"/></div>
    <c:forEach var="child" items="${element.children}">
        <c:set var="element" value="${child}" scope="request"/>
        <jsp:include page="${child.type}.jsp"/>
    </c:forEach>
</fieldset>
