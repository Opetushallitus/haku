<%@ tag description="Creates subject rows" body-content="empty" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<%@ tag trimDirectiveWhitespaces="true" %>

<%@ attribute name="id" required="true" type="java.lang.String" %>
<%@ attribute name="items" required="true" type="java.util.List" %>
<%@ attribute name="data" required="true" type="java.util.Map" %>
<select id="${id}" name="${id}" required="required">
    <option></option>
    <c:forEach var="item" items="${items}">
        <option value="${item.value}"
            ${(data[id] eq item.value) ? "selected=\"selected\"" : ""}>
            <haku:i18nText value="${item.i18nText}"/></option>
    </c:forEach>
</select>
<haku:errorMessage id="${id}"/>
