<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set value="${parentId}.${element.id}" var="id" scope="page"/>

<label>${element.title}</label>
<div>
    <textarea id="${id}" name="${id}" ${element.attributeString}><c:out value="${formData[id]}"/></textarea>
</div>
<div>${element.help}</div>
