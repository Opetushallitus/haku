<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag description="Simple hidden input" body-content="empty" pageEncoding="UTF-8" %>
<%@ tag trimDirectiveWhitespaces="true" %>
<%@ attribute name="id" required="true" type="java.lang.String" %>
<%@ attribute name="data" required="true" type="java.util.Map" %>
<input id="${id}" name="${id}" value="<c:out value='${data[id]}'/>" type="hidden"/>
