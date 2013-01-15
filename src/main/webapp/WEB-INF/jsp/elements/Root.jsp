<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="element" value="${it.element}" scope="request"/>
<c:set var="categoryData" value="${it.categoryData}" scope="request"/>
<c:set var="form" value="${it.form}" scope="request"/>
<c:set var="hakemusId" value="${it.hakemusId}" scope="request"/>
<jsp:include page="./${it.template}.jsp"/>