<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="/WEB-INF/tld/functions.tld" %>

<c:set var="showTheme" value="${false}" />
<c:forEach var="child" items="${fn:allChildren(element)}">
    <c:if test="${!showTheme}">
        <c:set var="showTheme" value="${not empty categoryData[child.id]}" />
    </c:if>
</c:forEach>

<c:if test="${showTheme}">
    <section>
        <hr>
        <h3><haku:i18nText value="${element.i18nText}"/></h3>
        <table>
            <tbody>
                <haku:viewChilds element="${element}"/>
            </tbody>
        </table>
    </section>
</c:if>