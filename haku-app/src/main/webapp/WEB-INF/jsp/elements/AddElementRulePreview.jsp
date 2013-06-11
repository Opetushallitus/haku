<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<div class="container_${element.id}">
    <c:choose>
        <c:when test="${not empty categoryData[element.relatedElementId]}">
            <haku:viewChilds element="${element}"/>
        </c:when>
    </c:choose>
</div>
