<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:if test="${not empty higherEducationAttachments}">
    <fmt:setBundle basename="form_common" var="form_common" />
    <h3><fmt:message bundle="${form_common}" key="form.valmis.todistus.otsikko" /></h3>

        <c:forEach items="${higherEducationAttachments}" var="entry">
            <fmt:message bundle="${form_common}" key="form.valmis.todistus.${entry.key}" />
            <c:if test="${not empty entry.value}">
                <c:forEach items="${entry.value}" var="ao">
                    <haku:applicationOffice ao="${ao}" />
                </c:forEach>
            </c:if>
        </c:forEach>
    </section>
</c:if>
