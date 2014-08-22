<%@ tag description="Outputs elements help attribute" body-content="empty" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<%@ attribute name="ao" required="true" type="fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionDTO" %>
<%@ tag trimDirectiveWhitespaces="true" %>

<address>
    <c:if test="${(not empty ao.provider) && (not empty ao.provider.name)}">
        <c:out value="${ao.provider.name}"/><br/>
    </c:if>

    <c:choose>
        <c:when test="${not empty ao.attachmentDeliveryAddress}">

            <c:if test="${not empty ao.attachmentDeliveryAddress.streetAddress}">
                <c:out value="${ao.attachmentDeliveryAddress.streetAddress}"/><br/>
            </c:if>
            <c:if test="${not empty ao.attachmentDeliveryAddress.streetAddress2}">
                <c:out value="${ao.attachmentDeliveryAddress.streetAddress2}"/><br/>
            </c:if>
            <c:if test="${not empty ao.attachmentDeliveryAddress.postalCode}">
                <c:out value="${ao.attachmentDeliveryAddress.postalCode}"/><br/>
            </c:if>
            <c:if test="${not empty ao.attachmentDeliveryAddress.postOffice}">
                <c:out value="${ao.attachmentDeliveryAddress.postOffice}"/>
            </c:if>

        </c:when>
        <c:otherwise>
            <c:if test="${not empty ao.provider.applicationOffice.name}">
                <c:out value="${ao.provider.applicationOffice.name}"/><br/>
            </c:if>
            <c:if test="${not empty ao.provider.applicationOffice.postalAddress.streetAddress}">
                <c:out value="${ao.provider.applicationOffice.postalAddress.streetAddress}"/><br/>
            </c:if>
            <c:if test="${not empty ao.provider.applicationOffice.postalAddress.streetAddress2}">
                <c:out value="${ao.provider.applicationOffice.postalAddress.streetAddress2}"/><br/>
            </c:if>
            <c:if test="${not empty ao.provider.applicationOffice.postalAddress.postalCode}">
                <c:out value="${ao.provider.applicationOffice.postalAddress.postalCode}"/><br/>
            </c:if>
            <c:if test="${not empty ao.provider.applicationOffice.postalAddress.postOffice}">
                <c:out value="${ao.provider.applicationOffice.postalAddress.postOffice}"/>
            </c:if>
        </c:otherwise>
    </c:choose>

    <c:if test="${not empty ao.attachmentDeliveryDeadline}">
        <br/>
        <fmt:message key="lomake.tulostus.liitteet.deadline"/>&nbsp;
        <fmt:formatDate value="${ao.attachmentDeliveryDeadline}" pattern="dd.M.yyyy"/>
    </c:if>
</address>
<br/>