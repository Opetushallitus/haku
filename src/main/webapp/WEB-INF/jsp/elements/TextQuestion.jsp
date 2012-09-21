<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class=question>
    <label id="label-${element.id}" for="${element.id}">${element.title}</label>

    <input ${element.attributeString} value="${categoryData[element.id]}"/><span class="required_field">${errors[element.id]}</span>

    <div class="ehelp" id="help-${element.id}">${element.help}</div>
</div>
