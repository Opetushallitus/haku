<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="virkailija" tagdir="/WEB-INF/tags/virkailija" %>

<%--
  ~ Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
  ~
  ~ This program is free software:  Licensed under the EUPL, Version 1.1 or - as
  ~ soon as they will be approved by the European Commission - subsequent versions
  ~ of the EUPL (the "Licence");
  ~
  ~ You may not use this work except in compliance with the Licence.
  ~ You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
  ~
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ European Union Public Licence for more details.
  --%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="page"/>
<!doctype html>
<fmt:setBundle basename="messages" scope="session"/>
<html>
<head>
    <haku:meta/>
    <title><fmt:message key="virkailija.haku.hakijatiedot"/></title>
    <script src="${contextPath}/resources/jquery/jquery.min.js" type="text/javascript"></script>
    <script src="${contextPath}/resources/jquery/jquery-ui-1.8.23.custom.min.js" type="text/javascript"></script>
    <script src="${contextPath}/resources/javascript/virkailija.js" type="text/javascript"></script>
    <link href="${contextPath}/resources/css/virkailija.css" type="text/css" rel="stylesheet"/>
    <script type="text/javascript" src="/virkailija-raamit/apply-raamit.js"></script>
    <haku:ie9StyleFix/>
</head>
<body>
<script type="text/javascript">
    var page_settings = {
        contextPath: "${pageContext.request.contextPath}"
    }
</script>
<div id="viewport">
    <div id="overlay" style="display: none;"></div>
    <div id="wrapper">

        <table class="structural-table" style="margin-left: 0.625%;width:99.375%;">
            <tbody>
            <tr>
                <td>
                    <jsp:include page="searchOrg.jsp"/>
                </td>
                <td>
                    <jsp:include page="searchForm.jsp"/>
                </td>
            </tr>
            </tbody>
        </table>
    </div>
</div>

</body>
</html>
