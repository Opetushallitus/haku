<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags"%>
<c:forEach var="option" items="${element.options}">
    <c:if test="${(categoryData[element.id] eq option.value)}">
            <c:set var="title" value="${option.title}"/>
    </c:if>
</c:forEach>
<tr>
    <c:choose>
        <c:when test="${element.inline}">
            <td class="label"><c:out value="${element.title}"/></td>
            <td>
                <c:out value="${title}"/>
            </td>
        </c:when>
        <c:otherwise>
            <td><c:out value="${element.title}"/>:&nbsp;<c:out value="${title}"/></td>
        </c:otherwise>
    </c:choose>
</tr>
<haku:viewChilds element="${element}"/>




