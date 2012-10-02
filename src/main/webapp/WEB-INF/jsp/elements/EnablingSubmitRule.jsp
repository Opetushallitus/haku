<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="container_${element.id}">
       <script language="javascript">
                $(document).ready(haku.triggerRule(${element.id}));
       </script>
    <c:set var="key" value="${element.id}"/>
    <c:choose>
        <c:when test="${categoryData[key] != null}">

            <c:set var="child" value="${element.related[key]}"/>
            <c:set var="element" value="${element.related[key]}" scope="request"/>
            <jsp:include page="${child.type}.jsp"/>
        </c:when>
        <c:otherwise>

        <noscript>
            <input type="submit" id="enabling-submit" name="enabling-submit" value="Ok"/>
        </noscript>
        </c:otherwise>

    </c:choose>

</div>
