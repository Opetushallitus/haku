<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<fieldset>
    <legend><c:out value="${element.title}"/></legend>
    <input type="file" id="${element.id}" ${element.attributeString} />
</fieldset>
