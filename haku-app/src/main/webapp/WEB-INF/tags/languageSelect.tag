<%@ tag description="Outputs elements help attribute" body-content="empty" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<%@ tag trimDirectiveWhitespaces="true" %>

<%@ attribute name="language" required="true" type="fi.vm.sade.oppija.lomake.domain.elements.Element" %>
<%@ attribute name="options" required="true" type="java.util.List" %>
<%@ attribute name="data" required="true" type="java.util.Map" %>
<%@ attribute name="preview" required="false" %>
<c:if test="${not preview}">
    <haku:i18nText value="${language.i18nText}"/>&nbsp;
    <select ${language.attributeString} required="required">
        <option></option>
        <c:forEach var="option" items="${options}">
            <c:set value="${language.id}.${option.id}" var="optionId" scope="page"/>
            <option name="${optionId}"
                    value="${option.value}" ${option.attributeString}
                ${(data[language.id] eq option.value) ? "selected=\"selected\"" : ""}><haku:i18nText
                    value="${option.i18nText}"/></option>
        </c:forEach>
    </select>
</c:if>
<c:if test="${preview}">
    <haku:i18nText value="${language.i18nText}"/>&nbsp;
    <c:forEach var="option" items="${options}">
        <c:if test="${(data[language.id] eq option.value)}">
            <haku:i18nText value="${option.i18nText}"/>
        </c:if>
    </c:forEach>
</c:if>
