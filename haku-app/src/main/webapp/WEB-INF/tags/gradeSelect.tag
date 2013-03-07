<%@ tag body-content="empty" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<%@ tag trimDirectiveWhitespaces="true" %>

<%@ attribute name="id" required="true" %>
<%@ attribute name="options" required="true" type="java.util.List" %>
<%@ attribute name="data" required="true" type="java.util.Map" %>
<%@ attribute name="showEmptyOption" required="false" %>
<%@ attribute name="preview" required="false" %>

<c:if test="${not preview}">
    <div class="field-container-select">
        <select id="${id}" name="${id}" required="required">
            <c:if test="${showEmptyOption}">
                <option></option>
            </c:if>
            <c:forEach var="option" items="${options}">
                <option value="${option.value}" ${(data[id] eq option.value) ? "selected=\"selected\"" : ""}>
                    <haku:i18nText value="${option.i18nText}"/></option>
            </c:forEach>
        </select>
        <haku:errorMessage id="${id}"/>
    </div>
</c:if>
<c:if test="${preview}">
    <c:forEach var="option" items="${options}">
        <c:if test="${(data[id] eq option.value)}">
            <haku:i18nText value="${option.i18nText}"/>
        </c:if>
    </c:forEach>
</c:if>
