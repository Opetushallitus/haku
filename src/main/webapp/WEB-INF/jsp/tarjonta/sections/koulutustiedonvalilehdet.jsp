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
<div class="tabs">
    <a href="#" data-tabs-group="applicationtabs" data-tabs-id="kuvaus" class="tab current"><span><fmt:message
            key="tarjonta.koulutuksenkuvaus"/></span></a>
    <a href="#" data-tabs-group="applicationtabs" data-tabs-id="hakeutuminen" class="tab"><span><fmt:message
            key="tarjonta.koulutukseenhakeutuminen"/></span></a>
    <a href="#" data-tabs-group="applicationtabs" data-tabs-id="opiskelupaikka" class="tab">
        <span><fmt:message key="tarjonta.oppilaitos"/></span></a>
</div>

<div class="tabsheets">

    <jsp:include page="koulutuksenkuvaus.jsp"/>
    <jsp:include page="koulutukseenhakeutuminen.jsp"/>
    <jsp:include page="opiskelupaikka.jsp"/>

</div>
