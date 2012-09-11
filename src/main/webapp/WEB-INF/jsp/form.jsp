<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE HTML>
<html>
    <head>
        <meta charset="utf-8" />
        <link rel="stylesheet" href="/haku/resources/css/styles.css" type="text/css">
        <title>${form.title}</title>
    </head>
    <body>

        <div>
             <c:forEach var="link" items="${form.navigation.children}">
                 <a ${link.attributeString}>${link.value}</a>&nbsp;
            </c:forEach>
        </div>
        <form method="post">
            <div>

                <c:forEach var="child" items="${category.children}">
                    <c:set var="element" value="${child}" scope="request"/>
                    <jsp:include page="elements/${child.type}.jsp"/>
                </c:forEach>

            </div>
            <div>
                <c:choose>
                    <c:when test="${category.hasPrev}">
                        <input type="submit" value="Edellinen" />
                    </c:when>
                </c:choose>
                <c:choose>
                    <c:when test="${category.hasNext}">
                        <input type="submit" value="Seuraava" />
                    </c:when>
                     <c:when test="${!category.hasNext}">
                        <input type="submit" value="Tallenna" />
                    </c:when>
                </c:choose>
            </div>
        </form>


    </body>
</html>
