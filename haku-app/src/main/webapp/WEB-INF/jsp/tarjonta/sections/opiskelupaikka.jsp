<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
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
<fmt:setBundle basename="messages"/>
<section id="opiskelupaikka" class="tabsheet" data-tabs-group="applicationtabs" data-tabs-id="opiskelupaikka">

    <div class="clear"></div>
    <img src="${pageContext.request.contextPath}/content/bulevardi31.png"/>
    <legend class="h3"><c:out value="${it.searchResult['LOPInstitutionInfoName']}"/></legend>
    <a href="<c:out value="${it.searchResult['LOIWebLinkHOMEUri']}" />"><c:out
            value="${it.searchResult['LOIWebLinkHOMELabel']}"/></a>

    <p><c:out value="${it.searchResult['LOPInstitutionInfoGeneralDescription']}"/></p>

</section>
