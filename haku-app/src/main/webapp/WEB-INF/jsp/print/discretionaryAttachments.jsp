<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

 <c:if test="${not empty discretionaryAttachments}">
    <section id="discretionaryAttachments">
        <hr>
        <h3><fmt:message key="lomake.tulostus.liitteet"/></h3>
        <p><fmt:message key="lomake.tulostus.liitteet.harkinnanvaraisuus"/></p>
        <div id="discretionaryAttachmentAddresses">
			<c:forEach var="discretionaryAttachment" items="${discretionaryAttachments}" varStatus="status">
				<c:if test="${not empty discretionaryAttachment.attachmentDeliveryAddress}">
					<address>
						<c:if test="${(not empty discretionaryAttachment.provider) && (not empty discretionaryAttachment.provider.name)}">
							<c:out value="${discretionaryAttachment.provider.name}"/><br/>
						</c:if>
						<c:if test="${not empty discretionaryAttachment.attachmentDeliveryAddress.streetAddress}">
							<c:out value="${discretionaryAttachment.attachmentDeliveryAddress.streetAddress}"/><br/>
						</c:if>
						<c:if test="${not empty discretionaryAttachment.attachmentDeliveryAddress.streetAddress2}">
							<c:out value="${discretionaryAttachment.attachmentDeliveryAddress.streetAddress2}"/><br/>
						</c:if>
						<c:if test="${not empty discretionaryAttachment.attachmentDeliveryAddress.postalCode}">
							<c:out value="${discretionaryAttachment.attachmentDeliveryAddress.postalCode}"/><br/>
						</c:if>
						<c:if test="${not empty discretionaryAttachment.attachmentDeliveryAddress.postOffice}">
							<c:out value="${discretionaryAttachment.attachmentDeliveryAddress.postOffice}"/>
						</c:if>
						<c:if test="${not empty discretionaryAttachment.attachmentDeliveryDeadline}">
							<br/>
							<fmt:message key="lomake.tulostus.liitteet.deadline"/>&nbsp;
							<fmt:formatDate value="${discretionaryAttachment.attachmentDeliveryDeadline}" pattern="dd.M.yyyy"/>
						</c:if>
					</address>
					<br/>
				</c:if>
			</c:forEach>
		</div>
    </section>
 </c:if>
