<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML>
<html>
    <head>
        <meta charset="utf-8" />
        <title>${category['id']}</title>
    </head>
    <body>
        <div>

        </div>

        <div>
            <c:forEach var="question" items="${category['questions']}">
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
            <a href="${prev}">Edellinen</a>
            <a href="${next}">Seuraava</a>
        </div>
    </body>
</html>
