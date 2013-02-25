<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags"%>

<c:set var="ssnElement" value="${element}"/>

<c:set var="element" value="${ssnElement.ssn}" scope="request"/>
<jsp:include page="/WEB-INF/jsp/elements/TextQuestionPreview.jsp"/>

<c:set var="element" value="${ssnElement.sex}" scope="request"/>
<jsp:include page="/WEB-INF/jsp/elements/RadioPreview.jsp"/>