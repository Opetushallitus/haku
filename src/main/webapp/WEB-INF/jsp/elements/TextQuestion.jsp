<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="id" value="${parentId}.${element.id}"/>

<label id="label-${id}" for="${id}">${element.title}</label>

<input type="text" id="${id}" ${element.attributeString}/>

<div id="help-${id}">${element.help}</div>
<!-- <span class="help">${element.help}</span> -->
