<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setBundle basename="messages" scope="application"/>
<c:set var="nimikeKoodi" value="${answers[element.referedId]}" />
<c:if test="${empty nimikeKoodi}">
    <c:set var="nimikeKoodi" value="nimikeFallback" />
</c:if>

<tr>
    <td class="label"><a name="${element.id}"></a>
        <c:choose>
            <c:when test="${element.inline or print}">
                <fmt:message key="lomake.component.gradeaverage.titlePrefix"/>
            </c:when>
            <c:otherwise>
                <span><fmt:message key="lomake.component.gradeaverage.titlePrefix"/>:</span>
            </c:otherwise>
        </c:choose>
    </td>
    <td>
        <haku:i18nText value="${element.ammattitutkintonimikkeet[nimikeKoodi].i18nText}" />
    </td>
</tr>
<haku:viewChilds element="${element}"/>
