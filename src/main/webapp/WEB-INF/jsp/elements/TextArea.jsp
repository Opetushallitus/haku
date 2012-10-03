<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<label>${element.title}</label>
<div>
    <textarea id="${element.id}" name="${element.id}" ${element.attributeString}><c:out value="${categoryData[element.id]}"/></textarea>
</div>
<div class="ehelp">${element.help}</div>
