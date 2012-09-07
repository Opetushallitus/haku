<fieldset>
    <legend>${element.title}</legend>
    <c:forEach var="child" items="${element.children}">
        <jsp:include page="./${child.type}.jsp">
            <jsp:param name="model" value="${child}"/>
            <jsp:param name="parentId" value="${element.id}"/>
        </jsp:include>
    </c:forEach>
</fieldset>
