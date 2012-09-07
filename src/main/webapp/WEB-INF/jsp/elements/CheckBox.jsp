<%@ taglib uri="http://java.sun.com/jsp/jstl/core"
           prefix="c" %>


<fieldset>
    <legend><c:out value="${element.title}"/></legend>
    <c:forEach var="option" items="${element.options}">
        <input type="checkbox" name="${element.title}" value="${option.value}"/>${option.title}<br />
    </c:forEach>
</fieldset>