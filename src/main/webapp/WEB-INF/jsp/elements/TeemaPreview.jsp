<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags"%>

<%-- set education specific additional questions for this theme --%>
<c:set var="additionalQuestionList" value="${additionalQuestions[element.id]}" scope="request" />

<fieldset>
    <legend class="h3"><c:out value="${element.title}"/></legend>
    <button class="set-right legend-align" type="submit" name="navigate" value="${element.id}">
        <span>
            <span>Muokkaa</span>
        </span>
    </button>
    <hr>
    <table class="form-summary-table">
        <tbody>
            <haku:viewChilds element="${element}"/>
        </tbody>
    </table>
</fieldset>
