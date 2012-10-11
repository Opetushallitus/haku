<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE HTML>
<html>
    <head>
        <META http-equiv="Content-Type" content="text/html;charset=UTF-8">
        <meta charset="utf-8" />
        <link rel="stylesheet" href="/haku/resources/css/styles.css" type="text/css">
        <title>admin</title>
    </head>
    <body>

        <div>
             <c:forEach var="link" items="${form.navigation.children}">
                 <a ${link.attributeString}>${link.value}</a>&nbsp;
            </c:forEach>
        </div>
        <form method="post" action="upload"accept-charset="utf-8" enctype="multipart/form-data">
            <div>

                    <c:set var="element" value="${attachment}" scope="request"/>
                    <jsp:include page="../elements/Attachment.jsp"/>

            </div>
            <div>

                        <input type="submit" name="tallenna" value="Tallenna" />

            </div>
        </form>
        <a href="/haku/admin">Takaisin</a>

    </body>
</html>
