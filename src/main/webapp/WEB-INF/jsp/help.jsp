<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
    <head>
    <title>Opetushallitus </title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <link href="/haku/resources/css/screen.css" type="text/css" rel="stylesheet" />
    </head>
    <body>
        <div id="viewport">
            <div id="overlay"></div>
            <div id="help-page">
                <section id="page">
                    <h1><c:out value="${themeTitle}"/></h1>

                    <c:forEach var="entry" items="${themeHelpMap}">
                        <h3><c:out value="${entry.key}"/></h3>
                        <p><c:out value="${entry.value}"/></p>

                    </c:forEach>
                </section>
            </div>
        </div>
    </body>
</html>