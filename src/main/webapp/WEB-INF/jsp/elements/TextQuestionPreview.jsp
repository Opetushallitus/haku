<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags"%>
<tr>
    <td class="label"><c:out value="${element.title}"/></td>
    <td><c:out value="${categoryData[element.id]}"/></td>
</tr>