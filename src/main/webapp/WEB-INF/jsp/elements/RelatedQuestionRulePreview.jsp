<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="/WEB-INF/tags/functions.tld"%>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags"%>
<div class="container_${element.id}">
    <c:set var="key" value="${element.relatedElementId}"/>
    <c:choose>
        <c:when test="${not empty categoryData[key]}">
            <c:if test="${fn:evaluate(categoryData[key], element.expression)}">
                <haku:viewChilds element="${element}"/>
            </c:if>
        </c:when>
        <c:otherwise>

        <noscript>
            <input type="submit" id="enabling-submit" name="enabling-submit" value="Ok"/>
        </noscript>
        </c:otherwise>

    </c:choose>

</div>
