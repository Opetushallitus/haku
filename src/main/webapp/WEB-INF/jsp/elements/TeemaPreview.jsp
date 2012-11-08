<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags"%>

<%-- set education specific additional questions for this theme --%>
<c:set var="additionalQuestionList" value="${additionalQuestions[element.id]}" scope="request" />

<fieldset>
    <legend class="h3"><c:out value="${element.title}"/></legend>
    <hr>
    <c:forEach var="vaihe" items="${form.categories}">
        <c:if test="${(not vaihe.preview)}">
            <c:forEach var="teema" items="${vaihe.children}">
                <c:if test="${(teema.id eq element.id)}">
                    <form method="get" action="/haku/lomake/${hakemusId.applicationPeriodId}/${hakemusId.formId}/${vaihe.id}">
                        <button class="set-right legend-align" type="submit">
                            <span>
                                <span>Muokkaa</span>
                            </span>
                        </button>
                    </form>
                </c:if>
            </c:forEach>
        </c:if>
    </c:forEach>
    <table class="form-summary-table">
        <tbody>
            <haku:viewChilds element="${element}"/>
        </tbody>
    </table>
</fieldset>
