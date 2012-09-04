<%@ taglib uri="http://java.sun.com/jsp/jstl/core"
  prefix="c" %>
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
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