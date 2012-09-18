<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<fieldset>
    <legend><c:out value="${element.title}"/></legend>
    <c:set var="id" value="${parentId}.${element.id}"/>
    <input type="file" id="${id}" ${element.attributeString} />
</fieldset>
