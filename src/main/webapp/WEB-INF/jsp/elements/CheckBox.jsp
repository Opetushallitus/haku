<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<fieldset class="question">
    <legend><c:out value="${element.title}"/></legend>

    <c:forEach var="option" items="${element.options}">
        <div>${errors[option.id]}</div>
        <input type="checkbox" name="${option.id}" value="${option.value}" ${(categoryData[option.id] eq option.value) ? "checked=\"checked\"" : ""} ${option.attributeString}/>${option.title}<br />
    </c:forEach>
    
</fieldset>
