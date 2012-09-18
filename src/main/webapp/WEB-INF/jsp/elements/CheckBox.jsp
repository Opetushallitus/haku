<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<fieldset>
    <legend><c:out value="${element.title}"/></legend>
    <c:forEach var="option" items="${element.options}">
        <input type="checkbox" name="${option.id}" value="${option.value}" ${(categoryData[optionId] eq option.value) ? "checked=\"checked\"" : ""} ${option.attributeString}/>${option.title}<br />
    </c:forEach>
</fieldset>
