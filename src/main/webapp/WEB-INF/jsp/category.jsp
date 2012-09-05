<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML>
<html>
    <head>
        <meta charset="utf-8" />
        <title>${formModel.currentCategoryId}</title>
    </head>
    <body>
        <div>
             <c:forEach var="categoryLink" items="${formModel.categoryLinks}">
                 <a href="${categoryLink.ref}">${categoryLink.label}</a>&nbsp;
            </c:forEach>
        </div>

        <div>
            <c:forEach var="question" items="${formModel.currentCategory['questions']}">
                <c:choose>
                    <c:when test="${question['type'] eq 'INPUT'}">
                        <label for="${question['id']}">${question['label']}</label>
                        <input type="text" name="firstname" />
                        <div>${question['help_text']}</div>
                    </c:when>
                </c:choose>
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
