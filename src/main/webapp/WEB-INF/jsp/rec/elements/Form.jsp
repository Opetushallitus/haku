<html>
    <head>
        <meta charset="utf-8" />
        <title>${formModel['title']}</title>
    </head>
    <body>
        <div>
             <c:forEach var="categoryLink" items="${formModel['categoryLink']}">
                 <a href="${categoryLink.ref}">${categoryLink.label}</a>&nbsp;
            </c:forEach>
        </div>

        <div>
            <c:forEach var="child" items="${formModel['children']}">
                <jsp:include page="elements/${child[type]}.jsp">
	                <jsp:param name="model" value="${child}" />
                    <jsp:param name="parentId" value="${formModel['id']}" />
                </jsp:include>
            </c:forEach>
        </div>

        <div>
            <c:choose>
                <c:when test="${formModel.prevAvailable}">
                    <a href="${formModel.prev.ref}">Edellinen</a>
                </c:when>
            </c:choose>
            <c:choose>
                <c:when test="${formModel.nextAvailable}">
                    <a href="${formModel.next.ref}">Seuraava</a>
                </c:when>
            </c:choose>
        </div>
    </body>
</html>