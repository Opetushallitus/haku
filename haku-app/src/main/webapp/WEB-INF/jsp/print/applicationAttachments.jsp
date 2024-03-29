<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

 <c:if test="${not empty applicationAttachments}">
    <section id="applicationAttachments">
        <hr role="presentation">
        <h3><fmt:message key="lomake.tulostus.liitteet"/></h3>
        <table class="striped">
            <tr>
                <th><fmt:message key="lomake.tulostus.liite" /></th>
                <th><fmt:message key="lomake.tulostus.liite.toimitusosoite" /></th>
                <th><fmt:message key="lomake.tulostus.liite.deadline" /></th>
            </tr>
            <c:forEach var="attachment" items="${applicationAttachments}">
            <tr>
                <td>
                    <c:if test="${not empty attachment.name}">
                        <haku:i18nText value="${attachment.name}" escape="false"/><br/>
                    </c:if>
                    <c:if test="${not empty attachment.header}">
                        <haku:i18nText value="${attachment.header}" escape="false"/><br/>
                    </c:if>
                    <c:if test="${not empty attachment.description}">
                        <haku:i18nText value="${attachment.description}" escape="false" />
                    </c:if>
                </td>
                <td>
                    <c:if test="${(not empty attachment.address.recipient)}">
                        <c:out value="${attachment.address.recipient}"/><br/>
                    </c:if>
                    <c:if test="${not empty attachment.address.streetAddress}">
                        <c:out value="${attachment.address.streetAddress}"/><br/>
                    </c:if>
                    <c:if test="${not empty attachment.address.streetAddress2}">
                        <c:out value="${attachment.address.streetAddress2}"/><br/>
                    </c:if>
                    <c:if test="${not empty attachment.address.postalCode}">
                        <c:out value="${attachment.address.postalCode}"/><br/>
                    </c:if>
                    <c:if test="${not empty attachment.address.postOffice}">
                        <c:out value="${attachment.address.postOffice}"/><br/>
                    </c:if>
                    <c:if test="${not empty attachment.emailAddress}">
                        <c:out value="${attachment.emailAddress}"/>
                    </c:if>
                </td>
                <td>
                    <c:if test="${not empty attachment.deadline}">
                        <fmt:formatDate pattern="dd.MM.yyyy HH:mm. " value="${attachment.deadline}" />
                    </c:if>
                    <c:if test="${not empty attachment.deliveryNote}">
                        <haku:i18nText value="${attachment.deliveryNote}" escape="false"/>
                    </c:if>
                </td>
            </tr>
            </c:forEach>
        </table>
</c:if>
