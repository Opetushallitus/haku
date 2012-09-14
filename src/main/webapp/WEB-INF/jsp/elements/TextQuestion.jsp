<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<label id="label-${element.id}" for="${element.id}">${element.title}</label>

<input type="text" id="${element.id}" ${element.attributeString}/>

<div id="help-${element.id}">${element.help}</div>
<!-- <span class="help">${element.help}</span> -->
