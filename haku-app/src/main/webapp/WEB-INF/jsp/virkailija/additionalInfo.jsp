<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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

<!DOCTYPE html>
<fmt:setBundle basename="messages" scope="application"/>
<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="page"/>
<c:set var="additionalInfo" value="${it.application.additionalInfo}" scope="request"/>
<html>
<head>
    <haku:meta/>
    <haku:icons contextPath="${contextPath}"/>
    <link rel="stylesheet"
          href="${contextPath}/resources/jquery-ui-theme/jquery-ui-1.8.23.custom.css"
          type="text/css">
    <link href="${contextPath}/resources/css/virkailija.css" type="text/css" rel="stylesheet"/>
    <script src="${contextPath}/resources/jquery/jquery.min.js"></script>
    <script src="${contextPath}/resources/jquery/jquery-ui-1.8.23.custom.min.js"></script>
    <script src="${contextPath}/resources/javascript/virkailija/application.js" type="text/javascript"></script>
    <script type="text/javascript" src="/virkailija-raamit/apply-raamit.js"></script>
    <title><fmt:message key="virkailija.otsikko"/></title>
    <haku:ie9StyleFix/>

</head>
<body>
<div id="app-container" class="virkailija">
    <section class="grid16-16 margin-top-2">
        <form class="form" method="post">
            <fieldset>
                <button class="save" type="submit"><span><span><fmt:message key="lomake.button.save"/></span></span>
                </button>
                <legend class="h3">Syötettävät tiedot</legend>
                <hr/>
                <div id="extra-data">
                    <c:forEach var="data" items="${additionalInfo}">
                        <div class="form-row">
                            <input type="text" placeholder="Avain" value="${data.key}" class="extra-key-input"/>
                            <input type="text" placeholder="Arvo" name="${data.key}" value="${data.value}"
                                   class="margin-horizontal-4"/>
                            <button class="remove_key_value_button remove" type="button"><span>Poista</span>
                            </button>
                        </div>
                    </c:forEach>
                </div>
                <div class="clear"></div>
                <button id="add_key_value_button" class="blueplus" type="button"><span>Lisää tieto</span></button>
            </fieldset>
        </form>
    </section>
</div>
</body>
</html>
