<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<label>${element.title}</label>
<div>
    <textarea ${element.attributeString}><c:out value="${categoryData[element.id]}"/></textarea>
    <div><span class="required_field">${errorMessages[element.id]}</span></div>
</div>
<div class="ehelp">${element.help}</div>
