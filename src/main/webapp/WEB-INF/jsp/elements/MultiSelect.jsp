<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="id" value="${parentId}.${element.id}"/>
<fieldset>
    <legend><c:out value="${element.title}"/></legend>
    <select multiple="multiple" name="${id}" id="${id}">
        <c:forEach var="option" items="${element.options}">
            <c:set value="${element.id}.${option.id}" var="optionId" scope="page"/>
            <option name="${optionId}" value="${option.value}">${option.title}</option>
        </c:forEach>
    </select>
</fieldset>
