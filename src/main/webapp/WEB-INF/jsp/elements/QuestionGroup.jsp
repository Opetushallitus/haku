<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags"%>
<fieldset>
    <legend class="h3"><c:out value="${element.title}"/></legend>
    <hr/>
    <a href="#" class="helplink">?</a>
    <div><c:out value="${element.help}"/></div>
    <haku:viewChilds element="${element}"/>
</fieldset>
