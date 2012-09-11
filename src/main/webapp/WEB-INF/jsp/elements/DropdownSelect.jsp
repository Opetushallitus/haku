<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<label for="${element.id}">${element.title}</label>

<select name="${element.id}" ${link.attributeString}>
    <c:forEach var="option" items="${element.options}">
        <option value="${option.value}">${option.title}</option>
    </c:forEach>
</select>

<div>${element.help}</div>
