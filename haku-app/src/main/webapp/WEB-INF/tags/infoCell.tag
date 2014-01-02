<%@ tag description="info row" body-content="empty" pageEncoding="UTF-8" %>
<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ attribute name="key" required="true" type="java.lang.String" %>
<%@ attribute name="value" required="true" %>
<%@ attribute name="cellId" required="false" type="java.lang.String" %>
<c:if test="${not empty cellId}">
    <c:set var="tmpId" value="id='${cellId}'" />
    <c:set var="tmpIdValue" value="id='_${cellId}'" />
</c:if>
<td ${tmpId}>
    <span class="bold"><fmt:message key="${key}"/>: </span><span ${tmpIdValue}><c:out value="${value}" escapeXml="true"/></span>
</td>
<c:remove var="tmpId"/>
<c:remove var="tmpIdValue"/>
