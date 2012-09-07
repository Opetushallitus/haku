<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="node" value="${element}" scope="page"/>
<jsp:include page="elements/${node.type}.jsp">
    <jsp:param name="e" value="${node}"/>
</jsp:include>

