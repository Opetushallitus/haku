<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<fieldset>
    <legend><c:out value="${element.title}"/></legend>
    <c:forEach var="option" items="${element.options}">
        <c:set value="${element.id}.${option.id}" var="optionId" scope="page"/>
        <input type="checkbox" name="${optionId}" value="${option.value}" ${(categoryData[optionId] eq option.value) ? "checked=\"checked\"" : ""} ${element.attributeString}/>${option.title}<br />
    </c:forEach>
</fieldset>
