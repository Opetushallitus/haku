<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML>
<html>
    <head>
        <meta charset="utf-8" />
        <title>${formModel.currentCategoryId}</title>
    </head>
    <body>
        <div>

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
            <a href="${formModel.prev}">Edellinen</a>
            <a href="${formModel.next}">Seuraava</a>
        </div>
    </body>
</html>
