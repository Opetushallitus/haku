<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>

<haku:messages messages="${errorMessages}" additionalClass="warning" form="${it.form}"/>
<c:choose>
    <c:when test="${preview}">
        <c:set var="virkailijaPreview" value="true" scope="request"/>
        <div class="form">
            <c:forEach var="child" items="${form.children}">
                <c:set var="element" value="${child}" scope="request"/>
                <jsp:include page="../elements/${child.type}Preview.jsp"/>
            </c:forEach>
            <jsp:include page="./notes.jsp"/>
        </div>
    </c:when>
    <c:otherwise>
        <c:set var="virkailijaEdit" value="true" scope="request"/>
        <form id="form-${it.element.id}" class="form" method="post" novalidate="novalidate">
            <button class="save" name="phaseId" type="submit" value="${it.element.id}">
                <span><fmt:message key="lomake.button.save"/></span>
            </button>
            <c:forEach var="child" items="${it.element.children}">
                <c:set var="element" value="${child}" scope="request"/>
                <jsp:include page="../elements/${child.type}.jsp"/>
            </c:forEach>
            <button class="save" name="phaseId" type="submit" value="${it.element.id}">
                <span><fmt:message key="lomake.button.save"/></span>
            </button>
        </form>
    </c:otherwise>
</c:choose>

<hr/>
