<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class=question>
    <label id="label-${element.id}" for="${element.id}">${element.title}</label>

    <input ${element.attributeString} value="${categoryData[element.id]}"/>

    <div class="ehelp" id="help-${element.id}">${element.help}</div>
</div>
