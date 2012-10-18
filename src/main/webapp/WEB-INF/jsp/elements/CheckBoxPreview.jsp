<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags"%>
<tr>
    <td class="label"><c:out value="${element.title}"/></td>
    <td>
        <c:forEach var="option" items="${element.options}" varStatus="status">
            <c:if test="${(categoryData[option.id] eq option.value)}">
                    <c:out value="${option.title}"/>&nbsp;
            </c:if>
        </c:forEach>
    </td>
</tr>
<haku:viewChilds element="${element}"/>
