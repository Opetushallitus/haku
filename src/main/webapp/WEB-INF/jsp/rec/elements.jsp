<c:forEach var="child" items="${formModel['children']}">
    <jsp:include page="elements/${child[type]}.jsp">
        <jsp:param name="model" value="${child}"/>
        <jsp:param name="parentId" value="${formModel['id']}"/>
    </jsp:include>
</c:forEach>