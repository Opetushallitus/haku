<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags"%>

<%-- set education specific additional questions for this theme --%>
<c:set var="additionalQuestionList" value="${additionalQuestions[element.id]}" scope="request" />

<fieldset>
    <legend class="h3"><c:out value="${element.title}"/></legend>
    <hr/>
    <div class="theme-help">
    	<div class="help-text"><c:out value="${element.help}"/></div>
    	<a href="${category.id}/${element.id}/help" target="_blank" class="helplink">?</a>
    </div>
    <haku:viewChilds element="${element}"/>
</fieldset>
