<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="f" uri="/WEB-INF/tags/functions.tld"%>
<div class="container_${element.id}">
       <script language="javascript">
            if( $('#${element.id}') != 'undefined'){
                $(document).ready(haku.triggerRule(${element.id}));
            }
       </script>
    <c:set var="key" value="${element.id}"/>
    <c:choose>
        <c:when test="${!empty categoryData[key]}">
            <c:if test="${f:evaluate(categoryData[key], element.expression)}">
                <c:set var="child" value="${element.childById[key]}"/>
                <c:set var="element" value="${element.childById[key]}" scope="request"/>
                <jsp:include page="${child.type}.jsp"/>
            </c:if>
        </c:when>
        <c:otherwise>

        <noscript>
            <input type="submit" id="enabling-submit" name="enabling-submit" value="Ok"/>
        </noscript>
        </c:otherwise>

    </c:choose>

</div>
