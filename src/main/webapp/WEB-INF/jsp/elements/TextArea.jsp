<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<label>${element.title}</label>

<div>
    <textarea id="${element.id}" name="${element.id}" ${element.attributeString}><c:out value="${formData[element.id]}"/></textarea>
</div>
<div>${element.help}</div>
