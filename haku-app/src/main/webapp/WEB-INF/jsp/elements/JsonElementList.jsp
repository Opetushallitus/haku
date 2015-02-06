<%@ page session="false"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="f" uri="/WEB-INF/tld/functions.tld" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://www.atg.com/taglibs/json" prefix="json" %>

<c:set var="answers" value="${it.answers}" scope="request"/>
<c:set var="form" value="${it.form}" scope="request"/>
<c:set var="applicationSystemId" value="${it.applicationSystemId}" scope="request"/>
<json:array name="items" var="item">
<c:forEach var="element" items="${it.elements}" varStatus="loop">
    <json:object>
        <json:property name="id" value="${element.id}"/>
        <json:property name="html" escapeXml="false">
            <c:set var="element" value="${element}" scope="request"/>
            <jsp:include page="./${element.type}.jsp"/>
        </json:property>
    </json:object>
</c:forEach>
</json:array>
