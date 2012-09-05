<%@ taglib uri="http://java.sun.com/jsp/jstl/core"
  prefix="c" %>

<!DOCTYPE HTML>
<html>
  <head>
      <meta charset="utf-8" />
    <title>foo</title>
  </head>
  <body>
    <table>
      <c:forEach var="input" items="${questions}">
        <tr>
        <c:choose>
            <c:when test="${input['type'] == 'HELP_TEXT'}">
                <td colspan="2"><div id="${input['id']}">${input['description']}</div></td>
          </c:when>
          <c:otherwise>
                <td>${input['description']}</td>
                <td><input id="${input['id']}" type="${input['type']}" value=""/></td>
          </c:otherwise>

         </c:choose>
                </tr>
      </c:forEach>
    </table>
  </body>
</html>
