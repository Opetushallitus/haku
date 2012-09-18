<%@ taglib uri="http://java.sun.com/jsp/jstl/core"
           prefix="c" %>

<fieldset>
    <legend><c:out value="${element.title}"/></legend>
    <c:forEach var="option" items="${element.options}" varStatus="status">
        <c:set var="id" value="${parentId}.${element.id}"/>
        <input type="radio" name="${id}" value="${option.value}" ${(formData[id] eq option.value) ? "checked=\"checked\"" : ""}/>${option.title}<br />
    </c:forEach>
</fieldset>
