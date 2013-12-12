<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
    <c:set var="contextPath" value="${pageContext.request.contextPath}" scope="page"/>
<p>
    <a href="${contextPath}/lomake/${application.applicationSystemId}/tulostus/${application.oid}" class="button small print" target="_blank">
    	<span>
    		<span>
    			<haku:i18nText value="${element.i18nText}"/>
    		</span>
    	</span>
    </a>
</p>