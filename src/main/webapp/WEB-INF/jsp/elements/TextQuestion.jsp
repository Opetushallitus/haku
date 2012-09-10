<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<label for="${element.id}">${element.title}</label>

<input type="text" id="${element.id}" ${link.attributeString}"/>

<div>${element.help}</div>