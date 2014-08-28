<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:if test="${not empty higherEducationAttachments}">
    <section id="${element.id}">
    <h3><haku:i18nText value="${element.i18nText}" /></h3>

        <c:forEach items="${higherEducationAttachments}" var="entry">
            <p><haku:i18nText value="${element.attachmentNotes[entry.key]}" /></p>
            <c:if test="${not empty entry.value}">
                <c:forEach items="${entry.value}" var="ao">
                    <haku:applicationOffice ao="${ao}" />
                </c:forEach>
            </c:if>
        </c:forEach>
    </section>
</c:if>
