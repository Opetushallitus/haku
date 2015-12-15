<%@ tag description="info row" body-content="empty" pageEncoding="UTF-8" %>
<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="f" uri="/WEB-INF/tld/functions.tld" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<%@ attribute name="key" required="true" type="java.lang.String" %>
<%@ attribute name="value" required="true" type="java.lang.String" %>
<%@ attribute name="id" required="false" type="java.lang.String" %>
<%@ attribute name="rootElement" required="false" type="fi.vm.sade.haku.oppija.lomake.domain.elements.Element" %>
<%@ attribute name="cellId" required="false" type="java.lang.String" %>


<c:if test="${not empty cellId}">
    <c:set var="tmpId" value="id='${cellId}'"/>
    <c:set var="tmpIdValue" value="id='_${cellId}'"/>
</c:if>
<td ${tmpId}>
    <c:if test="${not empty rootElement}">
        <c:set var="tmp_element" value="${f:findElementById(rootElement, id)}"/>
        <c:if test="${not empty tmp_element and not empty tmp_element.options}">
            <c:forEach var="option" items="${tmp_element.options}">
                <c:if test="${value eq option.value}">
                    <c:set var="optionValue" value="${option.i18nText.translations[requestScope['fi_vm_sade_oppija_language']]}"/>
                </c:if>
            </c:forEach>
        </c:if>
        <c:remove var="tmp_element"/>
    </c:if>
    <span class="bold"><fmt:message key="${key}"/>: </span><span ${tmpIdValue}><c:out value="${(optionValue != null) ? optionValue : value}"/></span>
</td>
<c:remove var="tmpId"/>
<c:remove var="optionValue"/>
<c:remove var="tmpIdValue"/>
