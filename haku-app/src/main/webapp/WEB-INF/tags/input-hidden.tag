<%@ tag description="Simple hidden input" body-content="empty" pageEncoding="UTF-8" %>
<%@ tag trimDirectiveWhitespaces="true" %>
<%@ attribute name="id" required="true" type="java.lang.String" %>
<%@ attribute name="data" required="true" type="java.util.Map" %>
<input id="${id}" name="${id}" value="${data[id]}" type="hidden"/>
