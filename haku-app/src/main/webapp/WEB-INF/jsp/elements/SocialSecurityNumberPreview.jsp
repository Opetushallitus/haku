<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags"%>

<c:set var="ssnElement" value="${element}"/>

<c:set var="element" value="${ssnElement.ssn}" scope="request"/>
<jsp:include page="/WEB-INF/jsp/elements/TextQuestionPreview.jsp"/>

<tr>
    <td class="label"><haku:i18nText value="${ssnElement.sexI18nText}"/></td>
    <td>
        <c:if test="${categoryData[ssnElement.sexId] eq ssnElement.maleOption.value}">
            <haku:i18nText value="${ssnElement.maleOption.i18nText}"/>
        </c:if>
        <c:if test="${categoryData[ssnElement.sexId] eq ssnElement.femaleOption.value}">
            <haku:i18nText value="${ssnElement.femaleOption.i18nText}"/>
        </c:if>
    </td>
</tr>
