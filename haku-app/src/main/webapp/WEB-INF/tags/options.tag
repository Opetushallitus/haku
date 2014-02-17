<%@ tag description="Iterate option items" body-content="empty" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<%@ tag trimDirectiveWhitespaces="true" %>
<%@ attribute name="options" type="java.util.Collection" %>

<c:forEach var="option" items="${options}">
    <haku:option option="${option}" selectedValue="${selected_value}"/>
</c:forEach>
