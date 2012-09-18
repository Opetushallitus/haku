<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

 <c:set var="id" value="${parentId}.${element.id}"/>

<label for="${id}">${element.title}</label>

<select name="${id}" ${link.attributeString}>
    <c:forEach var="option" items="${element.options}">
        <option value="${option.value}">${option.title}</option>
    </c:forEach>
</select>

<div>${element.help}</div>
