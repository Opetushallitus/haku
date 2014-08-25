<%@ page session="false"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
    <c:set var="contextPath" value="${pageContext.request.contextPath}" scope="page"/>
<p>
    <a href="${contextPath}/lomake/${application.applicationSystemId}/pdf/${application.oid}" class="button small print" target="_blank">
    	<span>
    		<span>
    			<haku:i18nText value="${element.i18nText}"/>
    		</span>
    	</span>
    </a>
</p>
