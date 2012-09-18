<%@ taglib uri="http://java.sun.com/jsp/jstl/core"
           prefix="c" %>

<fieldset>
    <legend><c:out value="${element.title}"/></legend>
    <c:forEach var="option" items="${element.options}" varStatus="status">
        <c:set var="id" value="${element.id}.${element.id}"/>
        <input  type="radio" name="${element.id}" value="${option.value}" ${(categoryData[element.id] eq option.value) ? "checked=\"checked\"" : ""} ${option.attributeString}/>${option.title}<br />
    </c:forEach>
</fieldset>
