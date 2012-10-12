<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<c:forEach var="child" items="${additionalQuestions}">
    <c:set var="element" value="${child}" scope="request"/>
    <jsp:include page="elements/${child.type}.jsp"/>
</c:forEach>