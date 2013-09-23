<%@ tag description="info row" body-content="empty" pageEncoding="UTF-8" %>
<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ attribute name="key" required="true" type="java.lang.String" %>
<%@ attribute name="value" required="true" %>
<%@ attribute name="cellId" required="false" type="java.lang.String" %>
<td <c:if test="${not empty cellId}">id="<c:out value="${cellId}"/>"</c:if>>
    <span class="bold"><fmt:message key="${key}"/>: </span><c:out value="${value}" escapeXml="true"/>
</td>
