<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>

<c:set var="usePriorities" value="${element.usePriorities}" scope="request"/>
<c:forEach var="child" items="${element.children}" varStatus="status">
    <c:set var="element" value="${child}" scope="request"/>
    <c:set var="index" value="${status.index + 1}" scope="request"/>
    <jsp:include page="${child.type}Preview.jsp"/>
</c:forEach>
