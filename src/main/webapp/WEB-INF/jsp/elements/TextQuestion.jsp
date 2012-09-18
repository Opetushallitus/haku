<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<label id="label-${element.id}" for="${element.id}">${element.title}</label>

<input ${element.attributeString} value="${categoryData[element.id]}"/>

<div id="help-${element.id}">${element.help}</div>
