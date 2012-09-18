<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<fieldset>
    <legend><c:out value="${element.title}"/></legend>
    <c:forEach var="option" items="${element.options}">
        <c:set value="${parentId}.${option.id}" var="id" scope="page"/>
        <input type="checkbox" name="${id}" value="${option.value}" ${(formData[id] eq option.value) ? "checked=\"checked\"" : ""} ${element.attributeString}/>${option.title}<br />
    </c:forEach>
</fieldset>
