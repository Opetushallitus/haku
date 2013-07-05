<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<c:set var="show" value="false"/>
<c:forEach var="key" items="${element.aoEducationDegreeKeys}">
    <c:if test="${categoryData[key] eq element.requiredEducationDegree}">
        <c:set var="show" value="true"/>
    </c:if>
</c:forEach>
<c:if test="${show eq 'true'}">
    <jsp:include page="./ThemePrint.jsp"/>
</c:if>