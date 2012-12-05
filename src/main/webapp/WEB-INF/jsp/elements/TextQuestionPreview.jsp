<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags"%>
<tr>
    <c:choose>
        <c:when test="${element.inline}">
            <td class="label"><c:out value="${element.title}"/></td>
            <td><c:out value="${categoryData[element.id]}"/></td>
        </c:when>
        <c:otherwise>
            <td><c:out value="${element.title}"/>:&nbsp;<c:out value="${categoryData[element.id]}"/></td>
        </c:otherwise>
    </c:choose>
</tr>