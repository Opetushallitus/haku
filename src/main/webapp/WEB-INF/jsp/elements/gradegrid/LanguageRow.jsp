<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:out value="${language.title}"/>
<select ${language.attributeString}>
    <option></option>
    <c:forEach var="option" items="${element.languageOptions}">
        <c:set value="${language.id}.${option.id}" var="optionId" scope="page"/>
        <option name="${optionId}"
                value="${option.value}" ${option.attributeString}
                    ${(categoryData[language.id] eq option.value) ? "selected=\"selected\"" : ""}>${option.title}</option>
    </c:forEach>
</select>