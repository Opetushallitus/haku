<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags"%>

<jsp:include page="/WEB-INF/jsp/elements/TextQuestionPreview.jsp"/>
<tr>
    <td class="label"><a name="${element.id}"></a><haku:i18nText value="${element.sexI18nText}"/></td>
    <td>
        <c:if test="${answers[element.sexId] eq element.maleOption.value}">
            <haku:i18nText value="${element.maleOption.i18nText}"/>
        </c:if>
        <c:if test="${answers[element.sexId] eq element.femaleOption.value}">
            <haku:i18nText value="${element.femaleOption.i18nText}"/>
        </c:if>
    </td>
</tr>
