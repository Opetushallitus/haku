<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<fmt:setBundle basename="messages" scope="application"/>

<fieldset id="${element.id}">
    <legend class="h3">
        <fmt:message key="lomake.component.gradeaverage.titlePrefix"/>:&nbsp;
        <c:set var="nimikekoodi" value="${answers[element.relatedNimikeId]}" />
        <c:if test="${empty nimikekoodi}">
            <c:set var="nimikekoodi" value="nimikeFallback" />
        </c:if>
        <c:choose>
            <c:when test="${nimikekoodi=='399999'}">
                <c:out value="${answers[element.relatedMuuNimike]}" />
            </c:when>
            <c:otherwise>
                <haku:i18nText value="${element.ammattitutkintonimikkeet[nimikekoodi].i18nText}" />
            </c:otherwise>
        </c:choose>

        <c:set var="oppilaitoskoodi" value="${fn:replace(answers[element.relatedOppilaitosId], '.', '_')}"/>
        <c:if test="${not empty oppilaitoskoodi}">
            <c:choose>
                <c:when test="${oppilaitoskoodi=='1_2_246_562_10_57118763579'}">
                    (<c:out value="${answers[element.relatedMuuOppilaitos]}" />)
                </c:when>
                <c:otherwise>
                    (<haku:i18nText value="${element.oppilaitokset[oppilaitoskoodi].i18nText}" />)
                </c:otherwise>
            </c:choose>
        </c:if>

    </legend>

    <haku:viewChilds element="${element}"/>

</fieldset>
