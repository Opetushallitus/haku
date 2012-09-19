<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


<label for="label-${element.id}">${element.title}</label>

<select ${element.attributeString}>
    <c:forEach var="option" items="${element.options}">
        <c:set value="${element.id}.${option.id}" var="optionId" scope="page"/>
        <option name="${optionId}" value="${option.value}" ${(categoryData[element.id] eq option.value) ? "selected=\"selected\"" : ""} ${option.attributeString}>${option.title}</option>
    </c:forEach>
</select>

<div class="ehelp">${element.help}</div>
