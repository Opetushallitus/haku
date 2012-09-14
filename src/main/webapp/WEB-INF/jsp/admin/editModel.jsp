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
        <form method="post" enctype="multipart/form-data">
            <div>


                    <textarea id="model" rows="200" cols="50">
                    ${model}
                    </textarea>

            </div>
            <div>

                        <input type="submit" value="Tallenna" />

            </div>
        </form>


    </body>
</html>
