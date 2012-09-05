<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML>
<html>
    <head>
        <meta charset="utf-8" />
        <title>${data['name']}</title>
    </head>
    <body>
        <ul>
            <c:forEach var="category" items="${data['categories']}">
                <li><a href="${category['id']}">${category['id']}</a></li>
            </c:forEach>
        </ul>
    </body>
</html>
