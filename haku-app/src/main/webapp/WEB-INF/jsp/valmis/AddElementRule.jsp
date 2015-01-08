<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
AERValmis
<c:choose>
    <c:when test="${not empty answers[element.relatedElementId]}">
        <haku:viewChilds element="${element}"/>
    </c:when>
</c:choose>
